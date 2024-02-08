import React, {useEffect} from "react";
import {Avatar, Group, Indicator, Text, UnstyledButton, useMantineTheme} from "@mantine/core";
import {Chat} from "../../model/Chat";
import {useNavigate, useParams} from "react-router-dom";
import {IconUser, IconUsersGroup} from "@tabler/icons-react";
import {useImagesStore} from "../../state/imagesStore";

interface ContactListEntryProps {
    chat: Chat,
    toggleNav: () => void,
}

export function ContactListEntry({chat, toggleNav}: ContactListEntryProps) {
    const avatarUrl = useImagesStore((state) => state.cachedImages[chat.avatarId || ""]);
    const fetchImageUrl = useImagesStore((state) => state.fetchImageUrl);
    const navigate = useNavigate();
    const {chatId} = useParams();
    const theme = useMantineTheme();

    useEffect(() => {
        if (chat.avatarId) {
            fetchImageUrl(chat.avatarId);
        }
    }, []);

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
                    <Indicator disabled={chat.newestReadMessageDate !== null && chat.date !== undefined && chat.newestReadMessageDate >= chat.date} style={{flexGrow: 0}}>
                        <Avatar size={40} src={avatarUrl?.url} radius={40}>
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
                            <Text inherit fs="italic">{chat.lastMessage || ""}</Text>
                        </Text>
                    </div>
                </Group>
                {chat.date &&
                    <Text c="dimmed"

                          fz="xs">
                        {
                            chat.date.toDateString() === new Date().toDateString() ?
                                `${chat.date.getHours()}:${chat.date.getMinutes()}`
                                : `${chat.date.getDate()}.${chat.date.getMonth()+1}.${chat.date.getFullYear()}`
                        }
                    </Text>}
            </Group>
        </UnstyledButton>
    );
}
