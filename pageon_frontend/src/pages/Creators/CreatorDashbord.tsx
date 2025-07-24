import React from "react";
import "../../styles/reset.css"
import "../../styles/global.css"
import CreatorSidebar from "../../components/CreatorSidebar";
import "./Creators.css"
function CreatorDashbord() {

    return (
        <div className="main-container">
            <main className="sidebar-main">
                <CreatorSidebar />
                <div className="creator-right-wrap">
                    
                </div>
            </main>
        </div>
    )
}


export default CreatorDashbord;