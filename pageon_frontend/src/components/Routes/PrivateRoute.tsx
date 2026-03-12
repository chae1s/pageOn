import { Navigate, Outlet, useLocation } from "react-router-dom";
import { useAuth } from "../../context/AuthContext";

const PrivateRoute = () => {
    const {isAuthenticated} = useAuth();
    const location = useLocation();

    return isAuthenticated ? (
        <Outlet />
    ) : (
        <Navigate 
            to="/users/login" 
            replace 
            state={{ from: location.pathname }} 
        />
    );
}

export default PrivateRoute;