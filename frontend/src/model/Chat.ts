export interface Chat {
    id: string
    avatarId?: string,
    name: string,
    newMessages: number,
    userInfo?: UserInfo // only in FirectChat
    groupInfo?: GroupInfo // only in GroupChat
    archived: boolean
    date: Date,
}

interface GroupInfo {
    description: string,
}

interface UserInfo {
    status: string,
}