import http from 'k6/http';
import { check, sleep } from 'k6';
import { SharedArray } from 'k6/data';
import { Trend, Rate } from 'k6/metrics';
import papaparse from 'https://jslib.k6.io/papaparse/5.1.1/index.js';

const contentDetailDuration = new Trend('content_detail_duration');
const episodesDuration = new Trend('episodes_duration');
const errorRate = new Rate('error_rate');

const contentData = new SharedArray('episode boundaries', function () {
    const data = papaparse.parse(open('./wed_contents.csv'), { header: true }).data;
    
    // URL 형식에 맞게 contentType 미리 변환 (성능 최적화)
    return data.map(item => {
        let urlType = '';
        if (item.contentType === 'WEBNOVEL') urlType = 'webnovels';
        else if (item.contentType === 'WEBTOON') urlType = 'webtoons';
        
        return {
            contentId: item.contentId,
            urlType: urlType
        };
    });
});

const tokenData = new SharedArray('user tokens', function () {
    const results = papaparse.parse(open('./tokens.csv'), { 
        header: true, 
        skipEmptyLines: true, // 빈 줄 무시
        quoteChar: '"'        // 따옴표 명시
    }).data;

    // 점(.)이 정확히 2개 포함된 "진짜 토큰"만 필터링해서 메모리에 올림
    return results.filter(item => 
        item.token && (item.token.match(/\./g) || []).length === 2
    );
});

const BASE_URL = 'http://localhost:8080/api';

export const options = {
  stages: [
    { duration: '1m', target: 200 },  // 200명까지 서서히 증가
    { duration: '3m', target: 200 },  // 200명 유지 (안정성 구간)
    { duration: '1m', target: 0 },    // 종료
  ],
  thresholds: {
    http_req_duration: ['p(95)<200'], // 95% 요청은 200ms 이내 응답해야 함
    content_detail_duration: ['p(95)<200'],
    episodes_duration: ['p(95)<200'],
    error_rate: ['rate<0.01'],
  },
};

export default function () {
  const userRow = tokenData[Math.floor(Math.random() * tokenData.length)];
  const authToken = userRow.token;

  const rand = Math.random(); 
  let index;

  const params = {
        headers: {
            'Authorization': `Bearer ${authToken}`,
            'Content-Type': 'application/json',
        },
    };

  if (rand < 0.8) {
    
    index = Math.floor(Math.random() * 10541); 
  } else {
    
    const min = 10541;
    const max = contentData.length - 1;
    index = Math.floor(Math.random() * (max - min + 1)) + min;
  }

const content = contentData[index];
const responses = http.batch([
    {
      method: 'GET',
      url: `${BASE_URL}/${content.urlType}/${content.contentId}`,
      params: { ...params, tags: { name: 'content-detail' } },
    },
    {
      method: 'GET',
      url: `${BASE_URL}/${content.urlType}/${content.contentId}/episodes?sort=recent&page=0&size=20`,
      params: { ...params, tags: { name: 'episodes' } },
    },
  ]);

  const [detailRes, episodesRes] = responses;

  // 커스텀 메트릭 기록
  contentDetailDuration.add(detailRes.timings.duration);
  episodesDuration.add(episodesRes.timings.duration);

  const ok = check(detailRes, {
    'detail status 200': (r) => r.status === 200,
  }) && check(episodesRes, {
    'episodes status 200': (r) => r.status === 200,
  });

  errorRate.add(!ok);
  
  sleep(0.1); // 요청 간 짧은 휴지기
}