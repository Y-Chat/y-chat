import React from "react";
import {ActionIcon, Container, Flex, Group, Text} from "@mantine/core";
import {IconArchive, IconUserCancel, IconUsersGroup} from "@tabler/icons-react";
import {useNavigate} from "react-router-dom";
import Logo from "../common/Logo";

interface IconBarProps {
    toggleNav: () => void
}

export function IconBar({toggleNav}: IconBarProps) {
    const navigate = useNavigate();

    return (
        <>
            <Flex style={{flexGrow: 1}} justify={"flex-start"} align={"center"} gap={0}>
                <Logo style={{width: 40, marginLeft: 16}}></Logo>
                <Text fw={600} size={"lg"}>Chat</Text>
            </Flex>

            <Container>
                <Group justify="flex-end" style={{flexGrow: 0}}>
                    <ActionIcon
                        c={"white"}
                        variant={"transparent"}
                        onClick={() => {
                            toggleNav();
                            navigate("/newGroup");
                        }}
                    >
                        <IconUsersGroup/>
                    </ActionIcon>
                    <ActionIcon
                        c={"white"}
                        variant={"transparent"}
                    >
                        <ActionIcon disabled>
                            <IconArchive/>
                        </ActionIcon>
                    </ActionIcon>
                    <ActionIcon
                        //c={"gray"}
                        variant={"transparent"}
                        disabled
                    >
                        <IconUserCancel/>
                    </ActionIcon>
                </Group>
            </Container>
        </>
    );
}
