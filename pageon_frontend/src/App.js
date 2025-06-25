import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import SignupEmail from './pages/SignupEmail';
import Home from './pages/Home';
import Signup from './pages/Signup'
import SignupSocial from './pages/SignupSocial'
import Login from './pages/Login'

function App() {
  return (
    <Router>
      <Routes>
        <Route path="/" element={<Home />} />
        <Route path='/users/signup' element={<Signup />} />
        <Route path="/users/signup/email" element={<SignupEmail />} />
        <Route path="/users/signup/social" element={<SignupSocial />} />
        <Route path="/users/login" element={<Login />} />
      </Routes>
    </Router>
  );
}

export default App;