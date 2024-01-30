import React, {useEffect} from "react";
import {ActionIcon, Avatar, Divider, Group, Text} from "@mantine/core";
import MenuDrawer from "../menu/MenuDrawer";
import MessageList from "./MessageList";
import ChatTextArea from "../text/ChatTextArea";
import {IconVideo} from "@tabler/icons-react";
import {accessToken, api} from "../../network/api";
import {useNavigate} from "react-router-dom";

function ChatMain() {
    // size in percent of screen -> content is 100% - sizeHeader - sizeFooter
    const sizeHeader = 10;
    const sizeFooter = 10;

    const navigate = useNavigate();

    useEffect(() => {
        api.getMessages({chatId: 'b883492e-cb45-484e-895a-0703700deac7', fromDate: new Date(2000, 0, 1)})
            .then(r => {
                console.log(JSON.stringify(r))
            })
            .catch(e => console.error(e))
        if(!!accessToken) {
            api.updateToken({notificationToken: accessToken}).catch((x) => console.error(x))
        }
    }, [])

    return (
        <>
            <header>
                <div style={{
                    position: "fixed",
                    top: 0,
                    height: `${sizeHeader}vh`,
                    width: "100%",
                    zIndex: 1,
                }}>
                    <Group w={"100%"} h={"100%"} align="center" justify="space-between" pl={10} pr={10}>
                        <MenuDrawer/>
                        <Group h={"100%"}>
                            <Group h={"100%"} gap="xs">
                                <Avatar size={40} src={null} radius={40}/>
                                <div style={{marginLeft: 5}}>
                                    <Text fz="sm" fw={500}>
                                        {`Ben Strobel`}
                                    </Text>
                                    <Text c="dimmed" fz="xs">
                                        {`@strobel123`}
                                    </Text>
                                </div>
                            </Group>
                        </Group>
                        <ActionIcon variant="transparent" c="lightgray" onClick={() => {
                            navigate("/call")
                        }}>
                            <IconVideo/>
                        </ActionIcon>
                    </Group>
                    <Divider/>
                </div>
            </header>

            <div style={{
                position: "absolute",
                top: `${sizeHeader}vh`,
                height: `${100 - sizeHeader - sizeFooter}vh`,
                width: "100vw",
            }}>
                <MessageList/>
            </div>

            <div style={{
                position: "fixed",
                bottom: 0,
                height: `${sizeFooter}vh`,
                width: "100vw",
                zIndex: 1
            }}>
                <ChatTextArea/>
            </div>
        </>
    );
}

export default ChatMain;
