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
    const messages = useMessagesStore(state => state.messages[chatId]) || [];
    const [moreMessagesToLoad, setMoreMessagesToLoad] = useState(!!messages);
    const [showChevron, setShowChevron] = useState(false);

    function scrollToBottom() {
        if (scrollableDiv.current) {
            scrollableDiv.current.scroll({top: scrollableDiv.current.scrollHeight, behavior: 'smooth'})
        }
    }

    useEffect(() => {
        console.log("test123 todo remove")
        if (messages.length == 0) {
            fetchMoreMessagesByChat(chatId, "PAST", false).then(more => setMoreMessagesToLoad(more));
        } else {
            fetchMoreMessagesByChat(chatId, "FUTURE", true);
        }
    }, []);

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
                    pullDownToRefresh
                    refreshFunction={async () => {
                        await fetchMoreMessagesByChat(chatId, "FUTURE", true)
                    }
                    }
                    pullDownToRefreshThreshold={50}
                    releaseToRefreshContent={
                        <Center>
                            <Text>Release to refresh</Text>
                        </Center>
                    }
                >
                    {messages.map((msg, i) =>
                        <MessageBubble key={i} message={msg}/>
                    )}
                </InfiniteScroll>
            </div>
        </>
    );
}

export default MessageList;