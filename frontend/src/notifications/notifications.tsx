import {Notifications} from "@mantine/notifications";
import {IconExclamationCircle, IconPhone, IconPhoneOff, IconCircleCheck} from "@tabler/icons-react";
import {ActionIcon, Group, rem, Text} from "@mantine/core";
import React from "react";
import {useNavigate} from "react-router-dom";

function codeToError(errorCode: string): string {
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

export function showSuccessNotification(message: string, title: string = "Notification") {
    Notifications.show({
        withBorder: true,
        title: title,
        message: message,
        icon: <IconCircleCheck style={{width: rem(18), height: rem(18)}}/>,
    });
}

export function showCallNotification(callId: string, callerId: string) {
    Notifications.show({
        withBorder: true,
        title: "Incoming Call",
        autoClose: false,
        withCloseButton: false,
        message: <Group style={{width: "100%"}} justify={"center"}>
            <Group>
                <Text>Accept Call</Text>
                <ActionIcon
                    size={"4vh"}
                    color={"white"}
                    style={{
                        backgroundColor: "green",
                        padding: "5px",
                        borderRadius: 100
                    }}
                    onClick={() => {
                        // navigate(`/call?accept=${callId}`)
                    }}
                >
                    <IconPhone/>
                </ActionIcon>
            </Group>
            <Group>
                <ActionIcon
                    size={"4vh"}
                    color={"white"}
                    style={{
                        backgroundColor: "red",
                        padding: "5px",
                        borderRadius: 100
                    }}
                    onClick={() => {
                        // TODO
                    }}
                >
                    <IconPhoneOff/>
                </ActionIcon>
                <Text>Deny Call</Text>
            </Group>
        </Group>,
    })
}
