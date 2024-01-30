import React from "react";
import {Avatar, Group, rem, Text, UnstyledButton} from "@mantine/core";
import {IconChevronRight} from "@tabler/icons-react";
import {useUserStore} from "../../state/userStore";
import {useNavigate} from "react-router-dom";

interface AccountBtnProps {
    toggleNav: () => void
}

function AccountBtn({toggleNav}: AccountBtnProps) {
    const user = useUserStore((state) => state.user)
    const navigate = useNavigate();
    return (
        <UnstyledButton
            h={"100%"}
            w={"100%"}
            onClick={() => {
                navigate('/account')
                toggleNav();
            }}>
            <Group>
                <Avatar
                    src={user?.profilePictureId}
                    radius="xl"
                />

                <div style={{flex: 1}}>
                    <Text size="sm" fw={500}>
                        {`${user?.firstName} ${user?.lastName}`}
                    </Text>

                    <Text c="dimmed" size="xs">
                        {`${user?.email}`}
                    </Text>
                </div>
                <Text c="green">{`${user?.balance}€`}</Text>
                <IconChevronRight style={{width: rem(14), height: rem(14)}} stroke={1.5}/>
            </Group>
        </UnstyledButton>
    );
}

export default AccountBtn;