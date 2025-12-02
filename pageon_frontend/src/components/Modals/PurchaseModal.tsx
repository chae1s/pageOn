import React from "react";
import { PurchaseTargetEpisode } from "../../types/Episodes";
import * as S from '../Styles/PurchaseModal.styles';
import crossIcon from '../../assets/crossIcon.png';

interface PurchaseModalProps {
    isOpen: boolean;
    onClose: () => void;
    onConfirm: (purchaseType: 'OWN' | 'RENT') => void;
    contentTitle: string;
    episode: PurchaseTargetEpisode | null;
    mode: PurchaseModalMode;
    isWebnovelType: boolean;
    allowRent: boolean;
}

export type PurchaseModalMode = 'OWN' | 'RENT' | 'SELECT';

function PurchaseModal({
    isOpen,
    onClose,
    onConfirm,
    contentTitle,
    episode,
    mode,
    isWebnovelType,
    allowRent
}: PurchaseModalProps) {

    if (!episode) return null;

    const modalTitle = `${contentTitle} ${episode.episodeNum}화`

    const formatPointValue = (value?: number | null) => {
        return typeof value === 'number' ? value.toLocaleString() : '0';
    };
    
    const getModalDescrption = () => {
        const purchaseText = `구매금액 ${formatPointValue(episode.purchasePrice)}P`
        const rentText = `대여금액 ${formatPointValue(episode.rentalPrice)}P`

        if (mode === 'OWN') return purchaseText;
        if (mode === 'RENT') return rentText;

        return allowRent ? (
            <>
                {purchaseText} · {rentText}
                <br />
                원하는 구매 방식을 선택해주세요.
            </>
        ) : (
            <>
                {purchaseText}
            </>
        )
    }

    return (
        <S.PurchaseModalOverlay $open = {isOpen} onClick={onClose}>
            <S.PurchaseModal onClick={(e) => e.stopPropagation()}>
                <S.CloseButton type="button" aria-label="닫기" onClick={onClose}>
                    <img src={crossIcon} alt="닫기" />
                </S.CloseButton>
                <S.PurchaseModalTitle>{modalTitle}</S.PurchaseModalTitle>
                <S.PurchaseModalDescription>{getModalDescrption()}</S.PurchaseModalDescription>
                <S.PurchaseModalActions>
                    {isWebnovelType ? (
                        <S.PurchaseBtn type="button" onClick={() => onConfirm('OWN')}>
                            구매
                        </S.PurchaseBtn>
                    ) : (
                        <>
                            {(mode === 'RENT' || (mode === 'SELECT' && allowRent)) && (
                                <S.RentalBtn type="button" onClick={() => onConfirm('RENT')}>
                                    대여
                                </S.RentalBtn>
                            )}

                            {mode !== 'RENT' && (
                                <S.PurchaseBtn type="button" onClick={() => onConfirm('OWN')}>
                                    구매
                                </S.PurchaseBtn>
                            )}
                        </>
                    )}
                </S.PurchaseModalActions>

            </S.PurchaseModal>
        </S.PurchaseModalOverlay>
    )

}

export default PurchaseModal;