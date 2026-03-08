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

                const response = await api.post('/payments/confirm', requestData)

                if (response.data.success) {
                    alert(response.data.message)

                    navigate("/points/charge", {replace: true})
                } else {
                    navigate("/payment/fail")
                }
                
            } catch (error) {
                console.error("결제 승인 실패: ", error);
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