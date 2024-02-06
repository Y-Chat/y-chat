import {Notifications} from "@mantine/notifications";
import {IconExclamationCircle, IconPhone, IconPhoneOff, IconCircleCheck} from "@tabler/icons-react";
import {ActionIcon, Group, rem, Text} from "@mantine/core";
import React from "react";
import IncomingCallNotification from "../components/notifications/IncomingCallNotification";

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

export function showNotification(message: string, title: string = "Notification", link?: string) {
    const id = title + (Math.random()*10000000)
    Notifications.show({
        id: id,
        withBorder: true,
        title: title,
        message: message,
        onClick: link ? () => {
            Notifications.hide(id)
            // This is a workaround. There is no other way to redirect outside component in react router 6.4 <
            window.location.pathname = link;
        } : undefined
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

export function showCallNotification(callId: string, callerId: string, offerSdp: string, offerType: string) {
    const notificationId = callIdToCallNotificationId(callId)
    Notifications.show({
        id: notificationId,
        withBorder: true,
        title: "Incoming Call",
        autoClose: false,
        withCloseButton: false,
        message: <IncomingCallNotification callId={callId} notificationId={notificationId} callerId={callerId} offerSdp={offerSdp} offerType={offerType}/>,
    })
}

export function callIdToCallNotificationId(callId: string) {
    return `callNotification-callId-${callId}`;
}
