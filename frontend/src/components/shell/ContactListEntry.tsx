import React from "react";
import {Avatar, Group, Indicator, Text, UnstyledButton, useMantineTheme} from "@mantine/core";
import {Chat} from "../../model/Chat";
import {useNavigate, useParams} from "react-router-dom";
import {useMessagesStore} from "../../state/messagesStore";
import {Message} from "../../model/Message";
import {IconUser, IconUsersGroup} from "@tabler/icons-react";

interface ContactListEntryProps {
    chat: Chat,
    toggleNav: () => void
}

export function ContactListEntry({chat, toggleNav}: ContactListEntryProps) {
    const messages = useMessagesStore((state) => state.messages[chat.id]);
    const navigate = useNavigate();
    const {chatId} = useParams();
    const theme = useMantineTheme();

    let lastMessage: Message | undefined;
    let lastDate = new Date()

    if (messages && messages.length > 0) {
        lastMessage = messages[0];
        lastDate = lastMessage.date
    }

    function renderLastMessage(){
        if (!lastMessage){
            return "";
        }

        if (lastMessage.type === "text"){
            return lastMessage.message
        } else {
            <Text inherit fs="italic">Image</Text>
        }
    }

    return (
        <UnstyledButton
            p={"md"}
            w={"100%"}
            style={{
                background: chatId == chat.id ? theme.colors["dark"][6] : undefined,
                borderRadius: 5
            }}
            onClick={() => {
                navigate(`/chat/${chat.id}`);
                toggleNav();
            }}>
            <Group justify="space-between" gap={0}>
                <Group gap="sm">
                    <Indicator disabled={!chat.newMessages} style={{flexGrow: 0}}>
                        <Avatar size={40} src={chat.avatar} radius={40}>
                            {chat.groupInfo ? <IconUsersGroup/> : <IconUser/>}
                        </Avatar>
                    </Indicator>
                    <div style={{marginLeft: 5}}>
                        <Text fz="sm" fw={500}>
                            {`${chat.name}`}
                        </Text>
                        <Text c="dimmed" fz="xs" style={{
                            height: "2em",
                            width: 220,
                            overflow: "hidden",
                            whiteSpace: "nowrap",
                            textOverflow: "ellipsis"
                        }}>
                            {renderLastMessage()}
                        </Text>
                    </div>
                </Group>
                <Text c="dimmed"
                      fz="xs">{`${lastDate.getDate()}.${lastDate.getMonth()}.${lastDate.getFullYear()}`}</Text>
            </Group>
        </UnstyledButton>
    );
}