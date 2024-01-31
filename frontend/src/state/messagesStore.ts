import {create} from 'zustand'
import {User} from "../model/User";
import {persist, PersistStorage} from 'zustand/middleware'
import superjson from "superjson";
import {Message} from "../model/Message";
import {useUserStore} from "./userStore";
import {Chat} from "../model/Chat";
import {api} from "../network/api";
import getUuidByString from "uuid-by-string";
import {debug} from "node:util";

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

interface temp {
    chatId: string,
    upwards: boolean,
    date: Date
}

/*let allChats: Message[] = [] TODO remove!

for (let i = 0; i < 50; i++) {
    const message = `Message${i + 1}`;
    const uuid = getUuidByString(message, 3);
    const fromMe = false; // Randomly true or false
    const object: Message = {
        id: uuid,
        type: 'text',
        message,
        fromMe,
        status: 'read',
        date: new Date(i * 31536000000)
    };
    allChats.push(object);
}

console.log(allChats)

function getChatsFromDate(date: Date, n: number, newer = false) {
    const sortedChats = allChats.sort((a, b) => b.date.getTime() - a.date.getTime());
    const index = sortedChats.findIndex(chat => (newer ? chat.date > date : chat.date < date));

    if (index !== -1) {
        if (newer) {
            return sortedChats.slice(index, index + 10);
        } else {
            return sortedChats.slice(index - n, index).reverse();
        }
    } else {
        return [];
    }
}

const b1 = getChatsFromDate(new Date(0), 10, true)
console.log(b1);
const b1 = getChatsFromDate(new Date(0), 10, true)*/


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

                    const updatedMessages = get().messages;
                    updatedMessages[chatId] = [...oldMessages, ...[]]

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