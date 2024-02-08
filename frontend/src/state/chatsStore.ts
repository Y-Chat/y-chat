import {create} from 'zustand'
import {persist, PersistStorage} from 'zustand/middleware'
import {api} from "../network/api";
import superjson from 'superjson';
import {Chat} from "../model/Chat";
import {ChatDTO} from "../api-wrapper";
import {useUserStore} from "./userStore";
import {useMessagesStore} from "./messagesStore";

interface ChatsState {
    chats: Chat[],
    getChat: (chatId: string) => Promise<Chat | null>
    fetchChats: () => Promise<void>
    refreshAdditionalInfo: () => void // refreshes date and last message,
    setNewestReadMessageDate: (chatId: string, date: Date) => void,
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
                getChat: async (chatId: string) => {
                    const userId = useUserStore.getState().user?.id;
                    if (!userId) {
                        return null;
                    }

                    let chat = get().chats.find((chat) => chat.id == chatId);
                    // fetch if we don't have this chat cached locally
                    if (!chat) {
                        try {
                            chat = transformChat(await api.getChat({userId: userId, chatId: chatId}));
                            // if we fetched a new chat -> add it to our store
                            set({chats: [...get().chats, chat]});
                        } catch (err) {
                            return null;
                        }
                    }
                    return chat;
                },
                fetchChats: async () => {
                    const userId = useUserStore.getState().user?.id;
                    if (!userId) {
                        return;
                    }
                    try {
                        const pageSize = 30
                        let fetchedChats: Chat[] = []
                        do {
                            const resp = await api.getAllChats({
                                userId: userId,
                                pageable: {
                                    size: pageSize
                                }
                            });
                            const chats = resp.content?.map(transformChat) || [];
                            fetchedChats = fetchedChats.concat(chats);
                            if (chats.length < pageSize) {
                                break;
                            }
                        } while (true);
                        const oldChats = get().chats;
                        const oldChatMap: {[id: string]: Chat} = {}
                        oldChats.forEach(x => oldChatMap[x.id] = x)
                        fetchedChats.forEach(x => {
                            if(x.id in oldChatMap) {
                                x.newestReadMessageDate = oldChatMap[x.id].newestReadMessageDate;
                            }
                        })
                        set({chats: fetchedChats});
                        get().refreshAdditionalInfo();
                    } catch (err) {
                        console.log(err)
                    }
                },
                refreshAdditionalInfo: () => {
                    const currChats = get().chats;
                    const currMessages = useMessagesStore.getState().messages;
                    const updatedChats = currChats.map(chat => {
                        const msgs = currMessages[chat.id];
                        if (msgs && msgs.length > 0) {
                            const recentDate = msgs[0].date;
                            const recentMessage = msgs[0].mediaId ? "Image" : msgs[0].message;
                            const newChat: Chat = {
                                ...chat,
                                date: recentDate,
                                lastMessage: recentMessage
                            }
                            return newChat;
                        }
                        return chat;
                    });
                    set({chats: updatedChats});
                },
                setNewestReadMessageDate: (chatId: string, date: Date) => {
                    set((state) => {
                        const chat = state.chats.find((x) => x.id === chatId);
                        if(!chat) return state;
                        chat.newestReadMessageDate = date;

                        return {
                            ...state,
                            chats: [...state.chats.filter(x => x.id !== chatId), chat]
                        }
                    })
                }
            }
        ),
        {
            name: 'chats-storage',
            storage:
            local,
        }
        ,
    ),
)

function transformChat(apiChat: ChatDTO): Chat {
    let name: string = "Chat";
    let avatar: string | undefined = undefined;

    if (apiChat.userProfileDTO) {
        name = `${apiChat.userProfileDTO.firstName} ${apiChat.userProfileDTO.lastName}`;
        avatar = apiChat.userProfileDTO.profilePictureId;
    } else if (apiChat.groupProfileDTO) {
        name = apiChat.groupProfileDTO.groupName;
        avatar = apiChat.groupProfileDTO.profilePictureId;
    }

    return {
        id: apiChat.chatId,
        avatarId: avatar,
        name: name,
        newestReadMessageDate: null,
        userInfo: apiChat.userProfileDTO && {status: "Hey there, I'm using Y-Chat"},
        groupInfo: apiChat.groupProfileDTO && {description: apiChat.groupProfileDTO.profileDescription || ""},
        archived: false,
    };
}
