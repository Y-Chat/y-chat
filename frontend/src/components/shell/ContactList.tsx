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

interface ContactListProps {
    toggleNav: () => void
}

export function ContactList({toggleNav}: ContactListProps) {
    const [search, setSearch] = useState('');
    const navigate = useNavigate();
    const user = useUserStore((state) => state.user)!;
    const chats = useChatsStore((state) => state.chats);
    const [sortedChats, setSortedChats] = useState<Chat[]>(chats);
    const fetchChats = useChatsStore((state) => state.fetchChats);

    function filterData() {
        const query = search.toLowerCase().trim();
        const filtered = chats.filter((item) => {
                return (item.name + item.email).toLowerCase().includes(query)
                // return Object.values(item).some(val => {
                //     if (typeof val == "string")
                //         return val.toString().toLowerCase().includes(query)
                // })
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
    }, [search])


    useEffect(() => {
        filterData();
    }, [chats]);

    const rows = sortedChats.map((row, i) => (
        <UnstyledButton key={i} onClick={() => {
            navigate(`/chat/${row.id}`);
            toggleNav();
        }}>
            <Group justify="space-between" gap={0}>
                <Group gap="sm">
                    <Indicator disabled={!row.newMessages} style={{flexGrow: 0}}>
                        <Avatar size={40} src={row.avatar} radius={40}/>
                    </Indicator>
                    <div style={{marginLeft: 5}}>
                        <Text fz="sm" fw={500}>
                            {`${row.name}`}
                        </Text>
                        <Text c="dimmed" fz="xs" style={{
                            height: "1.5em",
                            width: 220,
                            overflow: "hidden",
                            whiteSpace: "nowrap",
                            textOverflow: "ellipsis"
                        }}>
                            {`${row.lastMessage}`}
                        </Text>
                    </div>
                </Group>
                <Text c="dimmed"
                      fz="xs">{`${row.date.getDate()}.${row.date.getMonth()}.${row.date.getFullYear()}`}</Text>
            </Group>
        </UnstyledButton>
    ));

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