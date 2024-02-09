import React, {useEffect, useMemo} from "react";
import {Button, Group} from "@mantine/core";
import {Notifications} from "@mantine/notifications";
import {notificationNavigate} from "../../notifications/notifications";

interface GeneralNotificationProps {
    notificationId: string,
    message: string,
    action?: {link?: string, text?: string, callback?: () => void},
    audioPath?: string
}

export default function GeneralNotification({message, action, audioPath, notificationId}: GeneralNotificationProps) {

    const audio = useMemo(() => new Audio(audioPath), []);

    useEffect(() => {
        if(audioPath) {
            audio.play().catch(() => {
                console.error("can't start notification audio autoplay, because it's being blocked by the browser")
            })
        }
        return () => {
            audio.pause()
        }
    }, [audio]);

    return (<Group justify={"space-between"}>
        {message}
        {action && <Button variant={"light"} onClick={() => {
            if(action?.link && notificationNavigate) {
                notificationNavigate(action?.link);
            }
            if(action?.callback) {
                action.callback()
            }

            Notifications.hide(notificationId)
        }}>
            {action.text}
        </Button>}
    </Group>)
}
