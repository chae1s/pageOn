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
    const data = papaparse.parse(open('./episodes_data.csv'), { header: true }).data;
    
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
            rate: 1000, 
            timeUnit: '1s',
            duration: '10m',
            preAllocatedVUs: 100,
            maxVUs: 1000,
        },
    },
};

// 로컬 개발 환경 주소
const BASE_URL = 'http://localhost:8080/api';

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
        http.post(`${BASE_URL}/interests/${content.contentId}`, null, params);
    }

    // --- [액션 2] 구독 상태 확인 ---
    const subRes = http.get(`${BASE_URL}/${content.urlType}/episodes/${episodeId}/subscribe`, params);
    let isSubscribed = subRes.body === 'true'; // API가 true/false 평문을 반환한다고 가정

    // --- [액션 3] 미구독 시 구매/대여 ---
    if (!isSubscribed) {
        let purchaseType = 'OWN'; // 기본값
        if (content.contentTypeEnum === 'WEBTOON') {
            purchaseType = Math.random() < 0.7 ? 'RENT' : 'OWN';
        }

        // 컨트롤러 구조 반영: /webtoons/episodes/{id}/subscribe?purchaseType=RENT
        const buyRes = http.post(
            `${BASE_URL}/${content.urlType}/episodes/${episodeId}/subscribe?purchaseType=${purchaseType}`, 
            null, 
            params
        );
        
        if (buyRes.status === 200) isSubscribed = true;
    }

    // --- [액션 4] 에피소드 기반 액션 (구독 상태일 때만) ---
    if (isSubscribed) {
        const actionType = Math.random();

        if (actionType < 0.6) {
            // 에피소드 읽기 (60%)
            http.get(`${BASE_URL}/${content.urlType}/episodes/${episodeId}`, params);
        } else if (actionType < 0.8) {
            // 에피소드 별점 (20%, RequestDto)
            const randomScore = Math.floor(Math.random() * (10 - 8 + 1)) + 8; // 8~10점

            const ratingPayload = JSON.stringify({
                contentType: content.contentTypeEnum,
                episodeId: episodeId,
                score: randomScore
            });
            http.post(`${BASE_URL}/rating`, ratingPayload, params);

        } else {
            // 에피소드 댓글 (20%, RequestDto)
            const commentPayload = JSON.stringify({
                text: "이 에피소드 정말 레전드네요! 꼭 보세요.",
                isSpoiler: Math.random() < 0.1 // 10% 확률로 스포일러 체크
            });
            http.post(`${BASE_URL}/${content.urlType}/episodes/${episodeId}/comments`, commentPayload, params);
        }
    }
}