import React from "react";
import {Avatar, Group, rem, Text, UnstyledButton} from "@mantine/core";
import {IconChevronRight} from "@tabler/icons-react";
import {useAppStore} from "../../state/store";
import {useNavigate} from "react-router-dom";

interface AccountBtnProps {
    toggleNav: () => void
}

function AccountBtn({toggleNav}: AccountBtnProps) {
    const user = useAppStore((state) => state.user)
    const navigate = useNavigate()
    return (
        <UnstyledButton
            onClick={() => {
                navigate('/account')
                toggleNav();
            }}
            p={"md"}
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
    );
}

export default AccountBtn;