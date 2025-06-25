import { useEffect } from "react";
import { useNavigate, useLocation } from "react-router-dom";

function OAuthCallback() {
  const navigate = useNavigate();
  const location = useLocation();

  useEffect(() => {
    // 쿼리 파라미터에서 토큰 추출
    const params = new URLSearchParams(location.search);
    const accessToken = params.get("accessToken");
    
    if (accessToken != null) {
      localStorage.setItem("accessToken", accessToken);
    }

    
    // 홈으로 이동
    navigate("/", { replace: true });
  }, [location, navigate]);

  // 아무것도 렌더링하지 않음 (또는 로딩 메시지)
  return null;
}

export default OAuthCallback;
