import React, {useState} from "react";
import {ActionIcon, Avatar, Divider, Group, Image, Paper, rem, Textarea} from "@mantine/core";
import {IconPhoto, IconSend, IconUpload, IconX} from "@tabler/icons-react";
import {isMobile} from 'react-device-detect';
import {api} from "../../network/api";
import {Dropzone, FileWithPath, IMAGE_MIME_TYPE} from "@mantine/dropzone";
import {Message} from "../../api-wrapper";
import {uploadImage} from "../../network/media";
import {showErrorNotification} from "../../notifications/notifications";

function ChatTextArea() {
    const [message, setMessage] = useState("");
    const [image, setImage] = useState<{ file: FileWithPath | null, url: string }>({file: null, url: ""});
    const [messageSending, setMessageSending] = useState(false);

    async function sendMessage() {
        setMessageSending(true);
        let msg: Message = {
            id: "c7d5906b-df61-45bd-b44e-b3b8d4c8946a", // Is ignored by server, will be fixed so we don't need to pass a random uuid here
            senderId: "c7d5906b-df61-45bd-b44e-b3b8d4c8946a",
            chatId: "8e400639-1a6b-44f6-adf6-d4fd7d463e93", // TODO
            sentTimestamp: new Date(),
            message: message
        }

        if (image.file) {
            try {
                //TODO insert chatID here!
                msg.mediaId = await uploadImage(image.file, `chats/testChat/${image.file.name}`);
                const m = await api.sendMessage({message: msg});
                // TODO add message to local storage
                setMessage("");
                setImage({file: null, url: ""});
            } catch (err) {
                setMessageSending(false);
                return showErrorNotification("An error occurred sending your message.", "Sending Message Failed");
            }
        }
        setMessageSending(false);
    }

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
                        disabled={messageSending}
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
                                variant="transparent"
                                aria-label="Media"
                            >
                                <Dropzone
                                    maxFiles={1}
                                    multiple={false}
                                    pos={"relative"}
                                    onDrop={(files) => {
                                        files.forEach((file) => {
                                            setImage({file: file, url: URL.createObjectURL(file)})
                                        })
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
                        loading={messageSending}
                        size={42}
                        variant="filled"
                        aria-label="Send"
                        disabled={!message.length && !image}
                        onClick={sendMessage}
                    >
                        <IconSend/>
                    </ActionIcon>
                </Group>
            </Paper>
        </footer>
    );
}

export default ChatTextArea;
