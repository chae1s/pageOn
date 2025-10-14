import { Route, Routes, useLocation } from "react-router-dom";
import PrivateRoute from "./components/Routes/PrivateRoute";
import RoleRoute from "./components/Routes/RoleRoute";
import PublicOnlyRoute from "./components/Routes/PublicOnlyRoute";
import GlobalStyle from "./styles/Global.styles";
import Home from "./pages/Home/Home"
import Header from "./components/Headers/Header";
import CreatorHeader from "./components/Headers/CreatorHeader";
import Footer from "./components/Footer";
import Login from "./pages/Users/Login";
import Signup from "./pages/Users/Signup";
import SignupEmail from "./pages/Users/SignupEmail";
import PasswordFind from "./pages/Users/PasswordFind";
import MyPage from "./pages/Users/MyPage";
import PasswordCheck from "./pages/Users/PasswordCheck";
import EditProfile from "./pages/Users/EditProfile";
import Withdraw from "./pages/Users/Withdraw";
import FavoriteWorks from "./pages/Users/FavoriteWorks";
import RecentViewedWorks from "./pages/Users/RecentViewedWorks";
import MyComments from "./pages/Users/MyComments";
import CreatorRegister from "./pages/Creators/CreatorRegister";
import MockVerify from "./pages/Users/MockVerify";
import CreatorDashbord from "./pages/Creators/CreatorDashbord";
import WebnovelHome from "./pages/Home/WebnovelHome";
import WebtoonHome from "./pages/Home/WebtoonHome";
import WebnovelDetailPage from "./pages/Contents/WebnovelDetailPage";
import WebtoonDetailPage from "./pages/Contents/WebtoonDetailPage ";
import WebnovelViewer from "./pages/Contents/WebnovelViewer";

function Router() {
    const location = useLocation();
    const hideHeaderFooter =
        location.pathname === "/mock-verify" ||
        /^\/webnovels\/[^/]+\/viewer\/[^/]+$/.test(location.pathname) ||
        /^\/webtoons\/[^/]+\/viewer\/[^/]+$/.test(location.pathname);

    const creatorHeader = location.pathname.startsWith("/creators");
    const isAuthenticated = !!localStorage.getItem("accessToken");

    return (
        <>
        <GlobalStyle/>
        {!hideHeaderFooter && !creatorHeader && <Header></Header>}
        {!hideHeaderFooter && creatorHeader && <CreatorHeader/>}
        <Routes>
            <Route path="/" element={<Home />} />
            <Route path="/webnovels" element={<WebnovelHome />} />
            <Route path="/webtoons" element={<WebtoonHome />} />
            <Route path="/webnovels/:contentId" element={<WebnovelDetailPage />} />
            <Route path="/webtoons/:contentId" element={<WebtoonDetailPage />} />
            <Route element={<PublicOnlyRoute/>} >
                <Route path="/users/login" element={<Login />} />
                <Route path="/users/signup" element={<Signup />} />
                <Route path="/users/signup/email" element={<SignupEmail />} />
                <Route path="/users/find-password" element={<PasswordFind />} />
            </Route>
            
            <Route element={<PrivateRoute/>} >
                <Route path="/users/my-page" element={<MyPage />} />
                <Route path="/users/check-password" element={<PasswordCheck />} />
                <Route path="/users/edit" element={<EditProfile />} />
                <Route path="/users/withdraw" element={<Withdraw />} />
                <Route path="/library/favorites" element={<FavoriteWorks />} />
                <Route path="/library/recent-view" element={<RecentViewedWorks />} />
                <Route path="/library/my-comments" element={<MyComments />}  />
                <Route path="/creators/register" element={<CreatorRegister/>}  />
                <Route path="/mock-verify" element={<MockVerify/>}  />
                <Route path="/webnovels/:contentId/viewer/:episodeId" element={<WebnovelViewer />} />
            </Route>

            <Route element={<RoleRoute allowedRoles={["ROLE_CREATOR"]}/>}>
                <Route path="/creators/dashboard" element={<CreatorDashbord/>}  />
            </Route>
            
            
        </Routes>
        {!hideHeaderFooter && <Footer></Footer>}
        </>
    )
}

export default Router;