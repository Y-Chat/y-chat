export interface Message {
    id: string
    type:  "text" | "media" | "payment",
    message?: string, // all message types can have a text message attached to them
    amount?: number, // only payment messages
    mediaUrl?: string // only media messages
    fromMe: boolean,
    status: "sent" | "delviered" | "read",
    date: Date
}