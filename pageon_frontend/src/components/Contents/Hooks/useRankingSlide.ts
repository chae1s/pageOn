import { useState, useCallback, useEffect } from 'react';

export const useRankingSlide = (
  totalItems: number,
  columnsPerPage: number,
  columnWidth: number
) => {
  const totalPages = Math.ceil(totalItems / (columnsPerPage * 3)); 
  const [page, setPage] = useState(0);
  const [slideX, setSlideX] = useState(0);

  const prevPage = useCallback(() => {
    setPage((prev) => Math.max(prev - 1, 0));
  }, []);

  const nextPage = useCallback(() => {
    setPage((prev) => Math.min(prev + 1, totalPages - 1));
  }, [totalPages]);

  const resetSlide = useCallback(() => setPage(0), []);

  useEffect(() => {
    setSlideX(-page * columnsPerPage * columnWidth);
  }, [page, columnsPerPage, columnWidth]);

  useEffect(() => {
    resetSlide();
  }, [totalItems, resetSlide]);

  return {
    page,
    slideX,
    prevPage,
    nextPage,
    totalPages
  };
};
