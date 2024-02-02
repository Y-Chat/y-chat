import React, {useState} from "react";
import {ActionIcon, Avatar, Divider, Group, Image, Paper, rem, Textarea, useMantineTheme} from "@mantine/core";
import {IconPhoto, IconSend, IconUpload, IconX} from "@tabler/icons-react";
import {isMobile} from 'react-device-detect';
import {api} from "../../network/api";
import {Dropzone, FileWithPath, IMAGE_MIME_TYPE} from "@mantine/dropzone";
import {Message} from "../../api-wrapper";
import {uploadImage} from "../../network/media";
import {showErrorNotification} from "../../notifications/notifications";
import {useUserStore} from "../../state/userStore";
import {useChatsStore} from "../../state/chatsStore";
import {useMessagesStore} from "../../state/messagesStore";

interface ChatTextAreaProps {
    chatId: string
}

function ChatTextArea({chatId}: ChatTextAreaProps) {
    const [message, setMessage] = useState("");
    const [image, setImage] = useState<{ file: FileWithPath | null, url: string }>({file: null, url: ""});
    const [messageSending, setMessageSending] = useState(false);
    const theme = useMantineTheme();
    const user = useUserStore(state => state.user)!;
    const fetchMoreMessagesByChat = useMessagesStore(state => state.fetchMoreMessagesByChat);

    async function sendMessage() {
        setMessageSending(true);
        let msg: Message = {
            id: "", // Is ignored by server
            senderId: user.id,
            chatId: chatId,
            sentTimestamp: new Date(),
            message: message
        }


        try {
            if (image.file) {
                //TODO insert chatID here!
                msg.mediaPath = await uploadImage(image.file, `chats/${chatId}/${image.file.name}`);
            }
            const m = await api.sendMessage({message: msg});
            await fetchMoreMessagesByChat(chatId, "FUTURE", true);
            // TODO add message to local storage
            setMessage("");
            resetImage();

        } catch (err) {
            setMessageSending(false);
            return showErrorNotification("An error occurred sending your message.", "Sending Message Failed");
        }
        setMessageSending(false);
    }

    function resetImage() {
        setImage({file: null, url: ""});
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
                        leftSection={image.url ?
                            <>
                                <Image
                                    h={"xl"}
                                    w={"xl"}
                                    onClick={resetImage}
                                    src={image.url}/>
                                <ActionIcon onClick={resetImage} color={"red"} style={{
                                    position: "absolute",
                                    top: -2,
                                    left: -2
                                }} size={13}>
                                    <IconX/>
                                </ActionIcon>
                            </> : undefined

                        }
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
                        color={theme.colors[theme.primaryColor][6]}
                        loading={messageSending}
                        size={42}
                        variant="filled"
                        aria-label="Send"
                        disabled={!message && !image.file}
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