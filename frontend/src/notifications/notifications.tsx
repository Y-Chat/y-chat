import {Notifications} from "@mantine/notifications";
import {IconExclamationCircle} from "@tabler/icons-react";
import {rem} from "@mantine/core";
import React from "react";

function codeToError(errorCode: string): string{
    const errorMap = new Map<string, string>([
        ["auth/email-already-in-use", "E-mail already registered."],
        ["auth/invalid-credential", "Login was not not successful."],
    ]);

    return errorMap.get(errorCode) || errorCode;

}

export function showErrorNotification(errorCode: string) {
    Notifications.show({
        title: "Error",
        message: codeToError(errorCode),
        icon: <IconExclamationCircle style={{width: rem(18), height: rem(18)}}/>,
    });
}

export function showNotification(message: string) {
    Notifications.show({
        title: "Error",
        message: message,
    });
}