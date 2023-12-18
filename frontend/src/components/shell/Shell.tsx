import React from "react";
import {useAppStore} from "../../state/store";
import {Outlet} from "react-router-dom";


function Shell() {
    return (
        <div
            style={{
                height: `100vh`,
                width: "100vw"
            }}>
            <Outlet/>
        </div>
    );
}

export default Shell;