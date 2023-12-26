import {create} from 'zustand'

interface User{
    firstName: string,
    lastName: string
    email: string,
    username: string,
    avatar: string | null,
    balance: number
}

interface AppState {
    sideBarOpen: boolean
    selectedChat: string | null
    selectChat: (chatId: string) => void
    user: User | null,
    setUser: (user: User | null) => void
}


export const useAppStore = create<AppState>((set) => ({
    sideBarOpen: false,
    selectedChat: null,
    selectChat: (chatId) => {
        set({selectedChat: chatId})
    },
    user: null,
    setUser: (user) => {
        set({user});
    }
}))