import React, {useEffect} from "react";
import {Chat} from "../../model/Chat";
import {useOutletContext} from "react-router-dom";
import {ShellOutletContext} from "../shell/ShellOutletContext";
import {ActionIcon, Avatar, Container, Group, Text} from "@mantine/core";
import {IconVideo} from "@tabler/icons-react";
import MessageList from "./MessageList";
import ChatTextArea from "./ChatTextArea";
import {useChatsStore} from "../../state/chatsStore";

interface ChatWindowProps {
    chat: Chat
}

export function ChatWindow({chat}: ChatWindowProps) {
    const [setHeader] = useOutletContext<ShellOutletContext>();

    useEffect(() => {
        setHeader(
            <>
                <Group justify={"center"} gap={0}>
                    {chat ? <Avatar src={null} radius={"xl"} mr={"xs"}/> : undefined}
                    <div style={{marginLeft: 5}}>
                        <Text fz="sm" fw={500}>
                            {`${chat.name}`}
                        </Text>
                        <Text c="dimmed" fz="xs" w={150} style={{
                            height: "1.5em",
                            width: 155,
                            overflow: "hidden",
                            whiteSpace: "nowrap",
                            textOverflow: "ellipsis"
                        }}>
                            {`${chat.groupInfo ? chat.groupInfo?.description : chat.email}`}
                        </Text>
                    </div>
                </Group>

                <Container style={{flexGrow: 0}}>
                    <ActionIcon variant="transparent" c="lightgray">
                        {chat?.email ? <IconVideo/> : undefined}
                    </ActionIcon>
                </Container>
            </>
        );
    }, [chat]);

    return (
        <>
            <MessageList chatId={chat.id}/>
            <div style={{
                position: "fixed",
                bottom: 0,
                height: 90,
                width: "100%",
                zIndex: 1
            }}>
                <ChatTextArea chatId={chat.id}/>
            </div>
        </>
    );
}