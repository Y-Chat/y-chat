import {api} from "./api";
import {getDownloadURL, ref, uploadBytes} from "firebase/storage";
import {storageRef} from "../firebase/storage";

// get downloadable URL for objectName
export async function getImageUrl(objectName: string): Promise<string> {
    if (objectName.startsWith("chats/")) {
        // get signed file url from media server because for chat media, extra permissions have to be checked
        const resp = await api.getMedia({objectName})
        return resp.url
    }
    const objectRef = ref(storageRef, objectName);
    return await getDownloadURL(objectRef);
}

// upload file to path (path including file name). Returns objectId on successful upload.
export async function uploadImage(file: File, path: string): Promise<string> {
    const fileRef = ref(storageRef, path);
    const upload = await uploadBytes(fileRef, file);
    return upload.ref.fullPath;
}