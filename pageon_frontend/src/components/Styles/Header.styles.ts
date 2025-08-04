import { NavLink, Link } from "react-router-dom";
import { styled } from "styled-components";

export const HeaderContainer = styled.div`
  background: #fff;
  border-bottom: 1px solid #e5e5e5;
  padding: 0 0;
  position: relative;
  width: 100%;
`

export const Header = styled.header`
  max-width: 1100px;
  margin: 0 auto;
  position: relative;
  padding: 0;
`

export const HeaderLogoSearchUser = styled.div`
  width: 1100px;
  display: grid;
  grid-template-columns: 1fr auto auto;
  align-items: center;
  padding: 16px 30px 8px 30px;
`

export const HeaderLogo = styled(Link)`
  display: inline-flex;
  width: fit-content;
`

export const BookSearchForm = styled.form`
  width: 260px;
  margin-right: 8px;
  display: flex;
  align-items: flex-start;
`

export const BookSearchInputWrap = styled.div`
  flex: 1;
  box-sizing: border-box;
  min-width: 0;
`

export const BookSearchInputItem = styled.div`
  box-sizing: border-box;
`

export const BookSearchLabel = styled.label`
  background: #fff;
  border: 1px solid #ccc;
  border-radius: 8px;
  display: flex;
  align-items: center;
  padding: 10px;
  height: 40px;
`

export const BookSearchIcon = styled.svg`
  margin-right: 12px;
  opacity: 1;
  flex: none;
  vertical-align: middle;
`

export const BookSearchText = styled.span`
  position: absolute;
  width: 1px;
  height: 1px;
  margin: -1px;
  padding: 0;
  overflow: hidden;
  border: 0;
`

export const BookSearchInput = styled.input`
  flex: 1;
  width: 0;
  min-width: 0;
  height: 100%;
  font-size: 15px;
  line-height: 18px;
  letter-spacing: -0.01rem;
  color: #444;
`

export const AuthLinkContainer = styled.div`
  display: flex;
  align-items: center;
  position: relative;
`

export const AuthLinkList = styled.div`
  display: flex;
  align-items: center;
  box-sizing: border-box;
`

export const AuthLinkItem = styled.div<{$last:boolean}>`
  display: inline-flex;
  margin: 0 14px;
  width: 70px;
  align-items: center;
  justify-content: center;
  margin-right : ${({$last}) => ($last? "0px" : "14px" )};
`

export const AuthLink = styled(Link)`
  display: inline-flex;
  justify-content: center;
  align-items: center;
  font-size: 15px;
  font-weight: 500;
`

export const HeaderContentsEtc = styled.div`
  display: flex;
  align-items: center;
  padding: 0 30px;
  justify-content: space-between;
`

export const HeaderLinkList = styled.div`
  display: flex;
  align-items: center;
  min-width: 0;
  overflow: auto;
  font-size: 1rem;
  font-weight: 500;
  line-height: 18px;
  letter-spacing: -0.01rem;
  white-space: nowrap;
  scrollbar-width: none;
`

export const HeaderNavLink = styled(NavLink)<{$first: boolean}>`
  display: flex;
  align-items: center;
  padding: 17px 16px;
  padding-left: ${({$first}) => ($first? "0px" : "16px" )};

  &.active {
      color: #69a3ff;
  }
`

export const HeaderEtcLinkList = styled.div`
  display: flex;
  align-items: center;
  position: relative;
  font-size: 15px;
  font-weight: 500;
  line-height: 18px;
`

export const HeaderEtcLink = styled(Link)`
  display: inline-flex;
  align-items: center;
  margin: 12px 0px 12px 24px;
`

export const HeaderEtcLinkText = styled.span`
  letter-spacing: -0.02em;
`
