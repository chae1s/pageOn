import http from 'k6/http';
import { check, sleep } from 'k6';
import { SharedArray } from 'k6/data';
import papaparse from 'https://jslib.k6.io/papaparse/5.1.1/index.js';


const tokenData = new SharedArray('user tokens', function () {
    // tokens.csv는 헤더가 있음 (userId, token)
    return papaparse.parse(open('./tokens.csv'), { header: true }).data;
});

export let options = {
    vus: 1,
    duration: '1s'
}

export default function () {

    const userRow = tokenData[(__VU - 1) % tokenData.length];
    const authToken = userRow.token;

    const params = {
        headers: {
            'Authorization': `Bearer ${authToken}`,
            'Content-Type': 'application/json',
        },
    };

    const ratingPayload = JSON.stringify({
        score: 10
    });

    http.post(`http://localhost:8080/api/webnovels/1/episodes/10/rating`, ratingPayload, params);
}