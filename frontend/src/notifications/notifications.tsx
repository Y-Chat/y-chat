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

export function showErrorNotification(errorCode: string, title: string = "Error") {
    Notifications.show({
        withBorder: true,
        title: title,
        message: codeToError(errorCode),
        icon: <IconExclamationCircle style={{width: rem(18), height: rem(18)}}/>,
    });
}

export function showNotification(message: string, title: string = "Notification") {
    Notifications.show({
        withBorder: true,
        title: title,
        message: message,
    });
}