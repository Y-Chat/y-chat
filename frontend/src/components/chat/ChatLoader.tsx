import React, {useEffect, useState} from "react";
import {useChatsStore} from "../../state/chatsStore";
import {Chat} from "../../model/Chat";
import {useUserStore} from "../../state/userStore";
import {ChatWindow} from "./ChatWindow";
import {useParams} from "react-router-dom";
import {NotFound} from "../404/NotFound";

// This component serves as a wrapper for the actual chat Window. It takes makes sure the chat is available and loaded.
function ChatLoader() {
    const getChat = useChatsStore(state => state.getChat);
    const [chat, setChat] = useState<Chat | null>(null);
    const { chatId } = useParams();

    useEffect(() => {
        if (chatId){
            getChat(chatId).then(c => {
                if (c) {
                    setChat(c);
                }
            })
        }
    }, [chatId]);

    return (
        <>
            {chat ? <ChatWindow chat={chat}/> : <NotFound/>}
        </>
    );
}

export default ChatLoader;
