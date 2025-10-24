import styled from "styled-components";

export const MainContainer = styled.div`
  min-height: 100vh;
  background: var(--main-bg);
  color: var(--main-color);
  display: flex;
  flex-direction: column;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
`;

export const NoSidebarMain = styled.main`
  width: 100%;
  max-width: 1100px;
  margin: 0 auto;
  padding: 32px 30px;
  background: #fff;
  border-radius: 16px;
  box-shadow: none;
  gap: 40px;
  min-height: 600px;
`;

export const SidebarMain = styled.main`
  width: 100%;
  max-width: 1100px;
  margin: 0 auto;
  padding: 0 30px;
  background: #fff;
  border-radius: 16px;
  box-shadow: none;
  display: flex;
  gap: 40px;
  min-height: 600px;
  align-items: flex-start;
`;

export const SortBtn = styled.button<{$active: boolean}>`
    color: ${({$active}) => ($active? "#69a3ff" : "#b4b4b4" )};
    font-weight: ${({$active}) => ($active? "500" : "")};
`
