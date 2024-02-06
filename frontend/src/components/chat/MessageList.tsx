import React, {useEffect, useRef, useState} from "react";
import {ActionIcon, Center, Loader, Text, useMantineTheme} from "@mantine/core";
import MessageBubble from "./MessageBubble";
import {IconCircleChevronDown} from "@tabler/icons-react";
import InfiniteScroll from "react-infinite-scroll-component";
import {useMessagesStore} from "../../state/messagesStore";

interface MessageListProps {
    chatId: string
}

function MessageList({chatId}: MessageListProps) {
    const gapBetweenMessages = 16;
    const theme = useMantineTheme();
    const scrollableDiv = useRef<HTMLDivElement>(null);
    const fetchMoreMessagesByChat = useMessagesStore(state => state.fetchMoreMessagesByChat);
    const messages = useMessagesStore(state => state.messages[chatId]);
    const [moreMessagesToLoad, setMoreMessagesToLoad] = useState(true);
    const [showChevron, setShowChevron] = useState(false);

    function scrollToBottom() {
        if (scrollableDiv.current) {
            scrollableDiv.current.scroll({top: scrollableDiv.current.scrollHeight, behavior: 'smooth'})
        }
    }

    useEffect(() => {
        if (messages && messages.length == 0) {
            setMoreMessagesToLoad(false);
        }
    }, [messages]);

    useEffect(() => {
        const div = scrollableDiv.current;
        if (div) {
            div.addEventListener('scroll', () => {
                const scrollPosition = Math.abs(div.scrollTop);
                setShowChevron(!(scrollPosition <= 50));
            });
        }
    }, [scrollableDiv]);

    return (
        <>
            <ActionIcon
                color={"dark"}
                c={theme.colors[theme.primaryColor][6]}
                onClick={scrollToBottom}
                size={50}
                variant="filled"
                style={{
                    position: "fixed",
                    bottom: 90,
                    right: 0,
                    marginBottom: 20,
                    marginRight: 20,
                    zIndex: 1,
                    opacity: showChevron ? 1 : 0
                }}
            >
                <IconCircleChevronDown
                    size={40}
                />
            </ActionIcon>
            <div
                ref={scrollableDiv}
                id="scrollableDiv"
                style={{
                    // header and message bar are 90 each = 180. not very beautiful but necessary
                    height: " calc(100vh - 180px)",
                    overflow: 'auto',
                    display: 'flex',
                    flexDirection: 'column-reverse',
                }}
            >
                {messages !== undefined &&
                    <InfiniteScroll
                        scrollableTarget="scrollableDiv"
                        dataLength={messages.length} //This is important field to render the next data
                        next={async () => {
                            const hasMore = await fetchMoreMessagesByChat(chatId, "PAST", false);
                            setMoreMessagesToLoad(hasMore);
                        }}
                        hasMore={moreMessagesToLoad}
                        loader={
                            <Center mt={"md"}>
                                <Loader/>
                            </Center>
                        }
                        endMessage={
                            <Center>
                                <Text>Conversation started</Text>
                            </Center>
                        }
                        inverse={true}
                        style={{
                            display: 'flex',
                            flexDirection: 'column-reverse',
                            gap: gapBetweenMessages,
                            paddingBottom: gapBetweenMessages,
                            paddingTop: gapBetweenMessages,
                            paddingLeft: 16,
                            paddingRight: 16
                        }}
                    >
                        {messages.map((msg, i) =>
                            <MessageBubble key={i} message={msg}/>
                        )}
                    </InfiniteScroll>}
            </div>
        </>
    );
}

export default MessageList;