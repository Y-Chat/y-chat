import {create} from 'zustand'
import {User} from "../model/User";
import {persist, PersistStorage} from 'zustand/middleware'
import superjson from "superjson";

interface SettingsState {
    primaryColor: string,
    setPrimaryColor: (color: string) => void
}

const local: PersistStorage<SettingsState> = {
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

export const useSettingsStore = create<SettingsState>()(
    persist(
        (set, get) => (
            {
                primaryColor: 'violet',
                setPrimaryColor: (color: string) => {
                    set({primaryColor: color});
                }
            }
        ),
        {
            name: 'settings-storage',
            storage: local,
        },
    ),
)