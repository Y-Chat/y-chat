import {create} from 'zustand'
import {User} from "../model/User";
import {persist, PersistStorage} from 'zustand/middleware'
import superjson from "superjson";
import {Message} from "../model/Message";
import {useUserStore} from "./userStore";
import {Chat} from "../model/Chat";
import {api} from "../network/api";

interface MessagesState {
    // maps chatId to its respective messages
    messages: { [key: string]: Message[] | undefined }
    fetchMoreMessagesByChat: (chatId: string) => Promise<void>
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
                fetchMoreMessagesByChat: async (chatId: string) => {
                    let oldMessages = get().messages[chatId];
                    let latest = new Date(0);

                    if (!oldMessages) {
                        oldMessages = []
                    } else {
                        // if we have messages for this chat, get the latest date
                        latest = oldMessages[0].date;
                    }

                    const fetchedMessages: Message[] = [{
                        type: "text",
                        message: "Message1",
                        fromMe: true,
                        status: "read",
                        date: new Date()
                    }, {
                        type: "text",
                        message: "Message2",
                        fromMe: true,
                        status: "read",
                        date: new Date()
                    }];

                    const updatedMessages = get().messages;
                    updatedMessages[chatId] = [...oldMessages, ...fetchedMessages]

                    set({messages: updatedMessages});
                }
            }
        ),
        {
            name: 'messages-storage',
            storage: local,
        },
    ),
)

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