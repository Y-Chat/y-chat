import React from "react";
import {Anchor, Button, Group, PasswordInput, Stack, TextInput} from "@mantine/core";
import {upperFirst, useToggle} from "@mantine/hooks";
import {useForm} from "@mantine/form";
import {createUserWithEmailAndPassword, deleteUser, signInWithEmailAndPassword} from "firebase/auth";
import auth from "../../firebase/auth";
import {api} from "../../network/api";
import getUuidByString from "uuid-by-string";
import {showErrorNotification, showSuccessNotification} from "../../notifications/notifications";

interface EmailFormProps {
    loadingState: boolean,
    setLoading: (loadingState: boolean) => void,
    login: () => void
}

export function EmailForm({loadingState, setLoading, login}: EmailFormProps) {
    const [type, toggle] = useToggle(['login', 'register']);

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
        setLoading(true);
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
                    setLoading(false);
                }).catch(err => {
                    if (auth.currentUser) {
                        deleteUser(auth.currentUser).then(r => {
                            showErrorNotification("Something went wrong during registration. Please try again.", "Registration Unsuccessful");
                            setLoading(false);
                        }).catch(err => {
                            // if this fails again, the user has to manually reset the user. Might think of a smarter solution in the future
                            setLoading(false);
                        });
                    }
                })
            }).catch(err => {
                showErrorNotification("Something went wrong during registration. Please try again.", "Registration Unsuccessful");
                setLoading(false);
            }
        )
    }

    return (
        <form
            onSubmit={form.onSubmit(async () => {
                if (type === "register") {
                    register();
                } else if (type === "login") {
                    setLoading(true);
                    signInWithEmailAndPassword(auth, form.values.email, form.values.password).then(userCredentials => {
                        login();
                    }).catch(err => {
                        showErrorNotification("Maybe you had a typo in your credentials?", "Login Unsuccessful");
                        setLoading(false);
                    })
                }
            })}>
            <Stack>
                {type === 'register' && (
                    <>
                        <TextInput
                            disabled={loadingState}
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
                            disabled={loadingState}
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
                    disabled={loadingState}
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
                    disabled={loadingState}
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
                        disabled={loadingState}
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
                <Button type="submit" radius="xl" loading={loadingState}>
                    {upperFirst(type)}
                </Button>
            </Group>
        </form>
    );
}