import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
  vus: 100, 
  duration: '1m', // 1분간 지속
};

export default function () {
  // DB 조회가 발생하는 API 호출 (예: 웹툰 상세 조회)
  const res = http.get('http://localhost:8080/api/webnovels/1');
  
  check(res, {
    'is status 200': (r) => r.status === 200,
  });
  
  sleep(0.1); // 요청 간 짧은 휴지기
}