import { createContext, useContext, useState, useEffect, ReactNode, Children } from "react";

interface AuthContextType {
    isAuthenticated: boolean;
    accessToken: string | null;
    roles: string[];
    login: (token:string, roles: string[], provider:string) => void;
    logout: () => void;
}

const Authcontext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider = ({children} : {children: ReactNode}) => {
    const [accessToken, setAccessToken] = useState<string | null>(localStorage.getItem("accessToken"));

    const [roles, setRoles] = useState<string[]>(() => {
        try {
            return JSON.parse(localStorage.getItem("roles") || "[]");
        } catch {
            return [];
        }
    });

    const isAuthenticated = !!accessToken;

    const login = (token:string, roles: string[], proivder:string) => {
        localStorage.setItem("accessToken", token);
        localStorage.setItem("roles", JSON.stringify(roles));
        localStorage.setItem("provider", proivder)
        setAccessToken(token);
        setRoles(roles);
    }

    const logout = () => {
        localStorage.removeItem("accessToken");
        localStorage.removeItem("provider");
        localStorage.removeItem("roles");
        setAccessToken(null);
        setRoles([]);
    }

    useEffect(() => {
        const handleStorage = () => {
            setAccessToken(localStorage.getItem("accessToken"));
            setRoles(JSON.parse(localStorage.getItem("roles") || "[]"));
        };

        window.addEventListener("storage", handleStorage);
        return () => window.removeEventListener("storage", handleStorage);
    }, []);

    return (
        <Authcontext.Provider value={{isAuthenticated, accessToken, roles, login, logout}}>
            {children}
        </Authcontext.Provider>
    )
}

export const useAuth = () => {
    const context = useContext(Authcontext);
    if (!context) throw new Error ("useAuth must be used within an AuthProvider");

    return context;
}