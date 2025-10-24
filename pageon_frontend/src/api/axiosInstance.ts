import { rejects } from "assert";
import axios, {AxiosError, AxiosInstance, AxiosRequestConfig, InternalAxiosRequestConfig} from "axios";
import { error } from "console";
import { resolve } from "path";


const api: AxiosInstance = axios.create({
    baseURL: '/api',
    withCredentials: true,
    headers: {
        'Content-Type': 'application/json',
    },
})

api.interceptors.request.use(
    (config) => {
        const accessToken = localStorage.getItem('accessToken');
        
        if (accessToken && config.headers) {
            config.headers.Authorization = `Bearer ${accessToken}`
        }

        return config;
    },
    (error) => {
        return Promise.reject(error)
    }
);

// 토큰 갱신 중복 요청 방지 위한 변수 및 큐
let isRefreshing = false;
let failedQueue: Array<{ resolve: (value: any) => void; reject: (reason: any) => void; }> = [];

const processQueue = (error: any, token: string | null = null) => {
    failedQueue.forEach(prom => {
        if (error) {
            prom.reject(error);
        } else {
            prom.resolve(token);
        }
    });

    failedQueue = [];
}


api.interceptors.response.use(
    (response) => {
        // 2XX 응답은 그대로 통과
        return response;
    },

    async (error) => {
        const originalRequest = error.config as InternalAxiosRequestConfig & {_retry?:boolean};

        if (error.response?.status === 401 && !originalRequest._retry) {

            if (isRefreshing) {
                // 이미 갱신 요청 진행 중이면, 현재 요청을 큐에 추가
                return new Promise((resolve, reject) => {
                    failedQueue.push({resolve, reject});
                })
                .then(token => {
                    if (originalRequest.headers) {
                        originalRequest.headers.Authorization = `Bearer ${token}`;
                    }

                    return api(originalRequest);
                })
            }

            // 이 요청이 첫 번째 401 요청인 경우
            originalRequest._retry = true;
            isRefreshing = true;

            try {
                const response = await axios.post("/api/auth/refresh", {}, {
                    withCredentials: true
                });

                const newAccessToken = response.data.accessToken;
                if (response.data.isRefreshed) {
                    localStorage.setItem("accessToken", newAccessToken)
                }

                api.defaults.headers.common['Authorization'] = `Bearer ${newAccessToken}`;

                if (originalRequest.headers) {
                    originalRequest.headers.Authorization = `Bearer ${newAccessToken}`;
                }

                return api(originalRequest);
            } catch (_error: any) {
                console.log("Refresh Token Failed: ", _error);

                localStorage.removeItem("accessToken");

                window.location.href = "/users/login"

                return Promise.reject(_error);
            } finally {
                isRefreshing = false;
            }
            
        } 

        return Promise.reject(error);
    }

)
export default api;