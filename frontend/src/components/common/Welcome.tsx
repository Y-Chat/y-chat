import React, {useEffect} from "react";
import {Center, Container, Group, rem, Stack, Text, useMantineTheme} from "@mantine/core";
import Logo from "./Logo";
import {useOutletContext} from "react-router-dom";
import {ShellOutletContext} from "../shell/ShellOutletContext";
import {HelloSVG} from "./HelloSVG";
import {useUserStore} from "../../state/userStore";

export function Welcome() {
    const {setHeader} = useOutletContext<ShellOutletContext>();
    const user = useUserStore(state => state.user)!;
    const theme = useMantineTheme();

    useEffect(() => {
        setHeader(
            <Center>
                <Group gap={5}>
                    <Text size="xl" fz="lg">Hey</Text>
                    <Text fw={600} size="xl" fz="lg" c={theme.colors[theme.primaryColor][6]}>{user.firstName}</Text>
                    <Text size="xl" fz="lg">!</Text>
                </Group>
            </Center>
        );
    }, []);

    return (
        <Container pt={rem(100)}>
            <Stack align={"center"} gap={"lg"} justify="flex-start">
                <HelloSVG style={{width: rem(220)}}/>
                <Text fw={900} size={rem(30)} ta={"center"}>Welcome to {<Logo
                    style={{width: rem(40), position: "relative", left: 4}}/>}Chat,</Text>
                <Text size={"xl"} ta={"center"}>Start a new conversation with friends or continue an existing
                    one.</Text>
            </Stack>
        </Container>
    );
}
