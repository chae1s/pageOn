import { useEffect } from "react";
import { useNavigate, useParams, useSearchParams } from "react-router-dom";
import api from "../../api/axiosInstance";

function PaymentSuccess() {
    const [paymentParams] = useSearchParams();
    const navigate = useNavigate();
    useEffect(() => {

        const requestData = {
            paymentKey: paymentParams.get("paymentKey"),
            orderId: paymentParams.get("orderId"),
            amount:  paymentParams.get("amount")
        }

        async function PaymentConfirm() {
            try {

                await api.post('/payments/confirm', requestData)

                alert("결제가 완료되었습니다.")

                navigate("/points/charge", {replace: true})
                
            } catch (error: any) {
                
                const errorMessage = error.response.data.errorMessage;
                console.error("결제 승인 실패: ", errorMessage);
                
                alert("결제에 실패하였습니다.");
                navigate("/payment/fail")
            }
            
        }

        PaymentConfirm();
    
    }, [navigate]);

    return (
        <div>
            
        </div>
    )
}

export default PaymentSuccess