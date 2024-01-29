export interface Chat {
    id: string
    avatar: null | string,
    name: string,
    email?: string, // only in DirectChat
    newMessages: number,
    lastMessage: string,
    groupInfo?: GroupInfo // only in GroupChat
    archived: boolean
    date: Date
}

interface GroupInfo {
    description: string
}