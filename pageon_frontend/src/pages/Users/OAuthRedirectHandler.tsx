import { useLocation, useNavigate, useSearchParams } from "react-router-dom";
import { useAuth } from "../../context/AuthContext";
import { useEffect } from "react";
import axios from "axios";

const OAuthRedirectHandler: React.FC = () => {
    const { login } = useAuth();
    const [searchParams] = useSearchParams();

    const navigate = useNavigate();
    const location = useLocation();

    useEffect(() => {
        const fetchToken = async () => {
            const userId = searchParams.get("userId");
            const tempCode = searchParams.get("tempCode");

            if (userId && tempCode) {
                try {
                    const tempRequest = {
                        userId: userId,
                        tempCode: tempCode,
                    };
                    const response = await axios.post("/api/auth/exchange", tempRequest);
                    
                    const jwtInfo = response.data;

                    if (jwtInfo && jwtInfo.isLogin) {
                        console.log(jwtInfo)
                        login(jwtInfo.accessToken, jwtInfo.userRoles, jwtInfo.oauthProvider);

                        const savedPath = localStorage.getItem("redirectPath") || "/";
                        localStorage.removeItem("redirectPath");

                        navigate(savedPath, { replace: true });
                    } else {
                        alert("로그인에 실패하였습니다.");
                        navigate("/users/login");
                    }
                } catch (error) {
                    console.log(error)
                    alert("로그인에 실패하였습니다.");
                    navigate("/users/login");
                }
            } else {
                alert("로그인에 실패하였습니다.");
                navigate("/users/login");
            }
        };

        fetchToken();
        
    }, []);

    return <div></div>;
};

export default OAuthRedirectHandler;
