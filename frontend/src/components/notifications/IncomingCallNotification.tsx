import {IconPhone, IconPhoneOff} from "@tabler/icons-react";
import {api} from "../../network/api";
import {Notifications} from "@mantine/notifications";
import React, {useEffect, useMemo} from "react";
import {useNavigate} from "react-router-dom";
import {ActionIcon, Group, Text} from "@mantine/core";
import {useCallingStore} from "../../state/callingStore";

interface IncomingCallNotificationProps {
    notificationId: string,
    callId: string,
    callerId: string,
    offerSdp: string,
    offerType: string
}

export default function IncomingCallNotification({notificationId, callId, callerId, offerSdp, offerType}: IncomingCallNotificationProps) {
    const acceptCall = useCallingStore((state) => state.acceptCall);
    const denyCall = useCallingStore((state) => state.denyCall);

    const audio = useMemo(() => new Audio("/ringing.mp3"), []);

    useEffect(() => {
        audio.loop = true;
        audio.play().catch(() => {
            console.error("can't start notification audio autoplay, because it's being blocked by the browser")
        })
        return () => {
            audio.pause()
        }
    }, [audio]);

    return (<Group style={{width: "100%"}} justify={"center"}>
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
                    acceptCall(callId, offerSdp, offerType)
                    Notifications.hide(notificationId)
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
                    denyCall(callId)
                    Notifications.hide(notificationId)
                }}
            >
                <IconPhoneOff/>
            </ActionIcon>
            <Text>Deny Call</Text>
        </Group>
    </Group>)
}
