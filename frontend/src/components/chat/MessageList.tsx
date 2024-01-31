import React, {useEffect, useRef, useState} from "react";
import {ActionIcon, Container, Flex, LoadingOverlay, rem, ScrollArea, Stack, useMantineTheme} from "@mantine/core";
import MessageBubble from "./MessageBubble";
import {IconCircleChevronDown} from "@tabler/icons-react";
import InfiniteScroll from "react-infinite-scroll-component";
import {useMessagesStore} from "../../state/messagesStore";

interface MessageListProps {
    chatId: string
}

function MessageList({chatId}: MessageListProps) {
    const gapBetweenMessages = 8;
    const theme = useMantineTheme();
    const scrollableDiv = useRef<HTMLDivElement>(null);
    const fetchMoreMessagesByChat = useMessagesStore(state => state.fetchMoreMessagesByChat);
    const messages = useMessagesStore(state => state.messages[chatId]);

    function sleep(ms: number) { // TODO remove
        return new Promise(resolve => setTimeout(resolve, ms));
    }

    function scrollToBottom() {
        if (scrollableDiv.current) {
            scrollableDiv.current.scroll({top: scrollableDiv.current.scrollHeight, behavior: 'smooth'})
        }
    }

    useEffect(() => {
        fetchMoreMessagesByChat(chatId);
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
                {messages ? <InfiniteScroll
                    scrollableTarget="scrollableDiv"
                    dataLength={messages.length} //This is important field to render the next data
                    next={async () => await fetchMoreMessagesByChat(chatId)}
                    hasMore={true}
                    loader={<h4>Loading...</h4>}
                    endMessage={
                        <p style={{textAlign: 'center'}}>
                            <b>Yay! You have seen it all</b>
                        </p>
                    }
                    inverse={true}
                    style={{
                        display: 'flex',
                        flexDirection: 'column-reverse',
                        gap: 16,
                        paddingLeft: 16,
                        paddingRight: 16
                    }}
                    // below props only if you need pull down functionality
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
                </InfiniteScroll> : <LoadingOverlay style={{backgroundOpacity: 0.5, blur: 4}} visible={true}/>}
            </div>
        </>
    );
}

export default MessageList;