import React from "react";
import ContactListEntry from "./ContactListEntry";
import {Stack, UnstyledButton, Center, Text, TextInput} from "@mantine/core";
import {useAppStore} from "../../state/store";
import {IconSearch} from "@tabler/icons-react";

interface ContactListProps {
    toggleNav: () => void
}

function ContactList({toggleNav}: ContactListProps) {

    const contacts = [
        {
            avatar: null,
            firstName: "Niklas",
            lastName: "Mamtschur",
            username: "xXmamtschurXx",
            newMessages: 1
        },
        {
            avatar: null,
            firstName: "Benedikt",
            lastName: "Strobel",
            username: "strobel123",
            newMessages: 3
        },
        {
            avatar: null,
            firstName: "Ben",
            lastName: "Riegel",
            username: "ri3gel",
            newMessages: 0
        },
        {
            avatar: null,
            firstName: "Ben",
            lastName: "Riegel",
            username: "ri3gel",
            newMessages: 0
        },
        {
            avatar: null,
            firstName: "Ben",
            lastName: "Riegel",
            username: "ri3gel",
            newMessages: 0
        }, {
            avatar: null,
            firstName: "Ben",
            lastName: "Riegel",
            username: "ri3gel",
            newMessages: 1
        },
        {
            avatar: null,
            firstName: "Ben",
            lastName: "Riegel",
            username: "ri3gel",
            newMessages: 1
        },
        {
            avatar: null,
            firstName: "Ben",
            lastName: "Riegel",
            username: "ri3gel",
            newMessages: 0
        },
        {
            avatar: null,
            firstName: "Ben",
            lastName: "Riegel",
            username: "ri3gel",
            newMessages: 1
        },
        {
            avatar: null,
            firstName: "Ben",
            lastName: "Riegel",
            username: "ri3gel",
            newMessages: 1
        },
        {
            avatar: null,
            firstName: "Ben",
            lastName: "Riegel",
            username: "ri3gel",
            newMessages: 0
        },
        {
            avatar: null,
            firstName: "Ben",
            lastName: "Riegel",
            username: "ri3gel",
            newMessages: 0
        },
        {
            avatar: null,
            firstName: "Ben",
            lastName: "Riegel",
            username: "ri3gel",
            newMessages: 0
        },
        {
            avatar: null,
            firstName: "Ben",
            lastName: "Riegel",
            username: "ri3gel",
            newMessages: 0
        },
        {
            avatar: null,
            firstName: "Ben",
            lastName: "Riegel",
            username: "ri3gel",
            newMessages: 0
        },
        {
            avatar: null,
            firstName: "Ben",
            lastName: "Riegel",
            username: "ri3gel",
            newMessages: 0
        },
        {
            avatar: null,
            firstName: "Ben",
            lastName: "Riegel",
            username: "ri3gel",
            newMessages: 0
        },
        {
            avatar: null,
            firstName: "Ben",
            lastName: "Riegel",
            username: "ri3gel",
            newMessages: 0
        },
        {
            avatar: null,
            firstName: "Ben",
            lastName: "Riegel",
            username: "ri3gel",
            newMessages: 0
        },
        {
            avatar: null,
            firstName: "Ben",
            lastName: "Riegel",
            username: "ri3gel",
            newMessages: 0
        },
        {
            avatar: null,
            firstName: "Ben",
            lastName: "Riegel",
            username: "ri3gel",
            newMessages: 0
        },

    ]

    const selectChat = useAppStore((state) => state.selectChat);

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
                size="md"
                placeholder="Search friends or chats"
                leftSection={<IconSearch/>}
            />
            {contacts.map((contact, i) =>
                <UnstyledButton key={i} onClick={() => {
                    selectChat("TODOchatId");
                    toggleNav();
                }}>
                    <ContactListEntry
                        firstName={contact.firstName}
                        lastName={contact.lastName}
                        avatar={contact.avatar}
                        username={contact.username}
                        newMessages={contact.newMessages}
                    />
                </UnstyledButton>
            )}
        </Stack>
    );
}

export default ContactList;