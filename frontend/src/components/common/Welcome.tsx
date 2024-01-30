import React, {useEffect} from "react";
import {Center, Container, rem, Stack, Text} from "@mantine/core";
import Logo from "../shell/Logo";
import {useOutletContext} from "react-router-dom";
import {ShellOutletContext} from "../shell/ShellOutletContext";

export function Welcome() {
    const [setHeader] = useOutletContext<ShellOutletContext>();

    useEffect(() => {
        setHeader(
            <Center>
                <Text fz="lg">Welcome</Text>
            </Center>
        );
    }, []);

    return (
        <Container pt={"50%"}>
            <Stack align={"center"}>
                <Logo style={{width: rem(120)}}/>
                <Text ta={"center"}>Welcome to Y-Chat. Start a new conversation with friends or select an existing one!</Text>
            </Stack>
        </Container>
    );
}