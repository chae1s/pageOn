import { AuthProvider } from './context/AuthContext';
import { BrowserRouter } from 'react-router-dom';
import Router from './Router';
import ScrollToTop from './components/ScrollToTop';

function App() {
  return (
    <AuthProvider>
        <BrowserRouter>
            <ScrollToTop />
            <Router />
        </BrowserRouter>
    </AuthProvider>

  )
}

export default App;