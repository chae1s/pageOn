import { AuthProvider } from './context/AuthContext';
import { BrowserRouter } from 'react-router-dom';
import Router from './Router';

function App() {
  return (
    <AuthProvider>
        <BrowserRouter>
            <Router />
        </BrowserRouter>
    </AuthProvider>

  )
}

export default App;