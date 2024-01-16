import React, {useState} from "react";
import {ActionIcon, Divider, Group, Paper, Textarea} from "@mantine/core";
import {IconPhoto, IconSend} from "@tabler/icons-react";
import {isMobile} from 'react-device-detect';
import {api} from "../../network/api";

function ChatTextArea() {
    const [message, setMessage] = useState("");

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
                        /*
                                                Move Send button to bottom
                                                styles={{
                                                    section: {
                                                        alignItems: "flex-end"
                                                    }
                                                }}
                                                */
                        rightSection={
                            <ActionIcon
                                variant="transparent"
                                aria-label="Media"
                            >
                                <IconPhoto/>
                            </ActionIcon>
                        }
                    />
                    <ActionIcon
                        size={42}
                        variant="filled"
                        aria-label="Send"
                        disabled={!message.length}
                        onClick={() => {
                            api.sendMessage({
                                message: {
                                    id: "123",
                                    senderId: "frontendTest",
                                    chatId: "123",
                                    sentTimestamp: new Date(),
                                    message: message
                                }
                            }).catch(() => {
                                // TODO handle error
                            });
                            setMessage("");
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