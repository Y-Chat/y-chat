import React, {useEffect, useState} from 'react';
import {
    UnstyledButton,
    Group,
    Text,
    TextInput,
    rem,
    Indicator,
    Avatar,
    ActionIcon,
    Stack,
    Center,
} from '@mantine/core';
import {
    IconMessageOff,
    IconSearch,
    IconX
} from '@tabler/icons-react';
import {useUserStore} from "../../state/userStore";
import {useNavigate} from "react-router-dom";
import {useChatsStore} from "../../state/chatsStore";
import {Chat} from "../../model/Chat";
import {NewDirectChat} from "../newChat/NewDirectChat";
import {useMessagesStore} from "../../state/messagesStore";
import {ContactListEntry} from "./ContactListEntry";

interface ContactListProps {
    toggleNav: () => void
}

export function ContactList({toggleNav}: ContactListProps) {
    const [search, setSearch] = useState('');
    const chats = useChatsStore((state) => state.chats);
    const [sortedChats, setSortedChats] = useState<Chat[]>(chats);
    const fetchChats = useChatsStore((state) => state.fetchChats);

    function filterData() {
        const query = search.toLowerCase().trim();
        const filtered = chats.filter((item) => {
                return (item.name + item.email).toLowerCase().includes(query)
            }
        );
        filtered.sort((a, b) => b.date.getTime() - a.date.getTime());
        setSortedChats(filtered);
    }

    useEffect(() => {
        fetchChats();
    }, []);

    useEffect(() => {
        filterData();
    }, [search, chats]);

    const rows = sortedChats.map(chat => <ContactListEntry toggleNav={toggleNav} chat={chat}/>)

    return (
        <Stack
            justify="flex-start"
            gap={25}
            mt="md"
            mb="md"
            p={0}
        >
            {/*<Center>*/}
            {/*    <Text c={"dimmed"}>Chats</Text>*/}
            {/*</Center>*/}
            <TextInput
                placeholder="Search chats or add friends"
                size="md"
                w="100%"
                rightSection={
                    <NewDirectChat email={search}/>
                }
                leftSection={
                    search != "" ?
                        <ActionIcon variant="transparent" onClick={() => {
                            setSearch("");
                        }}>
                            <IconX style={{width: rem(16), height: rem(16)}} stroke={1.5}/>
                        </ActionIcon>
                        :
                        <IconSearch style={{width: rem(16), height: rem(16)}} stroke={1.5}/>
                }
                value={search}
                onChange={(e) => {
                    setSearch(e.currentTarget.value);
                }}
            />
            {rows.length <= 0 ?
                <Center>
                    <Stack c={"dimmed"} justify="start" align="center" gap={5}>
                        <IconMessageOff/>
                        <Text>No Chats.</Text>
                    </Stack>
                </Center>
                :
                rows
            }
        </Stack>
    );
}