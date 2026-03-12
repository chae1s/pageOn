import { Navigate, Outlet, useLocation } from "react-router-dom";
import { useAuth } from "../../context/AuthContext";

const PublicOnlyRoute = () => {
    const { isAuthenticated } = useAuth();
    const location = useLocation();

    
    const from = location.state?.from || "/";

    
    return isAuthenticated ? <Navigate to={from} replace /> : <Outlet />;
};
export default PublicOnlyRoute;