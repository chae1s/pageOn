import { useLocation, useNavigate, useSearchParams } from "react-router-dom";
import { useAuth } from "../../context/AuthContext";
import { useEffect } from "react";

const OAuthRedirectHandler: React.FC = () => {

    const {login} = useAuth();
    const [searchParams] = useSearchParams();

    const navigate = useNavigate();
    const location = useLocation();

    useEffect(() => {
        const accessToken = searchParams.get("accessToken");
        const provider = searchParams.get("provider");
        let rolesArray = searchParams.getAll("userRoles");

        if (accessToken && provider && rolesArray.length > 0) {
            login(accessToken, rolesArray, provider);

            const savedPath = localStorage.getItem("redirectPath") || "/";
            localStorage.removeItem("redirectPath"); // 사용 후 반드시 삭제

            navigate(savedPath, { replace: true });
        } else {
            alert("로그인에 실패하였습니다.");
            navigate("/login");
        }
    }, []);

    return <div></div>
}

export default OAuthRedirectHandler;

