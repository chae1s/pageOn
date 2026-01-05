import React from "react";
import { Link } from "react-router-dom";
import { SimpleContent } from "../../types/Content";
import { useRankingSlide } from "./Hooks/useRankingSlide";
import * as S from "../Styles/RankingContent.styles"
import RankingContentItem from "./RankingContentItem";

interface Props {
    contents: SimpleContent[];
    layout: "grid" | "slider";
}

function RankingContentList({ contents = [], layout = 'grid' }: Props) {
    const isSlider = layout === 'slider';
    const containerWidth = 1040; // 한 슬라이드의 너비

    const {
        page,
        slideX,
        prevPage,
        nextPage,
        totalPages
    } = useRankingSlide(contents.length, 3, 346.666);


    const groupedColumns = [];
    for (let i = 0; i < contents.length; i += 3) {
        groupedColumns.push(contents.slice(i, i + 3));
    }
  
    const NextIcon = () => (
      <svg width="20" height="20" viewBox="0 0 20 20" fill="none">
        <path d="M8 5l4 5-4 5" stroke="#222" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" />
      </svg>
    );
  
    const PrevIcon = () => (
      <svg width="20" height="20" viewBox="0 0 20 20" fill="none">
        <path d="M12 5l-4 5 4 5" stroke="#222" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" />
      </svg>
    );
  
    return (
        <S.ContentSection>
            <S.ContentList>
                {isSlider && page > 0 && (
                    <S.PrevButton onClick={prevPage}>
                        <PrevIcon />
                    </S.PrevButton>
                )}

                <S.ContentListContainer $layout={layout}
                    style={layout === "slider"? {width: `${totalPages * containerWidth}px`, transform: `translateX(${slideX}px)`}: {}}
                >
                    {groupedColumns.map((column, colIndex) => (
                        <S.ContentColumn key={colIndex}>
                            <ol>
                                {column.map((content, itemIndex) => {
                                    const rank = colIndex * 3 + itemIndex + 1;
                                    return (
                                        <RankingContentItem key={content.contentId} content={content} rank={rank} />
                                    );
                                })}
                            </ol>
                        </S.ContentColumn>
                    ))}
                </S.ContentListContainer>

                {page < totalPages - 1 && isSlider && (
                    <S.NextButton onClick={nextPage}>
                        <NextIcon />
                    </S.NextButton>
                )}
            </S.ContentList>

        </S.ContentSection>
    );

}

export default RankingContentList;