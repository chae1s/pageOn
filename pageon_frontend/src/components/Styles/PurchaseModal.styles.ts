import { styled } from "styled-components";


export const PurchaseModalOverlay = styled.div<{ $open: boolean }>`
    position: fixed;
    inset: 0;
    display: ${({ $open }) => ($open ? 'flex' : 'none')};
    align-items: center;
    justify-content: center;
    background: rgba(0, 0, 0, 0.4);
    z-index: 1600;
    padding: 20px;
`;

export const PurchaseModal = styled.div`
    width: 100%;
    max-width: 360px;
    padding: 32px 20px 24px;
    background: #fff;
    border-radius: 12px;
    box-shadow: 0 8px 32px rgba(0, 0, 0, 0.15);
    text-align: center;
    position: relative;
`;

export const PurchaseModalTitle = styled.h3`
    font-size: 18px;
    font-weight: 600;
    margin-bottom: 12px;
`;

export const PurchaseModalDescription = styled.p`
    font-size: 14px;
    color: #666;
    line-height: 1.5;
    margin-bottom: 20px;
    white-space: pre-wrap;
`;

export const PurchaseModalActions = styled.div`
    display: flex;
    gap: 10px;
    justify-content: center;
`;

export const CloseButton = styled.button`
    position: absolute;
    top: 12px;
    right: 12px;
    width: 32px;
    height: 32px;
    border: none;
    border-radius: 16px;
    background: transparent;
    color: #333;
    font-size: 18px;
    font-weight: 600;
    cursor: pointer;
    line-height: 1;
    display: flex;
    align-items: center;
    justify-content: center;

    &:hover {
        background: rgba(0, 0, 0, 0.05);
    }

    img {
        width: 16px;
        height: 16px;
        pointer-events: none;
    }
`;

export const ModalGhostButton = styled.button`
    display: inline-block;
    padding: 6px 12px;
    font-size: 14px;
    font-weight: 500;
    border-radius: 8px;
    border: 1px solid #d0d0d0;
    background: #fff;
    color: #5a5a5a;
    cursor: pointer;
`;

export const RentalBtn = styled.button`
    display: inline-block;
    padding: 6px 12px;
    font-size: 14px;
    font-weight: 500;
    border-radius: 8px;
    white-space: nowrap;
    line-height: 1;
    border: 1px solid #528efa;
    color: #528efa;
    background: #fff;
    cursor: pointer;
`;

export const PurchaseBtn = styled.button`
    display: inline-block;
    padding: 6px 12px;
    font-size: 14px;
    font-weight: 500;
    border-radius: 8px;
    white-space: nowrap;
    line-height: 1;
    background-color: #528efa;
    color: #FFF;
    border: 1px solid #528efa;
    cursor: pointer;
`;

