import React from 'react';
import * as S from '../Styles/PurchaseModal.styles';
import { EpisodeSummary } from '../../types/Episodes';

export type PurchaseModalMode = 'OWN' | 'RENT' | 'SELECT';

interface PurchaseModalProps {
    isOpen: boolean;
    onClose: () => void;
    onConfirm: (purchaseType: 'OWN' | 'RENT') => void;
    contentTitle: string;
    episode: EpisodeSummary | null;
    mode: PurchaseModalMode;
    allowRent: boolean;
    isWebnovelType: boolean; // 웹소설 여부 (구매 버튼만 노출)
}

function PurchaseModal({
    isOpen,
    onClose,
    onConfirm,
    contentTitle,
    episode,
    mode,
    allowRent,
    isWebnovelType
}: PurchaseModalProps) {
    if (!episode) return null;

    // 제목 계산
    const modalTitle = mode === 'RENT' 
        ? `${contentTitle} ${episode.episodeNum}화`
        : `${contentTitle} ${episode.episodeNum}화`;

    // 포인트 포맷팅
    const formatPointValue = (value?: number | null) => {
        return typeof value === 'number' ? value.toLocaleString() : '0';
    };

    // 설명 텍스트 계산
    const getModalDescription = () => {
        const purchaseText = `구매금액 ${formatPointValue(episode.purchasePrice)}P`;
        const rentText = `대여금액 ${formatPointValue(episode.rentalPrice)}P`;

        if (mode === 'OWN') return purchaseText;
        if (mode === 'RENT') return rentText;

        return allowRent ? (
            <>
                {purchaseText} · {rentText}
                <br />
                원하는 이용 방식을 선택해주세요.
            </>
        ) : (
            `${purchaseText}. 구매 후 열람할 수 있습니다.`
        );
    };

    return (
        <S.PurchaseModalOverlay $open={isOpen} onClick={onClose}>
            <S.PurchaseModal onClick={(e) => e.stopPropagation()}>
                <S.PurchaseModalTitle>{modalTitle}</S.PurchaseModalTitle>
                <S.PurchaseModalDescription>{getModalDescription()}</S.PurchaseModalDescription>
                <S.PurchaseModalActions>
                    {/* 웹소설: 구매 버튼만 노출 */}
                    {isWebnovelType ? (
                        <S.PurchaseBtn type="button" onClick={() => onConfirm('OWN')}>
                            구매
                        </S.PurchaseBtn>
                    ) : (
                        // 웹툰 등: 대여/구매 선택 로직
                        <>
                            {/* 대여 버튼: RENT 모드이거나, SELECT 모드이면서 대여가 허용된 경우 */}
                            {(mode === 'RENT' || (mode === 'SELECT' && allowRent)) && (
                                <S.RentalBtn type="button" onClick={() => onConfirm('RENT')}>
                                    대여
                                </S.RentalBtn>
                            )}
                            
                            {/* 구매 버튼: RENT 전용 모드가 아닐 때 노출 */}
                            {mode !== 'RENT' && (
                                <S.PurchaseBtn type="button" onClick={() => onConfirm('OWN')}>
                                    구매
                                </S.PurchaseBtn>
                            )}
                        </>
                    )}
                    <S.ModalGhostButton type="button" onClick={onClose}>
                        취소
                    </S.ModalGhostButton>
                </S.PurchaseModalActions>
            </S.PurchaseModal>
        </S.PurchaseModalOverlay>
    );
}

export default PurchaseModal;