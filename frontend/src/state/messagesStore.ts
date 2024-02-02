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

                    if (currentMessages.length == 0) {
                        d = new Date();
                    } else {
                        d = direction == "PAST" ? new Date(currentMessages[currentMessages.length - 1].date) : new Date(currentMessages[0].date);
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
                        return fetchedMessages.length % pageSize == 0
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

    let t: "text" | "payment" | "media" = "text"
    if (msg.transactionId) {
        t = "payment"
    } else if (msg.mediaPath) {
        t = "media"
    }
    return {
        id: msg.id,
        type: t,
        message: msg.message,
        amount: -1, // TODO payment not implemented
        mediaUrl: msg.mediaPath,
        fromMe: msg.senderId == uid,
        status: "sent",
        date: msg.sentTimestamp,


    }

}

/*    const msgs: Message[] = [
        {type: "text", message: "Message1", fromMe: true, status: "read"},
        {type: "text", message: "Message2", fromMe: false, status: "read"},
        {
            type: "text",
            message: "Message3 Message3 Message3 Message3 Message3 Message3 Message3 Message3 Message3Message3Message3 Message3 Message3 Message3 Message3",
            fromMe: true,
            status: "read"
        },
        {type: "text", message: "Message4", fromMe: true, status: "read"},
        {type: "text", message: "Message5", fromMe: false, status: "read"},
        {type: "text", message: "Message6", fromMe: false, status: "read"},
        {type: "text", message: "Message7Message7Message7", fromMe: false, status: "read"},
        {type: "text", message: "Message8", fromMe: true, status: "read"},
        {type: "text", message: "Message10", fromMe: true, status: "read"},
        {type: "text", message: "Message11", fromMe: false, status: "read"},
        {type: "text", message: "Message9", fromMe: true, status: "read"},
        {type: "text", message: "Message12", fromMe: true, status: "read"},
        {type: "text", message: "Message12", fromMe: true, status: "read"},
        {type: "text", message: "Message12", fromMe: true, status: "read"},
        {type: "text", message: "Message12", fromMe: true, status: "read"},
        {type: "text", message: "Message12", fromMe: true, status: "read"},
        {type: "text", message: "Message12", fromMe: true, status: "read"},
        {type: "text", message: "Message12", fromMe: true, status: "read"},
        {type: "text", message: "Message12", fromMe: true, status: "read"},
        {type: "text", message: "Message12", fromMe: true, status: "read"},
        {type: "text", message: "Message12", fromMe: true, status: "read"},
        {type: "text", message: "Message12", fromMe: true, status: "read"},
        {type: "text", message: "Message12", fromMe: true, status: "read"},
        {type: "text", message: "Message12", fromMe: true, status: "read"},
        {type: "text", message: "Message12", fromMe: true, status: "read"},
        {type: "text", message: "Message12", fromMe: true, status: "read"},
        {type: "text", message: "Message12", fromMe: true, status: "read"},
        {type: "text", message: "Message12", fromMe: true, status: "read"},
        {type: "text", message: "Message12", fromMe: true, status: "read"},
        {type: "text", message: "Message12", fromMe: true, status: "read"},
        {type: "text", message: "Message12", fromMe: true, status: "read"},
        {type: "text", message: "Message12", fromMe: true, status: "read"},
        {type: "text", message: "Message12", fromMe: true, status: "read"},
        {type: "text", message: "Message12", fromMe: true, status: "read"},
        {type: "text", message: "Message12", fromMe: true, status: "read"},
        {type: "text", message: "Message12", fromMe: true, status: "read"},
        {
            type: "media",
            mediaUrl: "https://img.freepik.com/free-photo/painting-mountain-lake-with-mountain-background_188544-9126.jpg?size=626&ext=jpg&ga=GA1.1.1546980028.1703635200&semt=sph",
            message: "Look at this view. Wow!",
            fromMe: true,
            status: "read"
        },
        {type: "text", message: "Message12", fromMe: true, status: "read"},
        {
            type: "text",
            message: "Message13 Message13 Message13 Message13 Message13 Message13 Message13 Message13",
            fromMe: true,
            status: "read"
        },
    ];*/