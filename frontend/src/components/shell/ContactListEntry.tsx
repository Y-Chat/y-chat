import React from "react";
import {Avatar, Group, Indicator, Text, UnstyledButton} from "@mantine/core";
import {Chat} from "../../model/Chat";
import {useNavigate} from "react-router-dom";
import {useMessagesStore} from "../../state/messagesStore";

interface ContactListEntryProps {
    chat: Chat,
    toggleNav: () => void
}

export function ContactListEntry({chat, toggleNav}: ContactListEntryProps) {
    const messages = useMessagesStore((state) => state.messages[chat.id]);
    const navigate = useNavigate();

    let lastMessage = "";
    let lastDate = new Date();

    if (messages && messages.length > 0) {
        lastMessage = messages[0].message;
        lastDate = messages[0].date;
    }

    return (
        <UnstyledButton onClick={() => {
            navigate(`/chat/${chat.id}`);
            toggleNav();
        }}>
            <Group justify="space-between" gap={0}>
                <Group gap="sm">
                    <Indicator disabled={!chat.newMessages} style={{flexGrow: 0}}>
                        <Avatar size={40} src={chat.avatar} radius={40}/>
                    </Indicator>
                    <div style={{marginLeft: 5}}>
                        <Text fz="sm" fw={500}>
                            {`${chat.name}`}
                        </Text>
                        <Text c="dimmed" fz="xs" style={{
                            height: "1.5em",
                            width: 220,
                            overflow: "hidden",
                            whiteSpace: "nowrap",
                            textOverflow: "ellipsis"
                        }}>
                            {`${lastMessage}`}
                        </Text>
                    </div>
                </Group>
                <Text c="dimmed"
                      fz="xs">{`${lastDate.getDate()}.${lastDate.getMonth()}.${lastDate.getFullYear()}`}</Text>
            </Group>
        </UnstyledButton>
    );
}