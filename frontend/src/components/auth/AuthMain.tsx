import React, {useState} from "react";
import {Center, Container, Divider, Group, Paper, rem,} from "@mantine/core";
import Logo from "../common/Logo";
import {useUserStore} from "../../state/userStore";
import {GoogleButton} from "./GoogleButton";
import {AppleButton} from "./AppleButton";
import auth from "../../firebase/auth";
import {api} from "../../network/api";
import {showErrorNotification} from "../../notifications/notifications";
import getUuidByString from "uuid-by-string";
import {EmailForm} from "./EmailForm";

function AuthMain() {
    const setUser = useUserStore((state) => state.setUser)
    const [userLoading, setUserLoading] = useState<boolean>(false)

    function login() {
        const fbUser = auth.currentUser
        if (fbUser) {
            api.getUser({userId: getUuidByString(fbUser.uid, 3)})
                .then(user => {
                    localStorage.clear();
                    setUser({
                        id: user.id,
                        firstName: user.userProfileDTO.firstName,
                        lastName: user.userProfileDTO.lastName,
                        email: fbUser.email!,
                        profilePictureId: user.userProfileDTO.profilePictureId || null,
                        balance: 1337
                    });
                    setUserLoading(false);
                })
                .catch(_ => {
                    showErrorNotification("Maybe you had a typo in your credentials?", "Login Unsuccessful");
                    setUserLoading(false);
                });
        }
    }

    return (
        <>
            <Container
                p={"md"}
                mt={"xl"}
                style={{
                    maxWidth: 500
                }}
            >
                <Center mb={"md"}>
                    <Logo style={{width: rem(120)}}/>
                </Center>
                <Paper radius="md" p="xl" withBorder>
                    <Group grow mb="md" mt="md">
                        <GoogleButton login={login} loadingState={userLoading} setLoading={setUserLoading}/>
                        <AppleButton/>
                    </Group>

                    <Divider label="Or continue with email" labelPosition="center" my="lg"/>
                    <EmailForm login={login} loadingState={userLoading} setLoading={setUserLoading}/>
                </Paper>
            </Container>
        </>
    );
}

export default AuthMain