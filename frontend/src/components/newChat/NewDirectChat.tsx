import React from "react";
import {useForm} from "@mantine/form";

export function NewDirectChat() {
    const form = useForm({
        initialValues: {},

        validate: {},
    });

    return (
        <form onSubmit={form.onSubmit(() => {
        })}>

        </form>
    );
}