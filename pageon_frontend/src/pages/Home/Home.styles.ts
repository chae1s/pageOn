import styled from "styled-components";
import { Link } from "react-router-dom";

export const HomeBanner = styled.section`
    display: flex;
    align-items: center;
    justify-content: space-between;
    background: linear-gradient(90deg, #f8fafc 60%, #eaf4ff 100%);
    border-radius: var(--border-radius);
    padding: 40px 32px;
    margin-bottom: 48px;
    gap: 32px;
    min-height: 280px;
`

export const BannerText = styled.div`
    flex: 1 1 0;
`

export const SectionBookList = styled.section`
    margin-bottom: 40px;
`

export const SectionBookTitleWrapper = styled.div`
    display: flex;
    justify-content: space-between;
    width: 100%;
    height: 48px;
    padding-top: 6px;
`;

export const SectionBookListTitle = styled.h2`
    font-size: 1.3rem;
    font-weight: 700;
    margin-bottom: 20px;
    color: #222;
`;

export const SectionBookListMoreViewLink = styled(Link)`
    color: #b0b8c1;
    background: none;
    border: none;
    font-size: 0.98rem;
    font-weight: 500;
    cursor: pointer;
    text-decoration: none;
    padding: 4px 0px 4px 12px;
    border-radius: 4px;
    transition: background 0.15s;
    display: flex;
    align-items: center;
    gap: 2px;
    margin-left: 0;
`

export const WeeklyTabsWrapper = styled.div`
    margin-bottom: 8px;
    height: 38px;
`

export const WeeklyTabs = styled.div`
    display: flex;
    justify-content: space-between;
    box-sizing: border-box;
    line-height: 38px;
`

export const WeeklyTabsItem = styled.div`
    position: relative;
    color: #222;
`

export const WeeklyTabsBtn = styled.button<{$isActive: boolean}>`
    background: none;
    border: none;
    color: ${({$isActive}) => ($isActive? "#69a3ff" : "#b4b4b4")};
    font-weight: ${({$isActive}) => ($isActive? "600" : "")};
`

