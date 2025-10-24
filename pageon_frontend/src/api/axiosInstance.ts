import axios, {AxiosError, AxiosInstance, AxiosRequestConfig} from "axios";



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

export default api;