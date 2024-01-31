import React from "react";
import {Group, Paper, Text} from "@mantine/core";
import {IconPackageImport} from "@tabler/icons-react";
import {Message} from "../../model/Message";
import {MediaMessage} from "./MediaMessage";

interface MessageBubbleProps {
    message: Message,
}

function MessageBubble({message}: MessageBubbleProps) {

    function messageContent() {
        if (message.type === "text") {
            return (
                <Text>{message.message}</Text>
            );
        } else if (message.type === "media") {
            return (
                <MediaMessage message={message}/>
            );
        } else if (message.type === "payment") {
            return (
                <p>Not supported in your version</p>
            );
        }
    }

    var today = new Date();


    return (
        <Group justify={message.fromMe ? "flex-start" : "flex-end"}>
            <Paper
                radius="md"
                shadow="md"
                p={10}
                bg={message.fromMe ? "dark.6" : "mainColors.6"}
                style={{
                    maxWidth: 300,
                    height: "100%",
                    placeSelf: message.fromMe ? "start" : "end",
                }}
            >
                {messageContent()}
                <Group justify="flex-end" gap={5} c="dimmed">
                    <Text size="xs">{today.getHours() + ":" + today.getMinutes()}</Text>
                    {/*<IconHourglassEmpty size={10}/>*/}
                    {/*<IconPackage size={10}/>*/}
                    <IconPackageImport size={10}/>
                </Group>
            </Paper>
        </Group>
    );
}

export default MessageBubble;