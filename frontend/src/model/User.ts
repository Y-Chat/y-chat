import firebase from "firebase/compat";

export interface User {
    firstName: string,
    lastName: string
    email: string,
    username: string,
    avatar: string | null,
    balance: number
}