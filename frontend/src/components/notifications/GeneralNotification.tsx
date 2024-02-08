import React, {useEffect, useMemo} from "react";
import {Button, Group} from "@mantine/core";
import {notificationNavigate} from "../../firebase/messaging";
import {Notifications} from "@mantine/notifications";

interface GeneralNotificationProps {
    notificationId: string,
    message: string,
    action?: {link: string, text: string},
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
            notificationNavigate && notificationNavigate(action?.link);
            Notifications.hide(notificationId)
        }}>
            {action.text}
        </Button>}
    </Group>)
}
