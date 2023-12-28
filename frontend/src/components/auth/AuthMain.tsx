import React from "react";
import {
    Anchor,
    Button,
    Checkbox,
    Flex,
    Group,
    Paper,
    PasswordInput,
    Stack,
    TextInput, useMantineTheme
} from "@mantine/core";
import {useForm} from "@mantine/form";
import {upperFirst, useToggle} from "@mantine/hooks";
import Logo from "../shell/Logo";
import {useAppStore} from "../../state/store";

function AuthMain() {
    const setUser = useAppStore((state) => state.setUser)
    const [type, toggle] = useToggle(['login', 'register']);
    const form = useForm({
        initialValues: {
            email: '',
            firstName: '',
            lastName: '',
            password: '',
            terms: true,
        },

        validate: {
            //email: (val) => (/^\S+@\S+$/.test(val) ? null : 'Invalid email'),
            //password: (val) => (val.length <= 6 ? 'Password should include at least 6 characters' : null),
        },
    });
    const theme = useMantineTheme();
    return (
        <Flex
            pt={"10vh"}
            w={"100vw"}
            h={"100vh"}
            gap="md"
            justify="flex-start"
            align="center"
            direction="column"
        >
            <Logo size={100}/>
            <Paper radius="md" p="xl" withBorder>
                <form onSubmit={form.onSubmit(() => {
                    setUser({
                        username: "strobel123",
                        firstName: "Ben",
                        lastName: "Strobel",
                        email: "ben@strobel.de",
                        avatar: null,
                        balance: 101
                    });
                })}>
                    <Stack>
                        {type === 'register' && (
                            <>
                                <TextInput
                                    withAsterisk
                                    size="md"
                                    label="First Name"
                                    placeholder="Max"
                                    value={form.values.firstName}
                                    onChange={(event) => form.setFieldValue('firstName', event.currentTarget.value)}
                                    radius="md"
                                />
                                <TextInput
                                    withAsterisk
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
                            size="md"
                            required
                            label="Email"
                            placeholder="Email@example.com"
                            value={form.values.email}
                            onChange={(event) => form.setFieldValue('email', event.currentTarget.value)}
                            error={form.errors.email && 'Invalid email'}
                            radius="md"
                        />

                        <PasswordInput
                            size="md"
                            required
                            label="Password"
                            placeholder="Your password"
                            value={form.values.password}
                            onChange={(event) => form.setFieldValue('password', event.currentTarget.value)}
                            error={form.errors.password && 'Password should include at least 6 characters'}
                            radius="md"
                        />

                        {type === 'register' && (
                            <Checkbox
                                label="I accept terms and conditions"
                                checked={form.values.terms}
                                onChange={(event) => form.setFieldValue('terms', event.currentTarget.checked)}
                            />
                        )}
                    </Stack>

                    <Group justify="space-between" mt="xl">
                        <Anchor component="button" type="button" c="dimmed" onClick={() => toggle()} size="xs">
                            {type === 'register'
                                ? 'Already have an account? Login'
                                : "Don't have an account? Register"}
                        </Anchor>
                        <Button type="submit" radius="xl">
                            {upperFirst(type)}
                        </Button>
                    </Group>
                </form>
            </Paper>
        </Flex>
    )
        ;
}

export default AuthMain