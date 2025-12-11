import { styled, css } from "styled-components";
import { Link } from "react-router-dom";

export const ContentSection = styled.div`
    width: 100%;
    padding: 6px 0 16px;
    font-weight: 500;
    font-size: 16px;
    line-height: 20px;
    color: #222;
`

export const ContentTotalCount = styled.div`

`

export const ContentSearchList = styled.ul`
    padding: 16px 0 50px;
`

export const ContentSearchListItem = styled.li`
    padding: 30px 0;
    width: 100%;
    position: relative;
    
    &:first-of-type {
        padding-top: 0px;
    }
`

export const ContentSearchWrapper = styled.div`
    display: flex;
    height: 100%;

`

export const ContentSearchItemCoverSection = styled.div`
    width: 120px;
    margin-right: 30px;
    align-items: stretch;
    order: 1;
    flex-shrink: 0;
`

export const ContentCoverWrapper = styled.div`
    position: relative;
    align-self: stretch;
    width: 100%;
    padding-bottom: 144.80519480519482%;
`

export const ContentCoverImage = styled.img`
    width: 100%;
    height: 100%;
    position: absolute;
    inset: 0px;
    object-fit: cover;
    object-position: center center;
    opacity: 1;
    border-style: none;
`

export const ContentSearchItemInfoSection = styled.div`
    align-items: center;
    flex: 1 1 auto;
    min-width: 0px;
    display: flex;
    order: 2;
`

export const ContentInfoWrapper = styled.div`
    display: flex;
    flex-direction: column;
    width: 100%;
    padding-right: 0px;
`

export const ContentTitleWrapper = styled.div`
    display: inline-flex;
`

export const ContentTitle = styled(Link)`
    overflow: hidden;
    text-overflow: ellipsis;
    word-wrap: break-word;
    white-space: normal;
    max-height: calc(19px + 1);
    line-height: 19px;
    word-break: break-all;
    font-weight: 500;
    font-size: 16px;
    margin-bottom: 4px;
    cursor: pointer;
    padding-right: 10px;
`

export const ContentType = styled.div`
    margin-left: 10px;
    color: #888;
    font-size: 14px;
    line-height: 19px;
    margin-bottom: 4px;
`

export const ContentAuthor = styled.div`
    display: flex;
    margin-bottom: 5px;
    width: 100%;
    height: 100%;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
    color: #888;
`

export const EpisodeInfoContainer = styled.div`
    display: flex;
    gap: 10px;
    margin-bottom: 5px;
`

export const ContentEpisodeCount = styled.div`
    color: #888;
    letter-spacing: -0.0em;
    overflow-wrap: break-word;
    font-size: 14px;
    line-height: 17px;
    display: flex;
    align-items: center;
    margin-bottom: 5px;
`

export const ContentEpisodeUpdatedAt = styled.div`
    color: #888;
    letter-spacing: -0.0em;
    overflow-wrap: break-word;
    font-size: 14px;
    line-height: 17px;
    display: flex;
    align-items: center;
    margin-bottom: 5px;
`

export const ContentRatingContainer = styled.div`
    display: flex;
    gap: 5px;
    padding-right: 10px;
    border-right: 1px solid #e6e6e6;
    align-items: center;
    font-size: 13px;
    line-height: 16px;
    margin-bottom: 10px;
`

export const ContentRatingStarIcon = styled.img`
    width: 17px;
    height: 17px;
`

export const ContentRatingScore = styled.div`
    font-size: 16px;
    font-weight: 600;
    line-height: 17px;
`

export const ContentRatingCount = styled.div`
    margin-left: 5px;
    font-size: 14px;
    line-height: 17px;
`

export const ContentDescriptionLink = styled(Link)`
`

export const ContentDescription = styled.p`
    color: #888;
    overflow: hidden;
    text-overflow: epllipsis;
    white-space: normal;
    max-height: 40px;
    line-height: 20px;
    width: 100%;
    font-size: 14px;
    overflow-wrap: break-word;
    
`

export const ContentKeywordContainer = styled.div`
    display: flex;
    flex-wrap: wrap;
    gap: 8px;
    margin-top: 15px;
`

export const ContentKeywordItem = styled.button`
    display: inline-block;
    padding: 6px 12px;
    background-color: #F4F4F4;
    font-size: 15px;
    font-weight: 500;
    border-radius: 8px;
    white-space: nowrap;
    line-height: 1;
`

export const NoResultsWrapper = styled.div`
    adding: 50px 20px;
    text-align: center;
    color: #888;
    padding: 80px;
`

export const NoResultsText = styled.p`

`