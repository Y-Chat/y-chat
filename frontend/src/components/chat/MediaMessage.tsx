import React, {useEffect, useState} from "react";
import {Image, Modal, Text} from "@mantine/core";
import {useDisclosure} from "@mantine/hooks";
import {Message} from "../../model/Message";
import {getImageUrl} from "../../network/media";

interface MediaMessageProps {
    message: Message
}

export function MediaMessage({message}: MediaMessageProps) {
    const [opened, {toggle}] = useDisclosure(false);
    const [url, setUrl] = useState<string | null>(null);
    const imageElement = <Image src={url}/>

    useEffect(() => {
        getImageUrl(message.mediaId!).then(url => {
            setUrl(url);
        })
    }, []);

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