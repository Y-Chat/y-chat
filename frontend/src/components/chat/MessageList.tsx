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
    const [moreMessagesToLoad, setMoreMessagesToLoad] = useState(true);

    function scrollToBottom() {
        if (scrollableDiv.current) {
            scrollableDiv.current.scroll({top: scrollableDiv.current.scrollHeight, behavior: 'smooth'})
        }
    }

    async function getNewMessages() {
    }

    useEffect(() => {
        // do one initials load into the past if we have no messages for this chat
        if (messages.length == 0) {
            fetchMoreMessagesByChat(chatId, "PAST", false).then(hasMore => setMoreMessagesToLoad(hasMore));
        }
    }, []);

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
                    zIndex: 1
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
                        <Center mt={"md"}>
                            <Text>Conversation started</Text>
                        </Center>
                    }
                    inverse={true}
                    style={{
                        display: 'flex',
                        flexDirection: 'column-reverse',
                        gap: gapBetweenMessages,
                        paddingLeft: 16,
                        paddingRight: 16
                    }}
                    // TODO do we need pull down on phone?
                    refreshFunction={() => console.log("refresh")}
                    pullDownToRefresh
                    pullDownToRefreshThreshold={50}
                    releaseToRefreshContent={
                        <h3 style={{textAlign: 'center'}}>&#8593; Release to refresh</h3>
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