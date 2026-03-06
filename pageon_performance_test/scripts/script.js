import http from 'k6/http';
import { check, sleep } from 'k6';
import { SharedArray } from 'k6/data';
import papaparse from 'https://jslib.k6.io/papaparse/5.1.1/index.js';


const tokenData = new SharedArray('user tokens', function () {
    // tokens.csv는 헤더가 있음 (userId, token)
    return papaparse.parse(open('./tokens.csv'), { header: true }).data;
});

// 1. CSV 데이터 로드 및 전처리
const contentData = new SharedArray('episode boundaries', function () {
    const data = papaparse.parse(open('./episode_data.csv'), { header: true }).data;
    
    // URL 형식에 맞게 contentType 미리 변환 (성능 최적화)
    return data.map(item => {
        let urlType = '';
        if (item.contentType === 'WEBNOVEL') urlType = 'webnovels';
        else if (item.contentType === 'WEBTOON') urlType = 'webtoons';
        
        return {
            contentId: item.contentId,
            urlType: urlType,
            minEp: parseInt(item['min(episodeId)']),
            maxEp: parseInt(item['max(episodeId)'])
        };
    });
});

export const options = {
    scenarios: {
        realistic_webtoon_load: {
            executor: 'constant-arrival-rate',
            rate: 50, 
            timeUnit: '1s',
            duration: '10m',
            preAllocatedVUs: 100,
            maxVUs: 1000,
        },
    },
};

// 로컬 개발 환경 주소
const BASE_URL = 'http://localhost:8080/api';

const commentList = [
    "와, 이 복선이 여기서 회수된다고요? 작가님은 다 계획이 있으시구나.",
    "오늘 연출 소름 돋았어요. BGM이랑 같이 보니까 영화 한 편 본 느낌.",
    "문장 하나하나가 보석 같아요. 웹소설 보다가 필사하고 싶어진 건 처음입니다.",
    "작화 퀄리티 실화인가요? 매 컷이 다 일러스트 수준이에요.",
    "스토리 빌드업 미쳤다... 초반부터 정주행 다시 하러 갑니다.",
    "작가님, 여기서 끊으시면 저는 일주일을 어떻게 버티나요...",
    "포인트 충전했습니다. 다음 화 빨리 가져와 주세요. 현기증 난단 말이에요.",
    "이게 무료분이라니... 유료분까지 다 결제했는데 다음 화가 없다니요!",
    "작가님 손목 괜찮으세요? 제 손목이라도 빌려드리고 싶네요. 더 그려주세요.",
    "방금 본 게 꿈인가요? 왜 마지막에 '다음 화에 계속'이 있는 거죠?",
    "오늘 유독 짧게 느껴지는 건 기분 탓인가요? 1초 만에 다 읽은 듯.",
    "댓글 보러 왔다가 저랑 똑같은 생각 하시는 분들 많아서 소름ㅋㅋㅋ",
    "부모님, 저를 이 시대에 태어나게 해주셔서 감사합니다. 이 작품을 보다니.",
    "내 통장 비번 알려줄 테니까 작가님이 알아서 인출해가세요. 제발 연참 좀...",
    "완결 날까 봐 벌써부터 무서워요. 작가님 평생 연재해 주세요. (진지)"
]
export default function () {
    // 임의의 유저(1~10만) 및 콘텐츠 선택
    const userRow = tokenData[(__VU - 1) % tokenData.length];
    const authToken = userRow.token;

    const content = contentData[Math.floor(Math.random() * contentData.length)];
    
    // 해당 작품의 에피소드 범위 내에서 랜덤 선택
    const episodeId = Math.floor(Math.random() * (content.maxEp - content.minEp + 1)) + content.minEp;
    
    const params = {
        headers: {
            'Authorization': `Bearer ${authToken}`,
            'Content-Type': 'application/json',
        },
    };

    // --- [액션 1] 작품 관심 등록 (구독 무관) ---
    // 전체 요청의 10% 비율로 발생
    if (Math.random() < 0.1) {
        http.post(`${BASE_URL}/${content.urlType}/${content.contentId}/interests`, null, params);
    }

    // --- [액션 2] 구독 상태 확인 ---
    const subRes = http.get(`${BASE_URL}/${content.urlType}/${content.contentId}/episodes/${episodeId}/subscribe`, params);
    let isSubscribed = (subRes.body === 'true'); // API가 true/false 평문을 반환한다고 가정
    let justPurchased = false;

    // --- [액션 3] 미구독 시 구매/대여 ---
    if (!isSubscribed) {
        let purchaseType = 'OWN'; // 기본값
        if (content.contentType === 'WEBTOON') {
            purchaseType = Math.random() < 0.7 ? 'RENT' : 'OWN';
        }

        const buyRes = http.post(
            `${BASE_URL}/${content.urlType}/${content.contentId}/episodes/${episodeId}/subscribe?purchaseType=${purchaseType}`, 
            null, 
            params
        );
        
        if (buyRes.status === 200) {
            isSubscribed = true;
            justPurchased = true;
        }
    }

    // --- [액션 4] 에피소드 기반 액션 (구독 상태일 때만) ---
    if (isSubscribed) {
        const actionType = Math.random();

        if (justPurchased || actionType < 0.6) {
            // 에피소드 읽기 (60%)
            http.get(`${BASE_URL}/${content.urlType}/${content.contentId}/episodes/${episodeId}`, params);
        } else if (actionType < 0.8) {
            // 에피소드 별점 (20%, RequestDto)
            const randomScore = Math.floor(Math.random() * (10 - 8 + 1)) + 8; // 8~10점

            const ratingPayload = JSON.stringify({
                score: randomScore
            });
            http.post(`${BASE_URL}/${content.urlType}/${content.contentId}/episodes/${episodeId}/rating`, ratingPayload, params);

        } else {
            // 에피소드 댓글 (20%, RequestDto)
            const commentPayload = JSON.stringify({
                text: commentList[Math.floor(Math.random() * commentList.length)],
                isSpoiler: Math.random() < 0.1 // 10% 확률로 스포일러 체크
            });
            http.post(`${BASE_URL}/${content.urlType}/${content.contentId}/episodes/${episodeId}/comments`, commentPayload, params);
        }
    }
}