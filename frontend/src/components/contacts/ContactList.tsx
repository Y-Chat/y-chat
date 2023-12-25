import React, {useState} from 'react';
import {
    UnstyledButton,
    Group,
    Text,
    TextInput,
    rem,
    keys, Indicator, Avatar, ActionIcon, Stack, Center,
} from '@mantine/core';
import {IconSearch, IconX} from '@tabler/icons-react';
import {useAppStore} from "../../state/store";

interface Chat {
    id: number
    avatar: null | string,
    firstName: string,
    lastName: string,
    username: string,
    newMessages: number,
    date: Date
}

function filterData(data: Chat[], search: string) {
    const query = search.toLowerCase().trim();
    return data.filter((item) =>
        keys(data[0]).some((key) => {
            const elem: any = item[key]
            if (typeof elem == "string")
                return elem.toString().toLowerCase().includes(query)
        })
    );
}

const data: Chat[] = [
    {
        id: 1,
        avatar: null,
        firstName: "Niklas",
        lastName: "Mamtschur",
        username: "xXmamtschurXx",
        newMessages: 1,
        date: new Date()
    },
    {
        id: 2,
        avatar: null,
        firstName: "Benedikt",
        lastName: "Strobel",
        username: "strobel123",
        newMessages: 3,
        date: new Date()
    },
    {
        id: 3,
        avatar: null,
        firstName: "Ben",
        lastName: "Riegel",
        username: "ri3gel",
        newMessages: 0,
        date: new Date()
    },
    {
        id: 4,
        avatar: null,
        firstName: "Ben",
        lastName: "Riegel",
        username: "ri3gel",
        newMessages: 0,
        date: new Date()
    },
    {
        id: 5,
        avatar: null,
        firstName: "Ben",
        lastName: "Riegel",
        username: "ri3gel",
        newMessages: 0,
        date: new Date()
    }, {
        id: 6,
        avatar: null,
        firstName: "Ben",
        lastName: "Riegel",
        username: "ri3gel",
        newMessages: 1,
        date: new Date()
    },
    {
        id: 7,
        avatar: null,
        firstName: "Ben",
        lastName: "Riegel",
        username: "ri3gel",
        newMessages: 1,
        date: new Date()
    },
    {
        id: 8,
        avatar: null,
        firstName: "Ben",
        lastName: "Riegel",
        username: "ri3gel",
        newMessages: 0,
        date: new Date()
    },
    {
        id: 9,
        avatar: null,
        firstName: "Ben",
        lastName: "Riegel",
        username: "ri3gel",
        newMessages: 1,
        date: new Date()
    },
    {
        id: 10,
        avatar: null,
        firstName: "Ben",
        lastName: "Riegel",
        username: "ri3gel",
        newMessages: 1,
        date: new Date()
    },
    {
        id: 11,
        avatar: null,
        firstName: "Ben",
        lastName: "Riegel",
        username: "ri3gel",
        newMessages: 0,
        date: new Date()
    },
    {
        id: 12,
        avatar: null,
        firstName: "Ben",
        lastName: "Riegel",
        username: "ri3gel",
        newMessages: 0,
        date: new Date()
    },
    {
        id: 13,
        avatar: null,
        firstName: "Ben",
        lastName: "Riegel",
        username: "ri3gel",
        newMessages: 0,
        date: new Date()
    },
    {
        id: 14,
        avatar: null,
        firstName: "Ben",
        lastName: "Riegel",
        username: "ri3gel",
        newMessages: 0,
        date: new Date()
    },
    {
        id: 15,
        avatar: null,
        firstName: "Ben",
        lastName: "Riegel",
        username: "ri3gel",
        newMessages: 0,
        date: new Date()
    },
    {
        id: 16,
        avatar: null,
        firstName: "Ben",
        lastName: "Riegel",
        username: "ri3gel",
        newMessages: 0,
        date: new Date()
    },
    {
        id: 17,
        avatar: null,
        firstName: "Ben",
        lastName: "Riegel",
        username: "ri3gel",
        newMessages: 0,
        date: new Date()
    },
    {
        id: 18,
        avatar: null,
        firstName: "Ben",
        lastName: "Riegel",
        username: "ri3gel",
        newMessages: 0,
        date: new Date()
    },
    {
        id: 19,
        avatar: null,
        firstName: "Ben",
        lastName: "Riegel",
        username: "ri3gel",
        newMessages: 0,
        date: new Date()
    },
    {
        id: 20,
        avatar: null,
        firstName: "Ben",
        lastName: "Riegel",
        username: "ri3gel",
        newMessages: 0,
        date: new Date()
    }

];

interface ContactListProps {
    toggleNav: () => void
}

export function ContactList({toggleNav}: ContactListProps) {
    const [search, setSearch] = useState('');
    const [sortedData, setSortedData] = useState(data);
    const selectChat = useAppStore((state) => state.selectChat);


    const handleSearchChange = (event: React.ChangeEvent<HTMLInputElement>) => {
        const {value} = event.currentTarget;
        setSearch(value);
        setSortedData(filterData(data, value));
    };

    const rows = sortedData.map((row) => (
        <UnstyledButton key={row.id} onClick={() => {
            selectChat("TODOchatId");
            toggleNav();
        }}>
            <Group justify="space-between">
                <Group gap="sm">
                    <Indicator disabled={!row.newMessages}>
                        <Avatar size={40} src={row.avatar} radius={40}/>
                    </Indicator>
                    <div style={{marginLeft: 5}}>
                        <Text fz="sm" fw={500}>
                            {`${row.firstName} ${row.lastName}`}
                        </Text>
                        <Text c="dimmed" fz="xs">
                            {`@${row.username}`}
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
            mt={25}
            mb={25}
            pr={10}
            pl={10}
        >
            <Center>
                <Text c={"dimmed"}>Chats</Text>
            </Center>
            <TextInput
                placeholder="Search friends or chats"
                size="md"
                mb="md"
                leftSection={
                    search != "" ?
                        <ActionIcon variant="transparent" onClick={() => {
                            setSearch("")
                            setSortedData(filterData(data, ""));
                        }}>
                            <IconX style={{width: rem(16), height: rem(16)}} stroke={1.5}/>
                        </ActionIcon>
                        :
                        <IconSearch style={{width: rem(16), height: rem(16)}} stroke={1.5}/>
                }
                value={search}
                onChange={handleSearchChange}
            />
            {rows}
        </Stack>
    );
}