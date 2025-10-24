import React from "react";
import "./Footer.css"
import { Link } from "react-router-dom";

function Footer() {

    return (
        <div className="footer-container">
            <footer className="footer">
                <div className="cs-link-list">
                    <Link to={"/"}>고객센터</Link>
                    <Link to={"/"}>공지사항</Link>
                </div>
                <div className="footer-inner">
                    &copy; {new Date().getFullYear()} 웹툰 & 웹소설. All rights reserved.
                </div>
            </footer>
        </div>
    )
}

export default Footer