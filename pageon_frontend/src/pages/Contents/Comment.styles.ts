import { styled } from "styled-components";
import { Link } from "react-router-dom";

export const CommentList = styled.div`
    width: 100%;
    margin: 0 0 80px;
`

export const CommentHeader = styled.div`
    display: flex;
    justify-content: space-between;
    align-items: center;
`
export const CommentCount = styled.div`
    font-weight: 600;
`

export const SortBtnList = styled.div`
    display: flex;
    padding: 16px 0;
    gap: 10px;
`

export const CommentInputSection = styled.div`
    width: 100%;
    display: flex;
    box-sizing: border-box;
    flex-direction: column
`

export const CommentInputWrap = styled.div`
    background: #fff;
    border: 1px solid #ccc;
    border-radius: 8px;
    display: flex;
    align-items: center;
    padding: 10px;
    height: auto;
    width: 100%;
    flex: 1;
    padding-inline: 16px;
`

export const CommentInputFlex = styled.div`
    display: flex;
    flex: 1;

`

export const CommentInputTextarea = styled.textarea`
    height: 18px;
    outline-style: none;
    color: #444;
    word-break: break-all;
    font-size: 14px;
    font-weight: 400;
    line-height: 18px;
    resize: none;
    width: 100%;
    overflow: hidden;
`

export const CommentInputBtn = styled.span`
    font-size: 14px;
    font-weight: 400;
    line-height: 18px;
    cursor: pointer;
    margin-left: 12px;
`

export const CommentSpoilerCheckSection = styled.div`
    padding-bottom: 15px;
    padding-top: 10px;
    line-height: 1.4;
`

export const CommentSpoilerCheckWrap = styled.div`
    align-items: center;
    cursor: pointer;
    height: 20px;
    display: inline-flex;
    line-height: 1.4;
    box-sizing: border-box;
`

export const CommentSpoilerCheckboxWrap = styled.div`
    aligh-items: center;
    min-width: fit-content;
    display: flex;
    position: relative;
    line-height: 1.4;
    margin-right: 10px;

`

export const CommentSpoilerCheckbox = styled.input`
    cursor: pointer;
    position: relative;
    box-sizing: border-box;
    padding: 0;
    overflow: visible;
`

export const CommentSpoilerCheckboxCheckIcon = styled.img`
    width: 18px;
    height: 18px;
`

export const CommentSpoilerCheckboxEmptyIcon = styled.img`
    width; 18px;
    height: 18px;
`

export const CommentSpoilerText = styled.span`
    font-size: 13px;
    font-weight: 400;
    line-height: 18px;

`

export const CommentListUl = styled.ul`
    display: grid;
`

export const CommentListEmptyText = styled.p`
    margin: 0 auto;
`

export const CommentListLi = styled.li`
    display: grid;
    gap: 7px;
    margin: 0;
    padding: 16px 0;
    
    &:not(:first-of-type) {
        border-top: 1px solid #e6e6e6;
    }
`

export const CommentEpisode = styled.div`
    display: flex;
    font-size: 12px;
    color: #a5a5a5;
    line-height: 15px;
`

export const CommentTitle = styled.div`
    margin-right: 6px;
`

export const CommentContentWrap = styled.div`
    position: relative;
    outline: none;
    box-sizing: border-box;
`

export const CommentContent = styled.p`
    font-size: 15px;
    overflow: hidden;
    text-overflow: ellipsis;
    max-height: calc(24px * 4);
    line-height: 24px;
    white-space: pre-line;
    word-break: break-all;
    overflow-wrap: anywhere;
`

export const CommentSpoiler = styled.div`
    font-size: 14px;
    line-height: 22px;
    color: #888;
    background: transparent;
    border: none;
    border-radius: 0;
    padding: 0;
    cursor: pointer;
    user-select: none;
`

export const HiddenCommentContent = styled(CommentContent)`
    visibility: hidden;
`

export const CommentSpoilerOverlay = styled.div`
    position: absolute;
    inset: 0;
    display: flex;
    align-items: center;
    color: #888;
    cursor: pointer;
    user-select: none;
`

export const CommentInfo = styled.div`
    display: flex;
    justify-content: space-between;
    align-items: center;
`

export const CommentInfoLeft = styled.div`
    display: grid;
    gap: 6px;
    color: #a5a5a5;
    line-height: 17px;
`

export const CommentUserInfo = styled.div`
    display: grid;
    grid-template-columns: auto auto auto;
    align-items: center;
    width: fit-content;
    gap: 4px;
    font-size: 14px;
    font-weight: 700;
    line-height: 17px;
    color: #444;
`

export const CommentDateBtn = styled.div`
    display: flex;
    align-items: center;
    margin: 8.5px 0;
    color: #a5a5a5;
    font-size: 14px;
    font-weight: 500;
    line-height: 17px;
`

export const CommentSpace = styled.div`
    background: #f0f0f0;
    border-radius: 999px;
    width: 1px;
    height: 10px;
    margin: 0 7px;
`

export const CommentLeftBtn = styled.button`
    font-weight: 500;
    background: none;
    box-shadow: none;
    color: #a5a5a5;
`

export const CommentBtnDivider = styled.div`
    width: 2px;
    height: 2px;
    margin: 0 5px;
    border-radius: 999px;
    background: #e6e6e6;
`

export const CommentRightBtn = styled.button`
    font-weight: 500;
    background: none;
    box-shadow: none;
    color: #a5a5a5;
`

export const LikeEmptyIcon = styled.img`
    width: 16px;
    height: 16px;
`

export const LikeFullIcon = styled.img`
    width: 16px;
    height: 16px;
`


export const CommentLikeBtn = styled.button`
    display: flex;
    justify-content: center;
    align-items: center;
    padding: 8px 7px;
    color: #787878;
    font-weight: 500;  
    line-height: 16px;
    margin-left: 6px;
    min-width: 64px;
    font-size: 14px;
    gap: 10px;
`

export const CommentListBtn = styled.button`
    font-size: 13px;
    font-weight: 400;
    line-height: 24px;
    color: #888;
`
export const CommentBestInfo = styled.div`
    display: flex;
    gap: 6px;
    color: #a5a5a5;
    line-height: 20px;
`

export const CommentBestUserInfo = styled.div`
    grid-template-columns: auto auto auto;
    align-items: center;
    font-size: 14px;
    font-weight: 500;
    line-height: 20px;
    margin: 2px 3px;
`

export const CommentBestIcon = styled.div`
    padding: 2px 5px;
    background: var(--error-color);
    color: #fff;
    font-size: 14px;
    border-radius: 3px;
`



