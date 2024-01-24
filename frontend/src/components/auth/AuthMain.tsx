import React, {useState} from "react";
import {
    Anchor,
    Button,
    Divider,
    Group,
    Paper,
    PasswordInput,
    Stack,
    TextInput,
    Text,
    Container,
    Center,
} from "@mantine/core";
import {useForm} from "@mantine/form";
import {upperFirst, useToggle} from "@mantine/hooks";
import Logo from "../shell/Logo";
import {useAppStore} from "../../state/store";
import {createUserWithEmailAndPassword, signInWithEmailAndPassword} from "firebase/auth"
import {GoogleButton} from "./GoogleButton";
import {AppleButton} from "./AppleButton";
import {showErrorNotification} from "../../notifications/notifications";
import auth from "../../firebase/auth";

function AuthMain() {
    const setUser = useAppStore((state) => state.setUser)
    const [type, toggle] = useToggle(['login', 'register']);
    const [userLoading, setUserLoading] = useState<boolean>(false)

    const form = useForm({
            initialValues: {
                firstName: '',
                lastName: '',
                email: '',
                username: '',
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
        })
    ;


    function register(email: string, password: string) {
        setUserLoading(true);
        createUserWithEmailAndPassword(auth, email, password)
            .then((userCredentials) => {
                setUser({
                    firstName: "Example",
                    lastName: "Name",
                    username: "example_username",
                    email: "example@example.com",
                    avatar: null,
                    balance: 69
                })
                setUserLoading(false);
            }).catch((error) => {
            showErrorNotification(error.code);
            setUserLoading(false);
        });
    }

    function login(email: string, password: string) {
        setUserLoading(true);
        signInWithEmailAndPassword(auth, email, password)
            .then((userCredentials) => {
                setUser({
                    firstName: "Example",
                    lastName: "Name",
                    username: "example_username",
                    email: "example@example.com",
                    avatar: null,
                    balance: 69
                })
                setUserLoading(false);
            })
            .catch((error) => {
                showErrorNotification(error.code);
                setUserLoading(false);
            });

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
                    <Logo size={100}/>
                </Center>
                <Paper radius="md" p="xl" withBorder>
                    <Group grow mb="md" mt="md">
                        <GoogleButton disabled={userLoading} radius="xl">Google</GoogleButton>
                        <AppleButton disabled={userLoading} radius="xl">Apple</AppleButton>
                    </Group>

                    <Divider label="Or continue with email" labelPosition="center" my="lg"/>

                    <form
                        onSubmit={form.onSubmit(() => {
                            const email = form.values.email
                            const password = form.values.password
                            if (type === "register") {
                                register(email, password)
                            } else if (type === "login") {
                                login(email, password)
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
                                placeholder="email@example.com"
                                value={form.values.email}
                                onChange={(event) => form.setFieldValue('email', event.currentTarget.value)}
                                error={form.errors.email && 'Invalid email'}
                                radius="md"
                            />

                            {type === 'register' && (
                                <TextInput
                                    disabled={userLoading}
                                    size="md"
                                    withAsterisk
                                    required
                                    label="Username"
                                    placeholder="example_username"
                                    value={form.values.username}
                                    leftSection={<Text>@</Text>}
                                    onChange={(event) => form.setFieldValue('username', event.currentTarget.value)}
                                    error={form.errors.username && 'Invalid username'}
                                    radius="md"
                                />
                            )}

                            <PasswordInput
                                disabled={userLoading}
                                size="md"
                                withAsterisk
                                required
                                label="Password"
                                placeholder="your password"
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
                                    placeholder="your password again"
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