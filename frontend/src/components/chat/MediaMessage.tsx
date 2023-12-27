import React from "react";
import {Image, Modal, Text} from "@mantine/core";
import {useDisclosure} from "@mantine/hooks";
import {Message} from "../../model/Message";

interface MediaMessageProps {
    message: Message
}

// TODO: support videos in the future?
export function MediaMessage({message}: MediaMessageProps) {
    const [opened, {toggle}] = useDisclosure(false);

    return (
        <>
            <Modal centered lockScroll opened={opened} onClose={toggle}>
                <Image src={message.mediaUrl}/>
            </Modal>
            <Image src={message.mediaUrl} onClick={toggle}/>
            <Text>{message.message}</Text>
        </>
    );
}