import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import SignupEmail from './pages/SignupEmail';
import Home from './pages/Home';
import Signup from './pages/Signup'
import Login from './pages/Login'
import OAuthCallback from './pages/OAuthCallback'
import PasswordFind from './pages/PasswordFind'
import MyPage from './pages/MyPage';
import PasswordCheck from './pages/PasswordCheck';
import EditProfile from './pages/EditProfile';
import Withdraw from './pages/Withdraw';
import FavoriteWorks from './pages/FavoriteWorks';
import RecentViewedWorks from './pages/RecentViewedWorks';
import MyComments from './pages/MyComments';

function App() {
  return (
    <Router>
      <Routes>
        <Route path="/" element={<Home />} />
        <Route path='/users/signup' element={<Signup />} />
        <Route path="/users/signup/email" element={<SignupEmail />} />
        <Route path="/users/login" element={<Login />} />
        <Route path="oauth/callback" element={<OAuthCallback />} />
        <Route path="/users/find-password" element={<PasswordFind />} />
        <Route path="/users/my-page" element={<MyPage />} />
        <Route path="/users/password-check" element={<PasswordCheck />} />
        <Route path="/users/edit" element={<EditProfile />} />
        <Route path="/users/withdraw" element={<Withdraw />} />
        <Route path="/library/favorites" element={<FavoriteWorks />} />
        <Route path="/library/recent-viewed" element={<RecentViewedWorks />} />
        <Route path="/library/my-comments" element={<MyComments />} />
      </Routes>
    </Router>
  );
}

export default App;