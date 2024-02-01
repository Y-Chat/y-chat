import {IconPhone, IconPhoneOff} from "@tabler/icons-react";
import {api} from "../../network/api";
import {Notifications} from "@mantine/notifications";
import React from "react";
import {useNavigate} from "react-router-dom";
import {ActionIcon, Group, Text} from "@mantine/core";
import {useCallingStore} from "../../state/callingStore";

interface IncomingCallNotificationProps {
    notificationId: string,
    callId: string,
    callerId: string
}

export default function IncomingCallNotification({notificationId, callId, callerId}: IncomingCallNotificationProps) {
    const acceptCall = useCallingStore((state) => state.acceptCall);
    const denyCall = useCallingStore((state) => state.denyCall);

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
                    //navigate(`/call?accept=${callId}`)
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
                    api.answerCall({answerCallRequest: {callId: callId, accept: false}})
                    Notifications.hide(notificationId)
                }}
            >
                <IconPhoneOff/>
            </ActionIcon>
            <Text>Deny Call</Text>
        </Group>
    </Group>)
}
