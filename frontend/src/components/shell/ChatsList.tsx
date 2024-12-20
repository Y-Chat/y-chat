import React, {useEffect, useMemo, useState} from 'react';
import {ActionIcon, Center, rem, Stack, Text, TextInput,} from '@mantine/core';
import {IconMessageOff, IconSearch, IconX} from '@tabler/icons-react';
import {useChatsStore} from "../../state/chatsStore";
import {Chat} from "../../model/Chat";
import {NewDirectChat} from "../newChat/NewDirectChat";
import {ContactListEntry} from "./ContactListEntry";
import {useMessagesStore} from "../../state/messagesStore";

interface ContactListProps {
    toggleNav: () => void
}

export function ChatsList({toggleNav}: ContactListProps) {
    const [search, setSearch] = useState('');
    const chats = useChatsStore((state) => state.chats);
    const fetchChats = useChatsStore(state => state.fetchChats);

    function fetchChatsForFirstTime() {
        const existingChatIds: { [id: string] : boolean; } = {};
        chats.forEach((x) => existingChatIds[x.id] = true)

        fetchChats().then(() => {
            useChatsStore
                .getState()
                .chats
                .filter((x) => !(x.id in existingChatIds))
                .forEach(c => useMessagesStore.getState().fetchMoreMessagesByChat(c.id, "FUTURE", true));
        })
    }

    useEffect(() => {
        fetchChatsForFirstTime();
    }, []);

    const sortedChats = useMemo(() => {
        const query = search.toLowerCase().trim();
        const filtered = chats.filter((item) => {
                return item.name.toLowerCase().includes(query);
            }
        );
        return filtered.sort((a, b) => {
            if (!b.date && !a.date) {
                return 0;
            }

            if (!b.date) {
                return -1;
            }

            if (!a.date) {
                return 1;
            }

            return b.date.getTime() - a.date.getTime();
        });
    }, [search, chats])

    const rows = sortedChats.map(chat => {
        return (
            <ContactListEntry key={chat.id} toggleNav={toggleNav} chat={chat}/>
        );
    })

    return (
        <Stack
            justify="flex-start"
            mt="md"
            mb="md"
            p={0}
            gap={0}
        >
            <TextInput
                placeholder="Search chats or add friends"
                size="md"
                w="100%"
                pl={"md"}
                pr={"md"}
                pb={"md"}
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
                    <Stack c={"dimmed"} justify="start" align="center" gap={0}>
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
