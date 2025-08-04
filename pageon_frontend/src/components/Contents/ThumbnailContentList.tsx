import React from "react";
import { SimpleContent } from "../../types/Content";
import { useThumbnailSlide } from "./Hooks/useThumbnailSlide";
import * as S from "../Styles/ThumbnailContent.styles"
import ThumbnailContentItem from "./ThumbnailContentItem";

interface Props {
    contents: SimpleContent[];
    layout?: 'grid' | 'slider';
}

const VISIBLE_COUNT = 6;
const SLIDE_UNIT = 175;

function ThumbnailContentList({ contents, layout = 'grid' }: Props) {

    const NextIcon = () => (
        <svg width="20" height="20" viewBox="0 0 20 20" fill="none">
            <path d="M8 5l4 5-4 5" stroke="#222" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
        </svg>
    )

    const PrevIcon = () => (
        <svg width="20" height="20" viewBox="0 0 20 20" fill="none">
            <path d="M12 5l-4 5 4 5" stroke="#222" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
        </svg>
    )

    const { startIdx, slideX, prevPage, nextPage, maxIdx } = useThumbnailSlide(contents.length, VISIBLE_COUNT, SLIDE_UNIT);

    const isSlider = layout === 'slider';
        
    
    return (
        <S.ContentListWrapper $layout={layout}>
            {isSlider && startIdx > 0 && <S.ContentListArrow direction="left" onClick={prevPage}><PrevIcon /></S.ContentListArrow>}
            {isSlider && startIdx < maxIdx && <S.ContentListArrow direction="right" onClick={nextPage}><NextIcon /></S.ContentListArrow>}
            <S.ContentList $layout={layout} style={isSlider ? { transform: `translateX(${slideX}px)` } : {}}>
                {contents.map((content) => (
                    <ThumbnailContentItem key={content.id} content={content} layout={layout} />
                ))}
            </S.ContentList>
        </S.ContentListWrapper>
    )
}

export default ThumbnailContentList;