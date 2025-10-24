import { Link } from "react-router-dom";
import {styled, css} from "styled-components";

export const UsersFormWrapper = styled.div`
    background: #fff;
    padding: 0 40px;
    width: 100%;
    max-width: 410px;
    text-align: center;
    margin: 0 auto;
`

export const UsersTitle = styled.h1`
    font-size: 25px;
    font-weight: 700;
    margin-bottom: 32px;
    color: #444;
`

export const UsersForm = styled.form`
    display: flex;
    flex-direction: column;
    text-align: left;
`

export const UsersFormGroup = styled.div`
    margin-bottom: 17px;
    display: flex;
    flex-direction: column;

    &.users-birthDate-gender {
        display: flex;
        flex-direction: row;
        gap: 10px;
    }
`

export const UsersFormLabel = styled.label`
    display: block;
    font-size: 0.9rem;
    font-weight: 600;
    margin-bottom: 8px;
    color: #444;
`

export const UsersFormInput = styled.input<{validation?: "" | "input-success" | "input-error"}>`
    width: 100%;
    padding: 12px 14px;
    border: 1px solid #ccc;
    border-radius: 3px;
    font-size: 1rem;
    box-sizing: border-box;


    ${({validation}) => 
        validation === "input-success" &&
        css`
            border-color: #A0C4FF !important;
        `
    }

    ${({validation}) => 
        validation === "input-error" &&
        css`
            border-color: #FC5858 !important;
        `
    }
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

export const UsersLinkWrap = styled.div`
    margin-top: 24px;
    font-size: 0.9rem;
    color: #666;
    margin-right: 8px;
`

export const UsersLink = styled(Link)`
    color: #528efa;
    text-decoration: none;
    font-weight: 600;
    margin-left: 8px;

    &:hover {
        text-decoration: underline;
    }
`

export const ErrorMessage = styled.p`
    color: var(--error-color);
    font-size: 0.8rem;
    margin-top: 8px;
`

