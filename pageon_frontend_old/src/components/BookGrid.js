// src/components/BookGrid.jsx
import React from "react";
import "./BookGrid.css";

function BookGrid({ books, emptyMessage = "작품이 없습니다." }) {
  return (
    <section className="book-section">
      <div className="books">
        <div className="books-list">
          {books.length === 0 ? (
            <p>{emptyMessage}</p>
          ) : (
            books.map((book) => (
              <div key={book.id} className="book-item">
                <img
                  src={book.coverUrl || `https://via.placeholder.com/140x200?text=No+Image`}
                  alt={book.title}
                  className="book-img"
                />
                <div className="book-title">{book.title}</div>
                <div className="book-author">{book.author}</div>
              </div>
            ))
          )}
        </div>
      </div>
    </section>
  );
}

export default BookGrid;
