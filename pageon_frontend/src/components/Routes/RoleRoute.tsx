import { Navigate, Outlet } from "react-router-dom";
import { useAuth } from "../../context/AuthContext";

const RoleRoute = ({allowedRoles} : {allowedRoles: string[]} ) => {
    const {roles} = useAuth();
    const hasAccess = allowedRoles.some(role => roles.includes(role));

    return hasAccess ? <Outlet /> : <Navigate to="/" replace />
}

export default RoleRoute