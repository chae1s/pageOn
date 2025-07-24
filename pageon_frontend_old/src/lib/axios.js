import axios from 'axios';

const instance = axios.create({
  baseURL: process.env.REACT_APP_API_URL || 'http://localhost:8080',
  withCredentials: true, // refreshToken 쿠키 전송을 위해 필요
});

// ✅ 요청 시 accessToken 자동 추가
instance.interceptors.request.use(
  config => {
    const accessToken = localStorage.getItem('accessToken');
    if (accessToken) {
      config.headers.Authorization = `Bearer ${accessToken}`;
    }
    return config;
  },
  error => Promise.reject(error)
);

// ✅ 응답 시 accessToken 만료 처리 및 재요청
instance.interceptors.response.use(
  response => response,
  async error => {
    const originalRequest = error.config;

    // accessToken 만료로 인해 401 발생 && 아직 재시도 안했으면
    if (error.response?.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true;

      try {
        // ✅ refreshToken은 쿠키(HttpOnly)로 보내므로 헤더에 붙일 필요 없음
        const res = await axios.post(
          `${process.env.REACT_APP_API_URL || 'http://localhost:8080'}/api/auth/reissue`,
          null,
          { withCredentials: true }
        );

        const jwtDto = res.data.success;

        if (jwtDto && jwtDto.isLogin) {
          localStorage.setItem('accessToken', jwtDto.accessToken);
          localStorage.setItem('provider', jwtDto.provider);

          // ✅ 재요청에 새 accessToken 반영
          originalRequest.headers.Authorization = `Bearer ${jwtDto.accessToken}`;
          return instance(originalRequest);
        }
      } catch (e) {
        console.error('🔒 토큰 재발급 실패:', e);
        // 필요 시 로그아웃 처리
        localStorage.removeItem('accessToken');
        localStorage.removeItem('provider');
        window.location.href = '/login'; // 또는 navigate
      }
    }

    return Promise.reject(error);
  }
);

export default instance;
