import React, {useEffect} from "react";
import {Image, Modal, Text} from "@mantine/core";
import {useDisclosure} from "@mantine/hooks";
import {Message} from "../../model/Message";
import {useImagesStore} from "../../state/imagesStore";

interface MediaMessageProps {
    message: Message
}

export function MediaMessage({message}: MediaMessageProps) {
    const [opened, {toggle}] = useDisclosure(false);
    const imageUrl = useImagesStore(state => state.cachedImages[message.mediaId!])
    const fetchImageUrl = useImagesStore(state => state.fetchImageUrl)

    useEffect(() => {
        fetchImageUrl(message.mediaId!);
    }, []);

    const imageElement = <Image src={imageUrl?.url}/>
    return (
        <>
            <Modal
                zIndex={9000}
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