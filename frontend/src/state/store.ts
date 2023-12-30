import {create} from 'zustand'
import {User} from "../model/User";

interface AppState {
    sideBarOpen: boolean
    selectedChat: string | null
    selectChat: (chatId: string) => void
    user: User | null,
    userLoading: boolean,
    setUserLoading: (userLoading: boolean) => void
    setUser: (user: User | null) => void
}

export const useAppStore = create<AppState>((set) => ({
    sideBarOpen: false,
    selectedChat: null,
    selectChat: (chatId) => {
        set({selectedChat: chatId})
    },
    user: null,
    userLoading: false,
    setUserLoading: (userLoading) => {
        set({userLoading});
    },
    setUser: (user) => {
        set({user});
    }
}))