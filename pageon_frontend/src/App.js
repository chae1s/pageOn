import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import SignupEmail from './pages/SignupEmail';
import Home from './pages/Home';
import Signup from './pages/Signup'
import Login from './pages/Login'
import OAuthCallback from './pages/OAuthCallback'
import PasswordFind from './pages/PasswordFind'

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
      </Routes>
    </Router>
  );
}

export default App;