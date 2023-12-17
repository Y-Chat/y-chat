import React from "react";
import {Avatar, Divider, Group, Modal, Paper, rem, Text, UnstyledButton} from "@mantine/core";
import {IconChevronRight} from "@tabler/icons-react";
import {useDisclosure} from "@mantine/hooks";
import {useAppStore} from "../../state/store";
import {AccountMain} from "../account/AccountMain";

interface AccountBtnProps{
    toggleNav: () => void
}

function AccountBtn({toggleNav}: AccountBtnProps) {
    const [opened, {open, close}] = useDisclosure(false);
    const user = useAppStore((state) => state.user)
    return (
        <>
            <Modal opened={opened} onClose={close} overlayProps={{backgroundOpacity: 0.5, blur: 4}} title="Account Info">
                <AccountMain/>
            </Modal>
            <Paper w={"100%"}>
                <UnstyledButton
                    p={"md"}
                    onClick={open}
                    style={{
                        display: "block",
                        width: "100%"
                    }}>
                    <Group>
                        <Avatar
                            src={user?.avatar}
                            radius="xl"
                        />

                        <div style={{flex: 1}}>
                            <Text size="sm" fw={500}>
                                {`${user?.firstName} ${user?.lastName}`}
                            </Text>

                            <Text c="dimmed" size="xs">
                                {`@${user?.username}`}
                            </Text>
                        </div>
                        <Text c="green">{`${user?.balance}€`}</Text>
                        <IconChevronRight style={{width: rem(14), height: rem(14)}} stroke={1.5}/>
                    </Group>
                </UnstyledButton>
                <Divider/>
            </Paper>

        </>

    );
}

export default AccountBtn;