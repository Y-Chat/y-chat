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
    const imageElement = <Image src={message.mediaUrl}/>

    return (
        <>
            <Modal
                centered
                lockScroll
                opened={opened}
                onClose={toggle}
                overlayProps={{backgroundOpacity: 0.5, blur: 4}}>
                {imageElement}
            </Modal>
            <div onClick={toggle}>
                {imageElement}
            </div>
            <Text>{message.message}</Text>
        </>
    );
}