import React, {useRef, useState} from "react";
import {ActionIcon, Flex, ScrollArea} from "@mantine/core";
import MessageBubble from "./MessageBubble";
import {IconCircleChevronDown} from "@tabler/icons-react";
import {Message} from "../../model/Message";

function MessageList() {

    const messageGap = 15;

    const messages: Message[] = [
        {type: "text", message: "Message1", fromMe: true, status: "read"},
        {type: "text", message: "Message2", fromMe: false, status: "read"},
        {
            type: "text",
            message: "Message3 Message3 Message3 Message3 Message3 Message3 Message3 Message3 Message3Message3Message3 Message3 Message3 Message3 Message3",
            fromMe: true,
            status: "read"
        },
        {type: "text", message: "Message4", fromMe: true, status: "read"},
        {type: "text", message: "Message5", fromMe: false, status: "read"},
        {type: "text", message: "Message6", fromMe: false, status: "read"},
        {type: "text", message: "Message7Message7Message7", fromMe: false, status: "read"},
        {type: "text", message: "Message8", fromMe: true, status: "read"},
        {type: "text", message: "Message10", fromMe: true, status: "read"},
        {type: "text", message: "Message11", fromMe: false, status: "read"},
        {type: "text", message: "Message9", fromMe: true, status: "read"},
        {type: "text", message: "Message12", fromMe: true, status: "read"},
        {type: "text", message: "Message12", fromMe: true, status: "read"},
        {type: "text", message: "Message12", fromMe: true, status: "read"},
        {type: "text", message: "Message12", fromMe: true, status: "read"},
        {type: "text", message: "Message12", fromMe: true, status: "read"},
        {type: "text", message: "Message12", fromMe: true, status: "read"},
        {type: "text", message: "Message12", fromMe: true, status: "read"},
        {type: "text", message: "Message12", fromMe: true, status: "read"},
        {type: "text", message: "Message12", fromMe: true, status: "read"},
        {type: "text", message: "Message12", fromMe: true, status: "read"},
        {type: "text", message: "Message12", fromMe: true, status: "read"},
        {type: "text", message: "Message12", fromMe: true, status: "read"},
        {type: "text", message: "Message12", fromMe: true, status: "read"},
        {type: "text", message: "Message12", fromMe: true, status: "read"},
        {type: "text", message: "Message12", fromMe: true, status: "read"},
        {type: "text", message: "Message12", fromMe: true, status: "read"},
        {type: "text", message: "Message12", fromMe: true, status: "read"},
        {type: "text", message: "Message12", fromMe: true, status: "read"},
        {type: "text", message: "Message12", fromMe: true, status: "read"},
        {type: "text", message: "Message12", fromMe: true, status: "read"},
        {type: "text", message: "Message12", fromMe: true, status: "read"},
        {type: "text", message: "Message12", fromMe: true, status: "read"},
        {type: "text", message: "Message12", fromMe: true, status: "read"},
        {type: "text", message: "Message12", fromMe: true, status: "read"},
        {type: "text", message: "Message12", fromMe: true, status: "read"},
        {
            type: "media",
            mediaUrl: "https://img.freepik.com/free-photo/painting-mountain-lake-with-mountain-background_188544-9126.jpg?size=626&ext=jpg&ga=GA1.1.1546980028.1703635200&semt=sph",
            message: "Look at this. Wow.",
            fromMe: true,
            status: "read"
        },
        {type: "text", message: "Message12", fromMe: true, status: "read"},
        {
            type: "text",
            message: "Message13 Message13 Message13 Message13 Message13 Message13 Message13 Message13",
            fromMe: true,
            status: "read"
        },
    ];

    const viewport = useRef<HTMLDivElement>(null);

    const scrollToBottom = () => viewport.current!.scrollTo({top: viewport.current!.scrollHeight, behavior: 'smooth'});

    return (
        <ScrollArea type="scroll" scrollbarSize={2} scrollHideDelay={500} h={"80vh"} viewportRef={viewport}>
            <ActionIcon
                color={"dark"}
                c={"mainColors"}
                onClick={scrollToBottom}
                size={50}
                variant="filled"
                style={{
                    position: "absolute",
                    bottom: 0,
                    right: 0,
                    marginBottom: 20,
                    marginRight: 20,
                    zIndex: 1
                }}>
                <IconCircleChevronDown size={40}/>
            </ActionIcon>
            <Flex
                p={10}
                direction="column"
                align="flex-start"
                style={{
                    gap: messageGap,
                    flexGrow: 1,
                    flexShrink: 1,
                    flexBasis: 0,
                    overflowY: "auto"
                }}>

                {messages.map((msg, i) =>
                    <MessageBubble key={i} message={msg}/>
                )}
            </Flex>
        </ScrollArea>
    );
}

export default MessageList;