import { useEffect } from "react";
import { useNavigate, useParams, useSearchParams } from "react-router-dom";

function PaymentFail() {
    const [paymentParams] = useSearchParams();
    const navigate = useNavigate();
    
    const code = paymentParams.get("code")
    const message = paymentParams.get("message")

    useEffect(() => {
        
        alert(`결제에 실패하였습니다.`)

        navigate('/points/charge', {replace: true})
    }, [message, code, navigate])

    return (
        <div></div>   
    )
}

export default PaymentFail