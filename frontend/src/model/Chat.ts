export interface Chat {
    id: string
    avatarId?: string,
    name: string,
    newestReadMessageDate: Date | null,
    userInfo?: UserInfo // only in DirectChat
    groupInfo?: GroupInfo // only in GroupChat
    archived: boolean
    date?: Date,
    lastMessage?: string,
}

interface GroupInfo {
    description: string,
}

interface UserInfo {
    status: string,
}
