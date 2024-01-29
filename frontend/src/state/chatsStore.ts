import {create} from 'zustand'
import {persist, PersistStorage} from 'zustand/middleware'
import {api} from "../network/api";
import superjson from 'superjson';
import {Chat} from "../model/Chat";

interface ChatsState {
    chats: Chat[],
    selectedChat: Chat | null
    setSelectedChat: (chatId: string) => void
    fetchChats: (userId: string) => Promise<void>
}

const local: PersistStorage<ChatsState> = {
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

export const useChatsStore = create<ChatsState>()(
    persist(
        (set, get) => (
            {
                chats: [],
                selectedChat: null,
                setSelectedChat: (chatId: string) => {
                    const chat = get().chats.find((chat) => chat.id == chatId)
                    if (chat) {
                        set({selectedChat: chat});
                    }
                },
                fetchChats: async (userId: string) => {
                    const resp = await api.getAllChats({
                        userId: userId,
                        pageable: {
                            size: 100 // TODO
                        }
                    });
                    const chats = resp.content?.map(chat => {
                        let name: string = "Chat";
                        let avatar: string | undefined = undefined;

                        if (chat.userProfileDTO) {
                            name = `${chat.userProfileDTO.firstName} ${chat.userProfileDTO.lastName}`;
                            avatar = chat.userProfileDTO.profilePictureId; // TODO
                        } else if (chat.groupProfileDTO) {
                            name = chat.groupProfileDTO.groupName;
                            avatar = chat.groupProfileDTO.profilePictureId; // TODO
                        }


                        const transformedChat: Chat = {
                            id: chat.chatId,
                            avatar: null,
                            name: name,
                            email: "email@user.com",
                            lastMessage: "Hey whad up? I was sondering how to do something lol i am just writitng words!",
                            newMessages: 1,
                            groupInfo: chat.groupProfileDTO ? {description: chat.groupProfileDTO.profileDescription || ""} : undefined,
                            archived: false,
                            date: new Date(Math.random() * 1000000000000) // TODO calc date
                        }
                        return transformedChat;
                    })
                    set({chats: chats});
                }
            }
        ),
        {
            name: 'chats-storage',
            storage: local,
        },
    ),
)