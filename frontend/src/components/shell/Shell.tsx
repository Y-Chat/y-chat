import React from "react";
import {useAppStore} from "../../state/store";
import ChatMain from "../chat/ChatMain";


function Shell() {

    const selectedChat = useAppStore((state) => state.selectedChat);

    return (
        <div
            style={{
                height: `100vh`,
                width: "100vw"
            }}>
            <ChatMain/>
        </div>
    );
}

export default Shell;