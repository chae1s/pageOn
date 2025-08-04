import { NavLink, Link } from "react-router-dom";
import { styled } from "styled-components";

export const SidebarContainser = styled.div`
    max-width: 220px;
    min-width: 180px;
    background: #fff;
    font-size: 1rem;
    height: fit-content;
    position: sticky;
    align-self: flex-start;
    margin-top: 1em;
`

export const SidebarMainLink = styled(Link)`
    display: block;
    width: 100%;
    height: 54px;
    font-size: 20px;
    font-weight: 600;
    padding-top: 10px;
`

export const SidebarNav = styled.nav`
    width: 150px;

`

export const SidebarNavSectionUl = styled.ul`
    list-style: none;
    padding: 0;
    margin: 0;
`

export const SidebarNavSectionLi = styled.li<{last: boolean}>`
    margin-bottom: 28px;
    padding-bottom: 28px;
    border-bottom: ${({last}) => (last? "none" : "1px solid #f0f0f0")};
`

export const SidebarNavTitle = styled.div`
  font-weight: 600;
  color: #444;
  margin-bottom: 10px;
  font-size: 17px;
`

export const SidebarNavItemUl = styled.ul`
  margin-left: 0;
  padding-left: 0;
`

export const SidebarNavItemLi = styled.li`
  margin-bottom: 8px;
`

export const SidebarNavLink = styled(NavLink)`
  color: #444;
  text-decoration: none;
  padding: 4px 0 4px 24px;
  display: block;
  border-radius: 6px;
  transition: background;

  &.active {
    font-weight: 600;
    color: #69a3ff;
  }
`



