import { useLocation, useNavigate } from 'react-router-dom';

export const isLoggedIn = (): boolean => {
    return !!localStorage.getItem('accessToken');
};

export const useAuthCheck = () => {
    const location = useLocation();
    const navigate = useNavigate();

    const checkLogin = (): boolean => {
        if (!isLoggedIn()) {
            alert('로그인이 필요합니다.');
            // 원래 있던 페이지의 경로를 state에 담아서 로그인 페이지로 보냅니다.
            navigate('/users/login', { state: { from: location.pathname } });
            return false; // 로그인 안 됨
        }
        return true; // 로그인 됨
    };

    // 컴포넌트에서 가져다 쓸 수 있도록 함수를 반환합니다.
    return { checkLogin };
};