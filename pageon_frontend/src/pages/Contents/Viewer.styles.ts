import { Link } from "react-router-dom";
import styled from "styled-components";

export const Viewer = styled.div`
    user-select: none;
`

export const EpisodeTitleSection = styled.section<{ isVisible: boolean }>`
    display: flex;
    position: fixed;
    top: 0px;
    left: 0px;
    width: 100%;
    min-height: 52px;
    z-index: 900;
    background: #eee;
    border-bottom: 1px solid #ccc;
    opacity: ${props => props.isVisible ? 1 : 0};
    transform: translate(0px, ${props => props.isVisible ? '0px' : '-10px'});
    pointer-events: ${props => props.isVisible ? 'auto' : 'none'};
    transition: color 0.2s border-color 0.2s, background 0.2s, transform 0.5s, opacity 0.35s;
`

export const EpisodeTitleContainer = styled.div`
    display: flex;
    justify-content: space-between;
    align-items: center;
    width: 100%;
    max-width: 720px;
    margin: 0 auto;
    padding: 0px 12px;
`

export const WebnovelTitle = styled(Link)`
    font-size: 16px;
    font-weight: 700;
    line-height: 19px;
    min-width: 0px;
    flex: 1 1 0px;
    white-space: nowrap;
    color: #444;
    transition: color 0.2s
`

export const EpisodeLinkContainer = styled.div`
    display: flex;
    gap: 20px;
`

export const EpisodeLink = styled(Link)<{ $disabled?: boolean }>`
    font-size: 14px;
    font-weight: 600;
    line-height: 19px;
    min-width: 0px;
    white-space: nowrap;
    color: ${props => props.$disabled ? '#a5a5a5' : '#444'};
    pointer-events: ${props => props.$disabled ? 'none' : 'auto'};
    cursor: ${props => props.$disabled ? 'default' : 'pointer'};
    text-decoration: none;
    transition: color 0.2s
`

export const ViewerBodySection = styled.section`
    color: #222;

`

export const EpisodeContentContainer = styled.div`
    max-width: 720px;
    margin: 0 auto;
    box-sizing: border-box;
`

export const EpisodeViewerContents = styled.div`
    position: relative;
`


export const ContentWrapper = styled.div`
    padding-top: 60px;
    user-select: none;
`

export const EpisodeContentHeader = styled.div`
    padding: 30px 4% 50px;
    font-size: 20px;
    font-weight: 600;
`

export const EpisodeNum = styled.span`
    margin-right: 7px;
`

export const EpisodeTitle = styled.span`

`

export const EpisodeContent = styled.article`
    letter-spacing: 0;
    padding: 0px 4% 80px;
    font-size: 18px;
    line-height: 33px;
    white-space: pre-line;
`

export const EpisodeThumbnailImage = styled.img`
    display: block;
    width: 100%;
    height: auto;
`

export const EpisodeContentFooter = styled.div`
    height: 60px;
    box-sizing: border-box;
    margin: 50px 0;
`

export const WraningMsg = styled.span`
    display: flex;
    justify-content: center;
    align-items: center;
    width: 100%;
    min-height: 60px;
    font-size: 11px;
    line-height: 18px;
    color: #888;
    padding: 16px;
    overflow-wrap: break-word;
    word-break: keep-all;
    box-sizing: border-box;
` 

export const ViewerCommentSection = styled.section`
    max-width: 720px;
    margin: 0 auto;
`

export const ViewerNextEpisodeBtnSection = styled.div`
    padding: 50px 0 100px;
    text-align: center;
`

export const ViewerNextEpisodeBtnContainer = styled.div`
    margin: 0 auto;
    text-align: center;
`

export const ViewerNextEpisodeBtn = styled(Link)<{ $disabled?: boolean }>`
    display: inline-flex;
    justify-content: center;
    align-items: center;
    width: 100%;
    max-width: 240px;
    border-radius: 6px;
    min-height: 52px;
    margin: 0 16px;
    font-size: 16px;
    font-weight: 600;
    line-height: 19px;
    background: ${props => props.$disabled ? 'var(--accent-color)' : '#528efa'};
    color: #fff;
    pointer-events: ${props => props.$disabled ? 'none' : 'auto'};
    cursor: ${props => props.$disabled ? 'default' : 'pointer'};
`

export const ViewerRatingSection = styled.div`
    max-width: 720px;
    margin: 20px auto;
    display: flex;
    justify-content: space-between;
`

export const ViewerRatingScore = styled.div`
    display: flex;
    align-items: center;
    gap: 10px;
`

export const RatingFullStarIcon = styled.img`
    width: 24px;
    height: 24px;
`

export const ViewerAverageRatingScore = styled.span`
    font-size: 14px;
    font-weight: 700;
    line-height: 24px;
`

export const ViewerRatingCount = styled.span`
    font-size: 14px;
    font-weight: 400;
    line-height: 24px;
`

export const ViewerRatingCreateBtn = styled.button`
    font-size: 13px;
    font-weight: 400;
    line-height: 24px;
    color: #888;
`

// Rating Modal
export const RatingModalOverlay = styled.div`
    position: fixed;
    top: 0;
    left: 0;
    width: 100vw;
    height: 100vh;
    background: rgba(0, 0, 0, 0.5);
    display: flex;
    justify-content: center;
    align-items: center;
    z-index: 1000;
`

export const RatingModal = styled.div`
    width: 100%;
    max-width: 420px;
    background: #fff;
    border-radius: 12px;
    padding: 24px 20px;
    box-shadow: 0 8px 24px rgba(0,0,0,0.18);
`

export const RatingModalTitle = styled.h3`
    margin: 0 0 12px 0;
    font-size: 18px;
    line-height: 24px;
    font-weight: 700;
    color: #222;
    text-align: center;
`

export const RatingModalSubtitle = styled.p`
    margin: 0 0 16px 0;
    font-size: 13px;
    line-height: 18px;
    color: #666;
    text-align: center;
`

export const RatingStars = styled.div`
    display: flex;
    justify-content: center;
    align-items: center;
    gap: 8px;
    padding: 10px 0 4px;
`

export const RatingStarWrapper = styled.div`
    position: relative;
    width: 40px;
    height: 40px;
    cursor: pointer;
    display: inline-flex;
    align-items: center;
    justify-content: center;
`

export const RatingStarImage = styled.img`
    width: 34px;
    height: 34px;
    user-select: none;
`

export const RatingScoreText = styled.div`
    margin-top: 10px;
    text-align: center;
    font-size: 14px;
    font-weight: 600;
    color: #333;
`

export const RatingModalActions = styled.div`
    display: flex;
    justify-content: center;
    gap: 10px;
    margin-top: 18px;
`

export const RatingCancelBtn = styled.button`
    min-width: 96px;
    height: 40px;
    border-radius: 8px;
    background: #f1f3f5;
    color: #333;
    font-size: 14px;
    font-weight: 600;
`

export const RatingConfirmBtn = styled.button`
    min-width: 96px;
    height: 40px;
    border-radius: 8px;
    background: #528efa;
    color: #fff;
    font-size: 14px;
    font-weight: 700;
`
