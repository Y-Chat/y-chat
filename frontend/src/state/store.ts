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
    selectedChat: string | null
    selectChat: (chatId: string) => void
    user: User | null,
    setUser: (user: User) => void
}


export const useAppStore = create<AppState>((set) => ({
    selectedChat: null,
    selectChat: (chatId) => {
        set({selectedChat: chatId})
    },
    user: null,
    setUser: (user) => {
        set({user});
    }
}))