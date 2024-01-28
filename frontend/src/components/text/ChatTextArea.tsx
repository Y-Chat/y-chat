import React, {useState} from "react";
import {ActionIcon, Avatar, Divider, Group, Image, Paper, rem, Textarea} from "@mantine/core";
import {IconPhoto, IconSend, IconUpload, IconX} from "@tabler/icons-react";
import {isMobile} from 'react-device-detect';
import {api} from "../../network/api";
import {Dropzone, IMAGE_MIME_TYPE} from "@mantine/dropzone";
import {Message} from "../../api-wrapper";
import {getImageUrl, uploadImage} from "../../network/media";

interface UploadedImage {
    url: string,
    imageId: string
}

function ChatTextArea() {
    const [message, setMessage] = useState("");
    const [image, setImage] = useState<UploadedImage | null>(null);
    const [uploadingImage, setUploadingImage] = useState(false);

    return (
        <footer style={{
            position: "relative"
        }}>
            <Divider/>
            <Paper
                radius={"0"}
                pt={10}
                // more padding for rounded corners on most modern phones
                pb={isMobile ? 45 : 10}
                pl={10}
                pr={10}
                bottom={0}
                w={"100%"}
                pos={"relative"}
            >
                <Group gap={10}>
                    <Textarea
                        value={message}
                        onChange={(event) => {
                            setMessage(event.target.value);
                        }}
                        variant="filled"
                        autosize
                        size="md"
                        style={{
                            flex: "1 1 auto"
                        }}
                        maxRows={5}
                        leftSection={image ? <Image w="80%" src={image.url}></Image> : undefined}
                        rightSection={
                            <ActionIcon
                                loading={uploadingImage}
                                variant="transparent"
                                aria-label="Media"
                            >
                                <Dropzone
                                    maxFiles={1}
                                    multiple={false}
                                    pos={"relative"}
                                    loading={uploadingImage}
                                    onDrop={(files) => {
                                        setUploadingImage(true);
                                        try {
                                            files.forEach(async (file) => {
                                                const objectId = await uploadImage(file, `chats/testChat/${file.name}`); //TODO insert chatID here!
                                                const url = await getImageUrl(objectId) // TODO function can fail. Handle separately?
                                                setImage({url, imageId: objectId})
                                                setUploadingImage(false);
                                            })
                                        } catch (e) {
                                            setUploadingImage(false);
                                            // TODO handle error
                                        }
                                    }}
                                    onReject={(files) => console.log('rejected files', files)}
                                    accept={IMAGE_MIME_TYPE}
                                >
                                    <Group justify="center" gap="xl" style={{pointerEvents: 'none'}}>
                                        <Dropzone.Accept>
                                            <Avatar size={120}>
                                                <IconUpload
                                                    style={{
                                                        width: rem(52),
                                                        height: rem(52),
                                                        color: 'var(--mantine-color-blue-6)'
                                                    }}
                                                    stroke={1.5}
                                                />
                                            </Avatar>
                                        </Dropzone.Accept>
                                        <Dropzone.Reject>
                                            <Avatar size={120}>
                                                <IconX
                                                    style={{
                                                        width: rem(52),
                                                        height: rem(52),
                                                        color: 'var(--mantine-color-red-6)'
                                                    }}
                                                    stroke={1.5}
                                                />
                                            </Avatar>
                                        </Dropzone.Reject>
                                        <Dropzone.Idle>
                                            <IconPhoto/>
                                        </Dropzone.Idle>
                                    </Group>
                                </Dropzone>
                            </ActionIcon>
                        }/>

                    <ActionIcon
                        size={42}
                        variant="filled"
                        aria-label="Send"
                        disabled={!message.length && !image}
                        onClick={async () => {
                            const msg: Message = {
                                id: "c7d5906b-df61-45bd-b44e-b3b8d4c8946a", // Is ignored by server, will be fixed so we don't need to pass a random uuid here
                                senderId: "frontendTest",
                                chatId: "8e400639-1a6b-44f6-adf6-d4fd7d463e93",
                                sentTimestamp: new Date(),
                                mediaId: image ? image.imageId : undefined,
                                message: message
                            }
                            api.sendMessage({message: msg})
                                .catch(() => {
                                    // TODO handle error
                                });
                            setMessage("");
                            setImage(null);
                        }}
                    >
                        <IconSend/>
                    </ActionIcon>
                </Group>
            </Paper>
        </footer>
    );
}

export default ChatTextArea;
