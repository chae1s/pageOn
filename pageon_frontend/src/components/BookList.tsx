import React, { useState } from "react";
import "./BookList.css"
import { SimpleBook } from "../types/Book"

const emptyMessage = "작품이 없습니다.";
const VISIBLE_COUNT = 6;

// 슬라이드 관련 상수: 이미지 width 163px, gap 12px
const BOOK_WIDTH = 163;
const BOOK_GAP = 12;
const SLIDE_UNIT = BOOK_WIDTH + BOOK_GAP; // 175

interface Props {
    simpleBooks: SimpleBook[];
    home: boolean;
}

function BookList({ simpleBooks, home } : Props) {
    const [startIdx, setStartIdx] = useState(0);
    const maxIdx = Math.max(0, simpleBooks.length - VISIBLE_COUNT);

    const handleLeft = () => {
        setStartIdx((prev) => Math.max(0, prev - VISIBLE_COUNT));
    };
    const handleRight = () => {
        setStartIdx((prev) => Math.min(maxIdx, prev + VISIBLE_COUNT));
    };

    // 슬라이드 이동용 transformX 계산 (163px(cover) + 12px(gap) = 175px)
    const slideX = home ? -(startIdx * SLIDE_UNIT) : 0;

    return (
        <section className="book-section">
            <div className="books">
                <div className={home ? "book-list-in-home slider-mode" : "book-list"}>
                    {home && simpleBooks.length > VISIBLE_COUNT ? (
                        <>
                            {startIdx > 0 && (
                                <button className="booklist-arrow left" onClick={handleLeft}>&lt;</button>
                            )}
                            {startIdx < maxIdx && (
                                <button className="booklist-arrow right" onClick={handleRight}>&gt;</button>
                            )}
                            <div
                                className="booklist-slider-track"
                                style={{
                                    display: 'flex',
                                    gap: `${BOOK_GAP}px`,
                                    transition: 'transform 0.5s cubic-bezier(0.4,0,0.2,1)',
                                    transform: `translateX(${slideX}px)`
                                }}
                            >
                                {simpleBooks.length === 0 ? (
                                    <p>{emptyMessage}</p>
                                ) : (
                                    simpleBooks.map((book) => (
                                        <div className={home ? "book-item-in-home" : "book-item"} key={book.id}>
                                            <div className="book-img-cover">
                                                <img
                                                    src={book.coverUrl || `https://via.placeholder.com/140x200?text=No+Image`}
                                                    alt={book.title}
                                                    className="book-img"
                                                />
                                            </div>
                                            <div className="book-info-cover">
                                                <div className="book-title">{book.title}</div>
                                                <div className="book-author">{book.author}</div>
                                            </div>
                                        </div>
                                    ))
                                )}
                            </div>
                        </>
                    ) : (
                        <>
                            {simpleBooks.length === 0 ? (
                                <p>{emptyMessage}</p>
                            ) : (
                                simpleBooks.map((book) => (
                                    <div className="book-item" key={book.id}>
                                        <div className="book-img-cover">
                                            <img
                                                src={book.coverUrl || `https://via.placeholder.com/140x200?text=No+Image`}
                                                alt={book.title}
                                                className="book-img"
                                            />
                                        </div>
                                        <div className="book-info-cover">
                                            <div className="book-title">{book.title}</div>
                                            <div className="book-author">{book.author}</div>
                                        </div>
                                    </div>
                                ))
                            )}
                        </>
                    )}
                </div>
            </div>
        </section>
    );
}

export default BookList