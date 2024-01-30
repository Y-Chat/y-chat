import React, {useEffect} from "react";
import {ActionIcon, Avatar, Container, Group, Text} from "@mantine/core";
import MessageList from "./MessageList";
import ChatTextArea from "./ChatTextArea";
import {IconVideo} from "@tabler/icons-react";
import {accessToken, api} from "../../network/api";
import {useChatsStore} from "../../state/chatsStore";
import {useOutletContext} from "react-router-dom";
import {ShellOutletContext} from "../shell/ShellOutletContext";

function ChatMain() {
    const selectedChat = useChatsStore(state => state.selectedChat);
    const [setHeader] = useOutletContext<ShellOutletContext>();

    useEffect(() => {
        api.getMessages({chatId: 'b883492e-cb45-484e-895a-0703700deac7', fromDate: new Date(2000, 0, 1)})
            .then(r => {
                console.log(JSON.stringify(r))
            })
            .catch(e => console.error(e))
        if (!!accessToken) {
            api.updateToken({notificationToken: accessToken}).catch((x) => console.error(x))
        }
    }, [])

    useEffect(() => {
        setHeader(
            <>
                <Group justify={"center"} gap={0}>
                    <Avatar src={null} radius={"xl"} mr={"xs"}/>
                    <div style={{marginLeft: 5}}>
                        <Text fz="sm" fw={500}>
                            {`${selectedChat!.name}`}
                        </Text>
                        <Text c="dimmed" fz="xs" w={150} style={{
                            height: "1.5em",
                            width: 155,
                            overflow: "hidden",
                            whiteSpace: "nowrap",
                            textOverflow: "ellipsis"
                        }}>
                            {selectedChat!.groupInfo ? `${selectedChat?.groupInfo?.description}` : `@${selectedChat?.email}`}
                        </Text>
                    </div>
                </Group>

                <Container style={{flexGrow: 0}}>
                    <ActionIcon variant="transparent" c="lightgray">
                        <IconVideo size={"sm"}/>
                    </ActionIcon>
                </Container>
            </>
        );
    }, []);

    return (
        <>
            {!selectedChat ? <Text>Welcome to Y-Chat</Text> :
                <>
                    <MessageList/>
                    <div style={{
                        position: "fixed",
                        bottom: 0,
                        height: 90,
                        width: "100vw",
                        zIndex: 1
                    }}>
                        <ChatTextArea/>
                    </div>
                </>
            }
        </>
    );
}

export default ChatMain;
