import styled, { css } from "styled-components";
import { Link } from "react-router-dom";


export const ContentListWrapper = styled.div<{ $layout: 'grid' | 'slider' }>`
  position: relative;
  width: 100%;
  overflow: hidden;
`;

export const ContentList = styled.div<{ $layout: 'grid' | 'slider' }>`
  ${({ $layout }) =>
    $layout === 'grid'
      ? css`
          display: grid;
          grid-template-columns: repeat(6, 1fr);
          gap: 12px;
        `
      : css`
          display: flex;
          gap: 12px;
          flex-wrap: nowrap;
          transition: transform 0.3s ease-in-out;
          will-change: transform;
        `}
`;

export const ContentListArrow = styled.button<{ direction: 'left' | 'right' }>`
  ${({ direction }) => (direction === 'left' ? 'left: 0;' : 'right: 0;')}
  position: absolute;
  top: 130px; /* 이미지(260px)의 중간 */
  transform: translateY(-80%);
  z-index: 3;
  background: rgba(255,255,255,0.8);
  border: 1px solid #ccc;
  border-radius: 50%;
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.5rem;
  cursor: pointer;
  box-shadow: 0 2px 8px rgba(0,0,0,0.08);
  pointer-events: auto;
`;

export const ContentListEmptyMsg = styled.p`
  width: 100%;
  text-align: center;
`;

export const ContentItem = styled.div<{ $layout: 'grid' | 'slider' }>`
  background: #fff;
  border-radius: 8px;
  padding: ${({ $layout }) => ($layout === 'grid' ? '12px 8px' : '0')};
  width: ${({ $layout }) => ($layout === 'slider' ? '163px' : '120px')};
  display: flex;
  flex-direction: column;
  align-items: ${({ $layout }) => ($layout === 'grid' ? 'center' : 'flex-start')};
  margin: 0 auto;

  ${({ $layout }) =>
    $layout === 'slider' &&
    css`
      flex-shrink: 0;
    `}
`;

export const ContentImageCover = styled(Link)<{ $layout: 'grid' | 'slider' }>`
  width: 100%;
  height: ${({ $layout }) => ($layout === 'slider' ? '224px' : '150px')};
  border-radius: 4px;
  overflow: hidden;
`;

export const ContentImage = styled.img`
  width: 100%;
  height: 100%;
  object-fit: cover;
`;

export const ContentInfoCover = styled.div<{ $layout: 'grid' | 'slider' }>`
  margin-top: ${({ $layout }) => ($layout === 'slider' ? '10px' : '8px')};
  text-align: left;
  width: 100%;
  width: ${({ $layout }) => ($layout === 'slider' ? '163px' : '104px')};
  display: flex;
  flex-direction: column;
  overflow: hidden;
  min-width: 0;
`;

export const ContentTitle = styled(Link)`
  display: inline-block;
  font-size: 1rem;
  font-weight: 600;
  color: #222;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
`;

export const ContentAuthor = styled.div`
  font-size: 0.875rem;
  color: #888;
  margin-top: 6px;
`;
