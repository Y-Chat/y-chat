import {getStorage, ref} from "firebase/storage";

export const storage = getStorage();

export const storageRef = ref(storage)
export const profilePicturesRef = ref(storage, "profilePictures")
export const chatsRef = ref(storage, "chats")
