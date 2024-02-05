import {create} from 'zustand'
import {persist, PersistStorage} from 'zustand/middleware'
import {api} from "../network/api";
import superjson from 'superjson';
import {Chat} from "../model/Chat";
import {ChatDTO} from "../api-wrapper";
import {useUserStore} from "./userStore";

interface ChatsState {
    chats: Chat[],
    getChat: (chatId: string) => Promise<Chat | null>
    fetchChats: () => Promise<void>
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
                        const resp = await api.getAllChats({
                            userId: userId,
                            pageable: {
                                size: 100 // TODO social service paging does not work reliably atm. Fetch 100 for now
                            }
                        });
                        const chats = resp.content?.map(transformChat) || [];
                        set({chats: chats});
                    } catch (err) {
                        // TODO handle errors
                    }
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
        newMessages: 1,
        userInfo: apiChat.userProfileDTO && {status: "Hey there, I'm using Y-Chat"},
        groupInfo: apiChat.groupProfileDTO && {description: apiChat.groupProfileDTO.profileDescription || ""},
        archived: false,
        date: new Date(), // TODO calc date,
    };
}