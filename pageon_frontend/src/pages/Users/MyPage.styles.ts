import { Link } from "react-router-dom";
import {styled, css} from "styled-components";

export const SidebarRightWrap = styled.div`
    flex: 1 1 0;
    min-width: 0;
    margin-top: 1em;
    padding-top: 10px;
`

export const MypageTitle = styled.h2`
    font-size: 1.3rem;
    font-weight: 700;
    margin-bottom: 24px;
    text-align: center;
`

export const PasswordCheckForm = styled.form`
    max-width: 400px;
    margin: 30px auto 0;
    padding: 32px;
`

export const PasswordCheckLabel = styled.label`
    display: block;
    margin-bottom: 8px;
    font-weight: 500;
`

export const PasswordCheckInput = styled.input`
    width: 100%;
    padding: 10px 12px;
    border: 1px solid #ccc;
    border-radius: 6px;
    margin-bottom: 16px;
    font-size: 1rem;
`

export const MypageBooksSortBtnWrapper = styled.div`
    display: block;
`

export const mypageBooksSortBtnList = styled.div`
    display: block;
    padding-left: 8px;
    margin-bottom: 16px;
`

export const MypageBooksSelectType = styled.div`
    display: flex;
    gap: 14px;
    color: #444;
    font-size: 0.98rem;
    padding: 10px 0 10px 8px;
    border-bottom: 1px solid rgba(0, 0, 0, .1)
`

export const SortBtn = styled.button<{active: boolean}>`
    color: ${({active}) => (active? "#69a3ff" : "#b4b4b4" )};
    font-weight: ${({active}) => (active? "500" : "")};
`

export const MypageBooksSearchSelectSort = styled.div`
    display: flex;
    justify-content: space-between;
`

export const MypageCommentsSection = styled.section`
    padding-left: 10px;
`

export const MypageBooksSearchGroup = styled.div`
    display: flex;
    align-items: center;
    width: 100%;
    max-width: 250px;
    margin: 12px 0 0 0;
`

export const MypageBooksSearchInput = styled.input`
    flex: 1;
    padding: 8px 8px 6px 8px;
    border: none;
    border-radius: 0;
    font-size: 1rem;
    background: none;
    color: #222;
`

export const MypageBooksSearchBtn = styled.button`
    background: none;
    border: none;
    padding: 0 6px;
    cursor: pointer;
    display: flex;
    align-items: center;
    height: 32px;
`

export const MypageBooksSortGroup = styled.div`
    display: flex;
    gap: 12px;
    font-size: 0.85em;
    padding: 10px 8px;
`

export const BookListSection = styled.section`
    margin-top: 13px;
    margin-bottom: 36px;
    padding-left: 8px;
`

export const SubmitBtn = styled.button`
    width: 100%;
    padding: 14px;
    background-color: #528efa;
    color: #fff;
    border: none;
    border-radius: 4px;
    font-size: 1.1rem;
    font-weight: 600;
    cursor: pointer;
    margin-top: 12px;

    &:disabled {
        background-color: var(--accent-color);
    }
`

export const ErrorMessage = styled.div`
    color: var(--error-color);
    font-size: 0.8rem;
    margin-top: 3px;
`

export const MypagePointSection = styled.section`
    padding-left: 10px;
`

export const PointTransactionList = styled.div`
    width: 100%;
    margin: 0 0 80px;
`

export const PointTransactionTable = styled.table`
    width: 100%;
    margin-bottom: 40px;
`

export const PointTransactionColDate = styled.col`
    width: 170px;
    text-align: center;
`

export const PointTransactionColTitle = styled.col`
    width: auto;
    text-align: left;
`

export const PointTransactionColAmount = styled.col`
    width: 140px;
    text-align: right;
`

export const PointTransactionColBalance = styled.col`
    width: 140px;
`

export const PointTransactionTableHead = styled.thead`
    
`

export const PointTransactionTheadTr = styled.tr`
    border-bottom: solid 1px #e5e5e5;
    font-size: 13px;
`

export const PointTransactionThDate = styled.th`
    padding: 12px 15px;
`

export const PointTransactionThTitle = styled.th`
    padding: 12px 15px;
    text-align: left;
`

export const PointTransactionThAmount = styled.th`
    padding: 12px 15px;
    text-align: right;
`

export const PointTransactionThBalance = styled.th`
    padding: 12px 15px;
    text-align: right;
`

export const PointTransactionTableBody = styled.tbody`

`

export const PointTransactionTbodyTr = styled.tr`
    border-bottom: solid 1px #e6e6e6;
`

export const PointTransactionTdDate = styled.td`
    padding: 13px 15px 12px 14px;
    font-size: 14px;
    line-height: 14px;
    text-align: center;
`

export const PointTransactionTdTitle = styled.td`
    font-weight: 600;
    padding: 13px 15px 12px 14px;
    font-size: 14px;
    line-height: 14px;
`

export const PointTransactionTdAmount = styled.td`
    padding: 13px 15px 12px 14px;
    font-size: 14px;
    line-height: 14px;
    color: #69a3ff;
    font-weight: 600;
    text-align: right;
`

export const PointTransactionTdBalance = styled.td`
    padding: 13px 15px 12px 14px;
    font-size: 14px;
    line-height: 14px;
    font-weight: 600;
    text-align: right;
`

export const PointTransactionEmpty = styled.div`
    text-align: center;
    padding: 20px 0;
    font-size: 14px;
    color: #666;
`

export const LibraryContentListWrapper = styled.div`
    position: relative;
    width: 100%;
    overflow: hidden;
    padding-bottom: 50px;
`

export const LibraryContentList = styled.div`
    display: flex;
    flex-wrap: wrap;
    gap: 16px;
`

export const EmptyListText = styled.div`
    margin: 10px auto;
`