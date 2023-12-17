import React from "react";
import {Group, Paper, Text} from "@mantine/core";
import {IconHourglassEmpty, IconPackage, IconPackageImport} from "@tabler/icons-react";

interface MessageBubbleProps {
    message: string,
    fromMe: boolean
}

function MessageBubble({message, fromMe}: MessageBubbleProps) {

    var today = new Date();


    return (
        <Paper
            radius="md"
            shadow="md"
            p={10}
            bg={fromMe ? "dark" : "mainColors"}
            style={{
                maxWidth: 300,
                height: "100%",
                placeSelf: fromMe ? "start" : "end",
            }}
        >
            <Text>{message}</Text>
            <Group justify="flex-end" gap={5} c="dimmed">
                <Text size="xs">{today.getHours() + ":" + today.getMinutes()}</Text>
                {/*<IconHourglassEmpty size={10}/>*/}
                {/*<IconPackage size={10}/>*/}
                <IconPackageImport size={10}/>
            </Group>


        </Paper>
    );
}

export default MessageBubble;