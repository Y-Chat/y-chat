import React, {useRef, useState} from "react";
import {ActionIcon, Flex, ScrollArea} from "@mantine/core";
import MessageBubble from "./MessageBubble";
import {IconCircleChevronDown} from "@tabler/icons-react";

function MessageList() {

    const messageGap = 15;

    const messages = [
        {msg: "Message1", fromMe: true},
        {msg: "Message2", fromMe: false},
        {
            msg: "Message3 Message3 Message3 Message3 Message3 Message3 Message3 Message3 Message3Message3Message3 Message3 Message3 Message3 Message3",
            fromMe: true
        },
        {msg: "Message4", fromMe: true},
        {msg: "Message5", fromMe: false},
        {msg: "Message6", fromMe: false},
        {msg: "Message7Message7Message7", fromMe: false},
        {msg: "Message8", fromMe: true},
        {msg: "Message10", fromMe: true},
        {msg: "Message11", fromMe: false},
        {msg: "Message9", fromMe: true},
        {msg: "Message12", fromMe: true},
        {msg: "Message12", fromMe: true},
        {msg: "Message12", fromMe: true},
        {msg: "Message12", fromMe: true},
        {msg: "Message12", fromMe: true},
        {msg: "Message12", fromMe: true},
        {msg: "Message12", fromMe: true},
        {msg: "Message12", fromMe: true},
        {msg: "Message12", fromMe: true},
        {msg: "Message12", fromMe: true},
        {msg: "Message12", fromMe: true},
        {msg: "Message12", fromMe: true},
        {msg: "Message12", fromMe: true},
        {msg: "Message12", fromMe: true},
        {msg: "Message12", fromMe: true},
        {msg: "Message12", fromMe: true},
        {msg: "Message12", fromMe: true},
        {msg: "Message12", fromMe: true},
        {msg: "Message12", fromMe: true},
        {msg: "Message12", fromMe: true},
        {msg: "Message12", fromMe: true},
        {msg: "Message12", fromMe: true},
        {msg: "Message12", fromMe: true},
        {msg: "Message12", fromMe: true},
        {msg: "Message12", fromMe: true},
        {msg: "Message12", fromMe: true},
        {msg: "Message12", fromMe: true},
        {msg: "Message13 Message13 Message13 Message13 Message13 Message13 Message13 Message13", fromMe: true},
    ];


    const [showScrollBtn, setShowScrollBtn] = useState(false);
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
                    <MessageBubble key={i} message={msg.msg} fromMe={msg.fromMe}/>
                )}
            </Flex>
        </ScrollArea>
    );
}

export default MessageList;