import React from "react";
import {ActionIcon, Center, Container, Group, Text} from "@mantine/core";
import {IconArchive, IconUserCancel, IconUsersPlus} from "@tabler/icons-react";
import {useNavigate} from "react-router-dom";
import Logo from "./Logo";

interface IconBarProps {
    toggleNav: () => void
}

export function IconBar({toggleNav}: IconBarProps) {
    const navigate = useNavigate();

    return (
        <>
            <Center style={{flexGrow: 1}}>
                <Logo style={{width:40}}></Logo>
                <Text fw={600} size={"lg"}>Chat</Text>
            </Center>

            <Container>
                <Group justify="flex-end" style={{flexGrow: 0}}>
                    <ActionIcon
                        variant={"transparent"}
                        onClick={() => {
                            toggleNav();
                            navigate("/newGroup");
                        }}
                    >
                        <IconUsersPlus/>
                    </ActionIcon>
                    <ActionIcon
                        variant={"transparent"}
                    >
                        <IconArchive/>
                    </ActionIcon>
                    <ActionIcon
                        variant={"transparent"}
                    >
                        <IconUserCancel/>
                    </ActionIcon>
                </Group>
            </Container>
        </>
    );
}