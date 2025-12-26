import styled, { css } from "styled-components";
import { Link } from "react-router-dom";


export const ContentListWrapper = styled.div`
  position: relative;
  width: 100%;
  overflow: hidden;
`;

export const ContentList = styled.div`
  display: flex;
  gap: 12px;
  flex-wrap: nowrap;
  transition: transform 0.3s ease-in-out;
  will-change: transform;
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

export const ContentItem = styled.div`
  background: #fff;
  border-radius: 8px;
  width: 163px;
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  margin: 0 auto;
  flex-shrink: 0;
`;

export const ContentImageCover = styled(Link)`
  width: 100%;
  height: 224px;
  border-radius: 4px;
  overflow: hidden;
`;

export const ContentImage = styled.img`
  width: 100%;
  height: 100%;
  object-fit: cover;
`;

export const ContentInfoCover = styled.div`
  margin-top: 10px;
  text-align: left;
  width: 100%;
  width: 163px;
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
`;

export const ContentSeparate = styled.div`

`

export const ContentInfoWrapper = styled.div`
    display: inline-flex;
    font-size: 14px;
    color: #666;
    line-height: 19px;
    margin-top: 5px;
`

export const ContentType = styled.div`
    
`