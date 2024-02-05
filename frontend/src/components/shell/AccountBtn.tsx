import React, {useEffect} from "react";
import {Avatar, Group, rem, Text, UnstyledButton, useMantineTheme} from "@mantine/core";
import {IconChevronRight, IconUser} from "@tabler/icons-react";
import {useUserStore} from "../../state/userStore";
import {useNavigate} from "react-router-dom";
import {useImagesStore} from "../../state/imagesStore";

interface AccountBtnProps {
    toggleNav: () => void
}

function AccountBtn({toggleNav}: AccountBtnProps) {
    const user = useUserStore((state) => state.user)!;
    const fetchImageUrl = useImagesStore((state) => state.fetchImageUrl);
    const avatarUrl = useImagesStore(state => state.cachedImages[user?.profilePictureId || ""]);
    const navigate = useNavigate();
    const theme = useMantineTheme();

    useEffect(() => {
        if (user.profilePictureId) {
            fetchImageUrl(user.profilePictureId);
        }
    }, [user])

    return (
        <UnstyledButton
            h={90}
            w={"100%"}
            onClick={() => {
                navigate('/account')
                toggleNav();
            }}>
            <Group>
                <Avatar
                    src={avatarUrl?.url}
                    radius="xl"
                >
                    <IconUser/>
                </Avatar>

                <div style={{flex: 1}}>
                    <Text size="sm" fw={500}>
                        {`${user?.firstName} ${user?.lastName}`}
                    </Text>

                    <Text c="dimmed" size="xs">
                        {`${user?.email}`}
                    </Text>
                </div>
                <Text c={theme.primaryColor}>{`${user?.balance}€`}</Text>
                <IconChevronRight style={{width: rem(14), height: rem(14)}} stroke={1.5}/>
            </Group>
        </UnstyledButton>
    );
}

export default AccountBtn;