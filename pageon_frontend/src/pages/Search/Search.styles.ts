import styled from "styled-components";
import { Link } from "react-router-dom";

export const ContentTypeList = styled.ul`
    display: flex;

`

export const ContentTypeItem = styled.li`
    display: flex;
    justify-content: center;
    align-items: center;
    width: 100%;
    height: 50px;
    font-size: 17px;
    font-weight: 500;
`

export const ContentTypeBtn = styled.button<{$active: boolean}>`
    color: ${({$active}) => ($active? "rgba(0, 0, 0, .8)":"rgba(0, 0, 0, .3)")};
    font-weight: ${({$active}) => ($active? "700" : "500")};
    width: 100%;
    height: 100%;
    border-bottom: 1px solid ${({$active}) => ($active? "rgba(0, 0, 0, .8)":"rgba(0, 0, 0, .1)")}
`

export const KeywordTable = styled.ul`

`

export const CategoryWithKeywords = styled.li`
    display: flex;
    width: 100%;
    border: 1px solid #e6e6e6;
    border-top: none;
`

export const CategoryName = styled.div`
    min-width: 140px;
    padding: 19px 0 17px 19px;
    font-weight: 600;
    font-size: 15px;
    line-height: 17px;
    background-color: #f5f5f5;
    display: flex;
    align-items: center;
`

export const KeywordList = styled.ul`
    min-width: 0px
    flex: 1;
    padding: 0 14px;
    display: flex;
    align-items: center;
`

export const KeywordItemWrap = styled.div`
    display: flex;
    flex-wrap: wrap;
    margin: 3px 0;
`

export const KeywordItem = styled.li`
    display: flex;
    align-items: center;
    font-weight: 400;
    font-size: 14px;
    line-height: 28px;
    margin-right: 16px;
    margin-bottom: 2px;
    height: 28px;
    color: #b4b4b4;
    border-radius: 4px;
`

export const KeywordBtn = styled.button<{$active: boolean}>`
    display: flex;
    align-items: center;
    padding: 6px 6px 5px 6px;
    color: ${({$active}) => ($active? "#69a3ff" : "rgba(0, 0, 0, .6)" )};
    font-weight: ${({$active}) => ($active? "600" : "400")};
`

export const SelectSortSection = styled.div`
    display: flex;
    justify-content: flex-end;
`

export const SelectSortBtnGroup = styled.div`
    display: flex;
    gap: 12px;
    font-size: 0.85em;
    padding: 10px 8px;
`



export const PaginationContainer = styled.div`
    display: flex;
    justify-content: center;
    align-items: center;
    padding: 40px 0;
    margin: -40px 0;
`

export const PaginationIconWrapper = styled.button`
    width: 34px;
    height: 34px;
    display: inline-flex;
    align-items: center;
    justify-content: center;

`

export const PaginationNumberList = styled.ul`
    display: flex;
    height: 16px;
    margin: 0 16px;
`

export const PaginationNumberListItem = styled.li`

`

export const PaginationNumberBtn = styled.button<{$active: boolean}>`
    display: inline-block;
    width: 32px;
    font-size: 13px;
    line-height: 16px;
    text-aligh: center;
    font-weight: 700;
    color: ${({$active}) => ($active? "rgba(0, 0, 0, .8)" : "rgba(0, 0, 0, .1)" )};
`
