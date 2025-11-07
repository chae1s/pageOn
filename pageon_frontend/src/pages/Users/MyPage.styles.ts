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