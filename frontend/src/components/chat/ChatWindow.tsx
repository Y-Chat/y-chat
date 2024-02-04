import React, {useEffect} from "react";
import {Chat} from "../../model/Chat";
import {useOutletContext} from "react-router-dom";
import {ShellOutletContext} from "../shell/ShellOutletContext";
import {ActionIcon, Avatar, Container, Group, Text} from "@mantine/core";
import {IconUser, IconUsersGroup, IconVideo} from "@tabler/icons-react";
import MessageList from "./MessageList";
import ChatTextArea from "./ChatTextArea";
import {useCallingStore} from "../../state/callingStore";

interface ChatWindowProps {
    chat: Chat
}

export function ChatWindow({chat}: ChatWindowProps) {
    const {setHeader} = useOutletContext<ShellOutletContext>();
    const startCall = useCallingStore((state) => state.startCall);

    useEffect(() => {
        setHeader(
            <>
                <Group justify={"center"} gap={0}>
                    <Avatar src={null} radius={"xl"} mr={"xs"}>
                        {chat.groupInfo ? <IconUsersGroup/> : <IconUser/>}
                    </Avatar>
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
                            {`${chat.groupInfo ? chat.groupInfo?.description || "No Group Description" : chat.email}`}
                        </Text>
                    </div>
                </Group>

                <Container style={{flexGrow: 0}}>
                    <ActionIcon variant="transparent" c="lightgray">
                        {chat?.email ? <IconVideo onClick={() => {
                            startCall("2e0c5f8f-c782-394f-9388-52000aae64cf")
                        }}/> : undefined}
                    </ActionIcon>
                </Container>
            </>
        );
    }, [chat]);

    return (
        <>
            <MessageList chatId={chat.id}/>
            <ChatTextArea chatId={chat.id}/>
        </>
    );
}
