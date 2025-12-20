import styled from "styled-components";
import { Link } from "react-router-dom";

export const ContentItem = styled.div`
    display: flex;
    gap: 12px;
    padding: 14px;
    background: #fff;
    width: calc(50% - 8px);
    min-height: 160px;
    box-sizing: border-box;
    transition: transform 0.15s ease, box-shadow 0.15s ease;
    cursor: default;

    @media (max-width: 768px) {
        width: 100%;
    }
`;

export const ContentImageCover = styled.div`
    height: 150px;
    border-radius: 4px;
    overflow: hidden;
    background: #f6f6f6;
    flex-shrink: 0;
`;

export const ContentImage = styled.img`
    width: 100%;
    height: 100%;
    object-fit: cover;
    cursor: pointer;
`;

export const ContentInfoCover = styled.div`
    display: flex;
    flex-direction: column;
    gap: 10px;
    min-width: 0;
    margin: auto 0;
`;

export const ContentTitleWrapper = styled.div`
    
`

export const ContentTitle = styled.div`
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
`;

export const ContentInfoWrapper = styled.div`
    display: inline-flex;
    font-size: 14px;
    color: #666;
    line-height: 19px;
`

export const ContentAuthor = styled.div`
    
`;

export const ContentType = styled.div`
    
`

export const ContentMetaRow = styled.div`
    display: flex;
    flex-wrap: wrap;
    font-size: 14px;
    line-height: 19px;
`;

export const ContentEpisodeUpdatedAt = styled.div`
    
`

export const ContentSeparate = styled.div`

`

export const ContentMetaItem = styled.div`
    
`;

export const ContentBadge = styled.span`
    display: inline-flex;
    align-items: center;
    justify-content: center;
    padding: 5px 8px;
    border-radius: 8px;
    background: #eef2ff;
    color: #3b5bdb;
    font-size: 12px;
    font-weight: 600;
`;

export const ContentStatus = styled.span`
    
`;

export const ContentLastReadHistory = styled.div`
    display: flex;
    flex-wrap: wrap;
    font-size: 14px;
    line-height: 19px;
`;

export const ContentEpisodeReadBtn = styled.button`
    margin-left: auto;
    display: flex;
    align-items: center;
    gap: 3px;
`

export const ContentEpisodeReadBtnText = styled.div`
    font-size: 14px;
    line-height: 19px;
    color: #444;
`

export const ContentEpisodeReadIcon = styled.img`
    width: 19px;
    height: 19px;
`



