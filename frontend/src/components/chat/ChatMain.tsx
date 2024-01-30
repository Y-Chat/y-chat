import React, {useEffect, useState} from "react";
import {LoadingOverlay} from "@mantine/core";
import {useChatsStore} from "../../state/chatsStore";
import {Chat} from "../../model/Chat";
import {useUserStore} from "../../state/userStore";
import {ChatWindow} from "./ChatWindow";

// This component serves as a wrapper for the actual chat Window. It takes makes sure the chat is available and loaded.
function ChatMain() {
    const selectedChatId = useChatsStore(state => state.selectedChatId);
    const getChat = useChatsStore(state => state.getChat);
    const user = useUserStore(state => state.user)!;
    const [chat, setChat] = useState<Chat | null>(null);

    useEffect(() => {
        getChat(selectedChatId, user.id).then(c => {
            if (c) {
                setChat(c);
            }
        })
    }, [selectedChatId]);

    return (
        <>
            <LoadingOverlay w={"100%"} visible={!chat} overlayProps={{radius: 0, blur: 10, zIndex: 1000}}/>
            {chat ? <ChatWindow chat={chat}/> : undefined}
        </>
    );
}

export default ChatMain;
