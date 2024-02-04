import React, {useEffect, useMemo, useState} from "react";
import {Chat} from "../../model/Chat";
import {useOutletContext} from "react-router-dom";
import {ShellOutletContext} from "../shell/ShellOutletContext";
import {ActionIcon, Avatar, Container, Group, Text} from "@mantine/core";
import {IconVideo} from "@tabler/icons-react";
import MessageList from "./MessageList";
import ChatTextArea from "./ChatTextArea";
import {useCallingStore} from "../../state/callingStore";
import {api} from "../../network/api";
import {useUserStore} from "../../state/userStore";
import {PageChatMemberDTO} from "../../api-wrapper";

interface ChatWindowProps {
    chat: Chat
}

export function ChatWindow({chat}: ChatWindowProps) {
    const {setHeader} = useOutletContext<ShellOutletContext>();
    const startCall = useCallingStore((state) => state.startCall);
    const user = useUserStore((state) => state.user)!;
    const [chatMembersFirstPage, setChatMembersFirstPage] = useState<PageChatMemberDTO | null>(null)

    useEffect(() => {
        api.getChatMembers({chatId: chat.id, userId: user.id, pageable: {page: 0, size: 10}})
            .then((x) => setChatMembersFirstPage(x))
            .catch((err) => {
                console.error(err);
                return null;
            })
    }, []);

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
                    <ActionIcon variant="transparent" c="lightgray" disabled={(chatMembersFirstPage?.content?.length ?? 0) > 2}>
                        {chat?.email ? <IconVideo onClick={() => {
                            const otherMember = chatMembersFirstPage?.content?.find((x) => x.userId !== user.id);
                            if(otherMember) {
                                startCall(otherMember.userId)
                            }
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
