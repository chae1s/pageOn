import { useState, useCallback, useEffect } from 'react';

export const useThumbnailSlide = (totalItems: number, visibleCount: number, slideUnit: number) => {
    const [startIdx, setStartIdx] = useState(0);
    const maxIdx = Math.max(0, totalItems - visibleCount);
    const [slideX, setSlideX] = useState(0);
  
    const prevPage = useCallback(() => {
      setStartIdx((prev) => Math.max(prev - visibleCount, 0));
    }, [visibleCount]);
  
    const nextPage = useCallback(() => {
      setStartIdx((prev) => Math.min(prev + visibleCount, maxIdx));
    }, [visibleCount, maxIdx]);
  
    const resetSlide = useCallback(() => setStartIdx(0), []);
  
    useEffect(() => {
      setSlideX(-(startIdx * slideUnit));
    }, [startIdx, slideUnit]);
  
    useEffect(() => {
      resetSlide();
    }, [totalItems, resetSlide]);
  
    return {
      startIdx,
      slideX,
      prevPage,
      nextPage,
      maxIdx
    };
}