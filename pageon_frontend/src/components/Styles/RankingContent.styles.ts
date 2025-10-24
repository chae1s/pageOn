import styled from "styled-components";
import { Link } from "react-router-dom";

export const ContentSection = styled.section`
  margin-top: 40px;
  position: relative;
`;

export const ContentList = styled.div`
  display: flex;
  position: relative;
  overflow: hidden;
  width: 1040px;
  margin: 0 auto;
`;

export const ContentListContainer = styled.div<{ $layout: "grid" | "slider" }>`
    display: flex;
    justify-content: center;
    transition: ${({ $layout }) => ($layout === 'slider' ? 'transform 0.3s ease' : '')};
    will-change: transform;
    white-space: nowrap;
`;

export const ContentColumn = styled.div`
  width: calc(1040px / 3);
  
  display: flex;
  flex-direction: column;
  
  display: inline-block;
  vertical-align: top;
`;

export const ContentItem = styled.li`
  width: 100%;
  min-height: 116px;
  &:not(:nth-of-type(3n)) {
      margin-bottom: 6px;
  }
`;

export const ContentItemWrapper = styled.div`
  display: flex;
  flex-direction: row;
  justify-content: space-between;
`;

export const ContentImageContainer = styled.div`
  display: flex;
  flex-direction: column;
  align-items: stretch;
  justify-content: center;
  width: 80px;
`;

export const ContentImageWrapper = styled.div`
    position: relative;
    @media (min-width: 768px) {
        padding-bottom: 145%;
        width: auto;
        height: 100%;
    }
`;

export const ContentImageCover = styled.div`
    overflow: hidden;
    position: absolute;
    top: 50%;
    left: 0px;
    transform: translate(0px, -50%);
    width: 100%;
    border-radius: 4px;
    @media (min-width: 768px) {
        height: 100%;
    }
`;

export const ContentImage = styled.img`
    display: block;
    opacity: 1;
    position: absolute;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
`;

export const ContentRankWrapper = styled.div`
    flex: 0 0 80px;
    align-self: stretch;
    display: flex;
    justify-content: center;
    align-items: center;
    text-align: center;
`;

export const ContentRankNumber = styled.span`
    font-size: 18px;
    font-weight: 500;
    line-height: 21px;
    color: #444;
`;

export const ContentInfoWrapper = styled.div`
    width: 184px;
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: flex-start;
`;

export const ContentInfoSection = styled.div`
    width: 100%;
`;

export const ContentTitle = styled(Link)`
    overflow: hidden;
    text-overflow: ellipsis;
    overflow-wrap: break-word;
    white-space: normal
    line-height: 19px;
    word-break: break-all;
    font-size: 16px;
    font-weight: 500;
`;

export const ContentAuthorWrapper = styled.div`
    margin-top: 6px;
`;

export const ContentAuthor = styled.p`
    line-heigth: 17px;
    font-size: 15px;
    color: #888;
    display: block;
    white-space: nowrap;
    flex: 0 1 auto;
    min-width: 0px;
    overflow: hidden;
    text-overflow: ellipsis;
    overflow-wrap: break-word;
`;

export const ContentRatingSection = styled.div`
    display: flex;
    flex-direction: column;
    margin-top: 6px;
`;

export const ContentRatingScoreWrapper = styled.div`
    overflow-wrap: break-word;
    display: flex;
    position: relative;
    align-items: center;
    font-size: 13px;
`;

export const ContentRatingScore = styled.span`
    font-weight: 500;
    margin-right: 4px;
    font-size: 13px;
`;

export const ContentRatingCount = styled.span`
    margin-left: 3px;
    color: #888;
`;

export const PrevButton = styled.button`
    position: absolute;
    top: 130px; /* 이미지(260px)의 중간 */
    transform: translateY(90%);
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
    left: 0;
`;

export const NextButton = styled.button`
    position: absolute;
    top: 130px; /* 이미지(260px)의 중간 */
    transform: translateY(90%);
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
    right: 0;
`;
