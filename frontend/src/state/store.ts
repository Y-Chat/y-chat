import {create} from 'zustand'
import {User} from "../model/User";
import {persist, createJSONStorage, StateStorage} from 'zustand/middleware'
import { get, set, del } from 'idb-keyval';
import firebase from "firebase/compat";

const storage: StateStorage = {
    getItem: async (name: string): Promise<string | null> => {
        return (await get(name)) || null
    },
    setItem: async (name: string, value: string): Promise<void> => {
        await set(name, value)
    },
    removeItem: async (name: string): Promise<void> => {
        await del(name)
    },
}

interface AppState {
    sideBarOpen: boolean
    selectedChat: string | null
    selectChat: (chatId: string) => void
    user: User | null,
    setUser: (user: User | null) => void
}

export const useAppStore = create<AppState>()(
    persist(
        (set, get) => (
            {
                sideBarOpen: false,
                selectedChat: null,
                selectChat: (chatId) => {
                    set({selectedChat: chatId})
                },
                user: null,
                setUser: (user) => {
                    set({user});
                }
            }
        ),
        {
            name: 'app-storage',
            storage: createJSONStorage(() => storage), // (optional) by default, 'localStorage' is used
        },
    ),
)