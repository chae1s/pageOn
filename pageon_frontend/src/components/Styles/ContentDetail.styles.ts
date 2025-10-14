import {styled, css} from "styled-components";
import { Link } from "react-router-dom";

export const ContentDetailHeader = styled.div`
    display: grid;
    grid-template: 
        'left right' auto
        'left bottom' auto / fit-content(30%) 1fr;
    column-gap: 30px;
    margin-bottom: 60px;
    padding-top: 30px;
`

export const ContentImageContainer = styled.div`
    min-width: 0;
    grid-area: left;
    box-sizing: border-box;
    width: 204px;
    position: relative;
    
`

export const ContentImage = styled.img`
    width: 100%;
    height: auto;
    border-radius: 4px;
`

export const ContentInfoContainer = styled.div`
    min-width: 0;
    grid-area: right;
    box-sizing: border-box;

`

export const ContentTitleWrapper = styled.div`
    display: flex;
    gap: 40px;
    margin-bottom: 12px;
    align-items: center;
`

export const ContentTitle = styled.h1`
    color: #222;
    font-size: 28px;
    font-weight: 600;
    line-height: 32px;
`

export const ContentStatus = styled.div<{$status: 'COMPLETED' | 'ONGOING' | 'REST'}>`
    display: inline-flex;
    align-items: center;
    justify-content: center;
    padding: 4px 10px;
    line-height: 1;
    border-radius: 12px;
    font-weight: 600px;
    height: 20px;
    font-size: 15px;
    color: #FFF;
    cursor: default;
    

    ${({$status}) => {
        switch ($status) {
            case 'COMPLETED':
              return css`
                background-color: #444; // 완결 - 짙은 회색
              `;
            case 'ONGOING':
              return css`
                background-color: #e74c3c; // 연재중 - 빨강
              `;
            case 'REST':
              return css`
                background-color: #3498db; // 휴재 - 파랑
              `;
            default:
              return css`
                background-color: gray;
              `;
          }
    }}
`

export const ContentInfoText = styled.div`
    display: flex;
    margin-bottom: 8px;
`

export const ContentAuthor = styled.div`
    font-size: 16px;
    font-weight: 600;
    line-height: 17px;
    padding-right: 10px;
    border-right: 1px solid #e6e6e6;
`

export const ContentSerialDay = styled.div`
    font-size: 14px;
    line-height: 17px;
    padding-left: 10px;
`

export const ContentScoreContainer = styled.div`
    display: flex;  
    margin-bottom: 8px;
`   

export const ContentRatingContainer = styled.div`
    display: flex;
    gap: 5px;
    padding-right: 10px;
    border-right: 1px solid #e6e6e6;
`

export const ContentRatingScore = styled.div`
    font-size: 16px;
    font-weight: 600;
    line-height: 17px;
`

export const ContentRatingCount = styled.div`
    margin-left: 5px;
    font-size: 14px;
    line-height: 17px;
`

export const ContentViewCount = styled.div`
    padding-left: 10px;
    font-size: 15px;
    line-height: 17px;
`
export const ContentViewCountName = styled.span`
    font-weight: 500;
    margin-right: 5px;
`
export const ContentDescription = styled.p`
    font-size: 15px;
    font-weight: 500;
    line-height: 20px;
    letter-spacing: -0.5px;
    white-space: pre-line;
    overflow: hidden;
    text-overflow: ellipsis;
    word-break: break-word;
    margin-bottom: 8px;
`

export const ContentKeywordContainer = styled.div`
    display: flex;
    flex-wrap: wrap;
    gap: 8px;
    margin-top: 15px;

`

export const ContentKeywordItem = styled.button`
    display: inline-block;
    padding: 6px 12px;
    background-color: #F4F4F4;
    font-size: 15px;
    font-weight: 500;
    border-radius: 8px;
    white-space: nowrap;
    line-height: 1;

`

export const ContentInterestBtnContainer = styled.div`

`

export const ContentInterestBtn = styled.button`

`

export const EpisodeSection = styled.section`
    padding-bottom: 80px;
    border-top: 2px solid #222;
`

export const EpisodeListOption = styled.div`
    display: flex;
    width: 100%;
    padding: 10px 0;
    height: 50px;
    align-items: center;
    justify-content: space-between;
`

export const OptionLeft = styled.div`
    padding-left: 10px;
    display: flex;
`

export const OptionItem = styled.li`
    display: flex;
    justify-content: center;
    align-items: center;
`

export const OptionCheckbox = styled.input`
    width: 15px;
    height: 15px;
    border: 1px solid #eee;
    cursor: pointer;
    border-radius: 4px;
`

export const OptionCheckboxLabel = styled.label`
    vertical-align: top;
    line-height: 18px;
    margin: 0 15px 0 8px;
`

export const FilterBtnDivider = styled.div`
    width: 1px;
    height: 14px;
    margin: 0 7px;
    border-radius: 999px;
    background: #e6e6e6;
`

export const EpisodeSellBtnWrapper = styled.div`
    display: flex;
    flex-wrap: wrap;
    gap: 8px;
`

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
`

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
`

export const OptionRight = styled.div`
    padding-right: 10px;
`

export const EpisodeFilterWrapper = styled.div`
    justify-content: center;
    align-items: center;
    cursor: pointer;
    flex-shrink: 0;
    display: flex;
`

export const EpisodeFilterBtn = styled.button<{$active: boolean}>`
    color: ${({$active}) => ($active? "#69a3ff" : "#b4b4b4" )};
    font-weight: ${({$active}) => ($active? "500" : "")};
    font-size: 14px;
`

export const EpisodeListWrapper = styled.div`
    position: relative;
`

export const EpisodeItem = styled.div`
    display: flex;
    padding: 0 10px;
    width: 100%;
    overflow: hidden;
    position: relative;
    border-top: 1px dotted #bbb;
    align-items: center;
    justify-content: space-between;
    height: 59px;
`

export const EpisodeItemLeft = styled.div`
    display: flex;
    align-items: center;
`

export const EpisodeThumbnailContainer = styled.div`
    margin-left: 8px;
`

export const EpisodeThumbnailImage = styled.img`

`

export const EpisodeInfoContainer = styled.div`
    margin-left: 8px;
    font-size: 14.5px;
`

export const EpisodeTitleAndNum = styled.div`
    font-weight: 500;
`

export const EpisodeTitle = styled(Link)`
    margin-left: 5px;
`

export const EpisodeCreateDate = styled.div`
    font-size: 14px;
    color: #A5A5A5;
    margin-top: 4px;
`

export const EpisodeItemRight = styled.div`
`

export const ShowAllBtnContainer = styled.div`
    position: relative;
`

export const ShowAllBtn = styled.button`
    padding: 16px 0 14px 0;
    width: 100%;
    height: 56px;
    display: flex;
    align-items: center;
    justify-content: center;
    position: relative;
    border-radius: 6px;
    border: 1px solid #ccc;
    margin-top: 0;
`

