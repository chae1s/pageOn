import { useEffect } from "react";
import { useLocation } from "react-router-dom";

function ScrollToTop() {
    const { pathname, state } = useLocation();

    useEffect(() => {
        const scrollPosition = sessionStorage.getItem("scrollPosition");

        if (!state?.scrollTo && !scrollPosition) {
            window.scrollTo(0, 0);
        }
    }, [pathname, state]);

    return null;
}

export default ScrollToTop;