import { styled } from "styled-components";

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
    font-size: 15px;
    color: #a5a5a5;
    line-height: 18px;
`

export const CommentTitle = styled.div`
    margin-right: 10px;
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

export const Commentinfo = styled.div`
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
    font-weight: 500;
    line-height: 17px;
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

export const CommentReportBtn = styled.button`
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

export const CommentEditBtn = styled.button`
    font-weight: 500;
    background: none;
    box-shadow: none;
    color: #a5a5a5;
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


