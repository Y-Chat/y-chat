export interface Chat {
    id: string
    avatar: null | string,
    name: string,
    email: string,
    newMessages: number,
    lastMessage: string,
    isGroup: boolean,
    date: Date
}