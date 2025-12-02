import React from 'react'
import { Pagination } from '../../types/Page';
import * as S from '../Styles/Pagination.styles';

interface PaginationProps {
    pageData: Pagination<any>;
    handlePageChange: (page: number) => void;
}
function PageNavigator({ pageData, handlePageChange }: PaginationProps) {

    

    const getPageNumbers = () => {
        if (!pageData) return [];

        const currentPage = pageData.pageNumber;
        const totalPages = pageData.totalPages;

        // 한 번에 보여줄 페이지 번호 개수
        const pageBlockSize = 6;

        const startPage = Math.floor(currentPage / pageBlockSize) * pageBlockSize;

        let endPage = startPage + pageBlockSize - 1;

        if (endPage >= totalPages) {
            endPage = totalPages - 1;
        }

        const pages = [];
        for (let i = startPage; i <= endPage; i++) {
            pages.push(i)
        }

        return pages;
    }

    const pageNumbers = getPageNumbers();

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

    return (
        <S.PaginationContainer>
            
            <S.PaginationIconWrapper
                onClick={() => handlePageChange(pageData.pageNumber - 1)}
                disabled={pageData.first}
            >
                <PrevIcon />
            </S.PaginationIconWrapper>

            <S.PaginationNumberList>
                
                {pageNumbers.map((number) => (
                    <S.PaginationNumberListItem key={number}>
                        <S.PaginationNumberBtn
                            $active={pageData.pageNumber === number}
                            onClick={() => handlePageChange(number)}
                        >
                            {number + 1}
                        </S.PaginationNumberBtn>
                    </S.PaginationNumberListItem>
                ))}
            </S.PaginationNumberList>

            
            <S.PaginationIconWrapper
                onClick={() => handlePageChange(pageData.pageNumber + 1)}
                disabled={pageData.last}
            >
                <NextIcon />
            </S.PaginationIconWrapper>
        </S.PaginationContainer>
    )
}
export default PageNavigator;
