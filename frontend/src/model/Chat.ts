import {Message} from "./Message";

export interface Chat {
    id: string
    avatar: null | string,
    name: string,
    email?: string, // only in DirectChat
    newMessages: number,
    lastMessage: string,
    groupInfo?: GroupInfo // only in GroupChat
    archived: boolean
    date: Date,
    messages: Message[]
}

interface GroupInfo {
    description: string
}