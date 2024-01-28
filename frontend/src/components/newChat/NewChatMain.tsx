import React, {useState} from "react";
import {
    Center,
    Container,
    Divider,
    Group, rem,
    SegmentedControl,
    Stack,
    Text,
} from "@mantine/core";
import MenuDrawer from "../menu/MenuDrawer";
import {IconUser, IconUsersGroup} from "@tabler/icons-react";
import {NewDirectChat} from "./NewDirectChat";
import {NewGroupChat} from "./NewGroupChat";

export function NewChatMain() {
    // d -> direct chat, g -> group chat
    const [chatType, setChatType] = useState('d');

    return (
        <>
            <header>
                <div style={{
                    height: `${10}vh`,
                    width: "100%",
                    zIndex: 1,
                }}>
                    <Group justify="space-between" pl={10} h={"100%"} pr={10}>
                        <MenuDrawer/>
                        <Text fz="xl" fw={500}>Start a new {chatType == "d" ? "direct" : "group"} chat</Text>
                        <span/>
                    </Group>
                    <Divider/>
                </div>
            </header>

            <Container p='md'>
                <Stack justify="flex-start" align="stretch">
                    <SegmentedControl
                        value={chatType}
                        onChange={setChatType}
                        fullWidth
                        size="md"
                        data={[
                            {
                                label: <Center style={{gap: 10}}>
                                    <IconUser style={{width: rem(16), height: rem(16)}}/>
                                    <span>Direct Chat</span>
                                </Center>,
                                value: "d"
                            },
                            {
                                label: <Center style={{gap: 10}}>
                                    <IconUsersGroup style={{width: rem(16), height: rem(16)}}/>
                                    <span>Group</span>
                                </Center>,
                                value: "g"
                            },
                        ]}/>
                </Stack>
                <Container mt="md">
                    {chatType == "d" ? <NewDirectChat/> : <NewGroupChat/>}
                </Container>
            </Container>
        </>
    );
}