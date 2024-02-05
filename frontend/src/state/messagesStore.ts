import {create} from 'zustand'
import {persist, PersistStorage} from 'zustand/middleware'
import superjson from "superjson";
import {api} from "../network/api";
import {Message} from "../model/Message";
import {Message as ApiMessage} from "../api-wrapper";
import {useUserStore} from "./userStore";

interface MessagesState {
    // maps chatId to its respective messages
    messages: { [key: string]: Message[] | undefined }
    // fetches messages for specified chat in specified direction. Until end = true -> fetches all possible chat in specified direction. Not recommended for direction "PAST".
    // returns false if no more messages in that direction are to come
    fetchMoreMessagesByChat: (chatId: string, direction: "PAST" | "FUTURE", untilEnd: boolean) => Promise<boolean>
}

const local: PersistStorage<MessagesState> = {
    getItem: (name) => {
        const str = localStorage.getItem(name)
        if (!str) return null
        return superjson.parse(str)
    },
    setItem: (name, value) => {
        localStorage.setItem(name, superjson.stringify(value))
    },
    removeItem: (name) => localStorage.removeItem(name),
}

export const useMessagesStore = create<MessagesState>()(
    persist(
        (set, get) => (
            {
                // always be aware that this state might not yet have an entry for a chat -> type is undefined, not an empty list!
                // messages[<id>][0] is the newest message!
                messages: {},
                fetchMoreMessagesByChat: async (chatId: string, direction: "PAST" | "FUTURE", untilEnd: boolean) => {
                    let currentMessages = get().messages[chatId] || [];
                    let d: Date;
                    const pageSize = 10;

                    if (currentMessages.length === 0) {
                        // if we have no messages in the storage we have no entry date to fetch from.
                        // -> We can only use the current time and fetch a few messages into the past
                        d = new Date();
                        direction = "PAST"
                        untilEnd = false
                    } else {
                        // otherwise use the date from either the last or first message, we have, and fetch into the past or future
                        d = direction === "PAST" ? new Date(currentMessages[currentMessages.length - 1].date) : new Date(currentMessages[0].date);
                    }

                    let fetchedMessages: Message[] = []
                    try {
                        do {
                            const n = (await api.getMessages({
                                chatId: chatId,
                                fromDate: d,
                                direction: direction,
                                pageSize: pageSize
                            })).messages.map(transformMessage);
                            fetchedMessages = fetchedMessages.concat(n);
                            if (n.length < pageSize) {
                                break;
                            }
                        } while (untilEnd)
                    } catch (err) {
                        return true;
                    }

                    if (fetchedMessages.length > 0) {
                        const updatedMessages = get().messages;

                        if (direction === "PAST") {
                            updatedMessages[chatId] = [...currentMessages, ...fetchedMessages];
                        } else {
                            updatedMessages[chatId] = [...fetchedMessages, ...currentMessages];
                        }
                        set({messages: updatedMessages});
                        return fetchedMessages.length % pageSize === 0
                    } else {
                        return false;
                    }
                }
            }
        ),
        {
            name: 'messages-storage',
            storage: local,
        },
    ),
)

function transformMessage(msg: ApiMessage): Message {
    const uid = useUserStore.getState().user?.id! // dangerous

    let type: "text" | "payment" | "media" = "text"
    if (msg.transactionId) {
        type = "payment"
    } else if (msg.mediaPath) {
        type = "media"
    }
    return {
        id: msg.id,
        type: type,
        message: msg.message,
        amount: -1, // TODO payment not implemented
        mediaId: msg.mediaPath,
        fromMe: msg.senderId === uid,
        status: "sent",
        date: msg.sentTimestamp,
    }
}
