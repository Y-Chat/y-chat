import React, {useState} from "react";
import {
    Anchor,
    Button,
    Center,
    Container,
    Divider,
    Group,
    Paper,
    PasswordInput,
    rem,
    Stack,
    TextInput,
} from "@mantine/core";
import {useForm} from "@mantine/form";
import {upperFirst, useToggle} from "@mantine/hooks";
import Logo from "../common/Logo";
import {useUserStore} from "../../state/userStore";
import {createUserWithEmailAndPassword, deleteUser, signInWithEmailAndPassword} from "firebase/auth"
import {GoogleButton} from "./GoogleButton";
import {AppleButton} from "./AppleButton";
import auth from "../../firebase/auth";
import {api} from "../../network/api";
import {showErrorNotification, showSuccessNotification} from "../../notifications/notifications";
import getUuidByString from "uuid-by-string";

function AuthMain() {
    const setUser = useUserStore((state) => state.setUser)
    const [type, toggle] = useToggle(['login', 'register']);
    const [userLoading, setUserLoading] = useState<boolean>(false)

    const form = useForm({
        initialValues: {
            firstName: '',
            lastName: '',
            email: '',
            password: '',
            passwordRepeat: '',
            terms: true,
        },

        validate: {
            email: (val) =>
                (/^\S+@\S+$/.test(val) ? null : 'Invalid email'),
            password: (val) => (
                val.length <= 6 ? 'Password should include at least 6 characters' : null),
            passwordRepeat: (value, values) =>
                (value !== values.password && type === "register" ? 'Passwords did not match' : null),
        },
    });


    function register() {
        setUserLoading(true);
        createUserWithEmailAndPassword(auth, form.values.email, form.values.password)
            .then(userCredentials => {
                api.createUser({
                    userId: getUuidByString(userCredentials.user.uid, 3),
                    userProfileDTO: {
                        firstName: form.values.firstName,
                        lastName: form.values.lastName,
                    }
                }).then(user => {
                    toggle();
                    showSuccessNotification("Now try signing in using your credentials.", "Registration successful");
                    form.reset();
                    setUserLoading(false);
                }).catch(err => {
                    if (auth.currentUser) {
                        deleteUser(auth.currentUser).then(r => {
                            showErrorNotification("Something went wrong during registration. Please try again.", "Registration Unsuccessful");
                            setUserLoading(false);
                        }).catch(err => {
                            // if this fails again, the user has to manually reset the user. Might think of a smarter solution in the future
                            setUserLoading(false);
                        });
                    }
                })
            }).catch(err => {
            showErrorNotification("Something went wrong during registration. Please try again.", "Registration Unsuccessful");
            setUserLoading(false);
        })
    }

    function login() {
        setUserLoading(true);
        signInWithEmailAndPassword(auth, form.values.email, form.values.password).then(userCredentials => {
            return api.getUser({userId: getUuidByString(userCredentials.user.uid, 3)})
                .then(user => {
                    setUser({
                        id: user.id,
                        firstName: user.userProfileDTO.firstName,
                        lastName: user.userProfileDTO.lastName,
                        email: userCredentials.user.email!,
                        profilePictureId: null,
                        balance: 1337
                    });
                    setUserLoading(false);
                });
        }).catch(err => {
            showErrorNotification("Maybe you had a typo in your credentials?", "Login Unsuccessful");
            setUserLoading(false);
        })
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
                    <Logo style={{ width: rem(120) }}/>
                </Center>
                <Paper radius="md" p="xl" withBorder>
                    <Group grow mb="md" mt="md">
                        <GoogleButton disabled={true} radius="xl">Google</GoogleButton>
                        <AppleButton disabled={true} radius="xl">Apple</AppleButton>
                    </Group>

                    <Divider label="Or continue with email" labelPosition="center" my="lg"/>

                    <form
                        onSubmit={form.onSubmit(async () => {
                            if (type === "register") {
                                register()
                            } else if (type === "login") {
                                login()
                            }
                        })}>
                        <Stack>
                            {type === 'register' && (
                                <>
                                    <TextInput
                                        disabled={userLoading}
                                        withAsterisk
                                        required
                                        size="md"
                                        label="First Name"
                                        placeholder="Max"
                                        value={form.values.firstName}
                                        onChange={(event) => form.setFieldValue('firstName', event.currentTarget.value)}
                                        radius="md"
                                    />
                                    <TextInput
                                        disabled={userLoading}
                                        withAsterisk
                                        required
                                        size="md"
                                        label="Last Name"
                                        placeholder="Mustermann"
                                        value={form.values.lastName}
                                        onChange={(event) => form.setFieldValue('lastName', event.currentTarget.value)}
                                        radius="md"
                                    />
                                </>
                            )}

                            <TextInput
                                disabled={userLoading}
                                size="md"
                                withAsterisk
                                required
                                label="Email"
                                placeholder="Email@example.com"
                                value={form.values.email}
                                onChange={(event) => form.setFieldValue('email', event.currentTarget.value)}
                                error={form.errors.email && 'Invalid email'}
                                radius="md"
                            />

                            <PasswordInput
                                disabled={userLoading}
                                size="md"
                                withAsterisk
                                required
                                label="Password"
                                placeholder="Your password"
                                value={form.values.password}
                                onChange={(event) => form.setFieldValue('password', event.currentTarget.value)}
                                error={form.errors.password}
                                radius="md"
                            />

                            {type === 'register' && (
                                <PasswordInput
                                    disabled={userLoading}
                                    size="md"
                                    withAsterisk
                                    required
                                    label="Repeat Password"
                                    placeholder="Your password again"
                                    value={form.values.passwordRepeat}
                                    onChange={(event) => form.setFieldValue('passwordRepeat', event.currentTarget.value)}
                                    error={form.errors.passwordRepeat}
                                    radius="md"
                                />
                            )}
                        </Stack>

                        <Group justify="space-between" mt="xl">
                            <Anchor component="button" type="button" c="dimmed" onClick={() => toggle()} size="xs">
                                {type === 'register'
                                    ? 'Already have an account? Login'
                                    : "Don't have an account? Register"}
                            </Anchor>
                            <Button type="submit" radius="xl" loading={userLoading}>
                                {upperFirst(type)}
                            </Button>
                        </Group>
                    </form>
                </Paper>
            </Container>
        </>
    );
}

export default AuthMain