import {create} from 'zustand'
import {User} from "../model/User";
import {persist, PersistStorage} from 'zustand/middleware'
import superjson from "superjson";

interface AppState {
    user: User | null,
    setUser: (user: User | null) => void,
}

const local: PersistStorage<AppState> = {
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

export const useUserStore = create<AppState>()(
    persist(
        (set, get) => (
            {
                user: null,
                setUser: (user) => {
                    set({user});
                }
            }
        ),
        {
            name: 'user-storage',
            storage: local,
        },
    ),
)