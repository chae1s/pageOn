import { styled } from "styled-components";

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