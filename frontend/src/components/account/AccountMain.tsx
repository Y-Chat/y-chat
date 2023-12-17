import React from "react";
import {Avatar, Center, Text, Divider, Group, Stack, SimpleGrid, Card, Input} from "@mantine/core";
import {useForm} from "@mantine/form";
import {TextInput} from "@mantine/core";
import {useAppStore} from "../../state/store";

export function AccountMain() {
    const user = useAppStore((state) => state.user);
    const form = useForm({
        initialValues: {
            email: '',
            firstName: '',
            lastName: '',
        },

        validate: {
            //email: (val) => (/^\S+@\S+$/.test(val) ? null : 'Invalid email'),
            //password: (val) => (val.length <= 6 ? 'Password should include at least 6 characters' : null),
        },
    });
    return (
        <Center>
            <form onSubmit={form.onSubmit(() => {
            })}>
                <Stack justify="flex-start" align="stretch">
                    <Center mb={10}>
                        <Avatar size={120}/>
                    </Center>
                    <Group grow>
                        <TextInput
                            size="md"
                            label="First Name"
                            placeholder="Max"
                            value={form.values.firstName}
                            onChange={(event) => form.setFieldValue('firstName', event.currentTarget.value)}
                            radius="md"
                        />
                        <TextInput
                            size="md"
                            label="Last Name"
                            placeholder="Mustermann"
                            value={form.values.lastName}
                            onChange={(event) => form.setFieldValue('lastName', event.currentTarget.value)}
                            radius="md"
                        />
                    </Group>
                    <TextInput
                        size="md"
                        label="Email"
                        placeholder="Max@mustermann.de"
                        value={form.values.lastName}
                        onChange={(event) => form.setFieldValue('lastName', event.currentTarget.value)}
                        radius="md"
                    />
                    <Divider/>
                    <Input.Wrapper
                        size="md"
                        label="Your Balance"
                    >
                        <Center><Text size="md" c="green">{user?.balance}€</Text></Center>
                    </Input.Wrapper>
                    <SimpleGrid cols={3} verticalSpacing="sm">
                        {["+5€", "+10€", "+50€"].map(e =>
                            <Card withBorder>
                                <Center>{e}</Center>
                            </Card>
                        )}
                    </SimpleGrid>

                </Stack>
            </form>
        </Center>
    );
}