import {api} from "./api";
import {getDownloadURL, ref, uploadBytes} from "firebase/storage";
import {storageRef} from "../firebase/storage";

// get downloadable URL for objectName
export async function getImageUrl(objectName: string): Promise<string | null> {
    // TODO theoretically we could add some caching mechanism here. Only fetch new URL if the URL in cache expired
    try {
        if (objectName.startsWith("chats/")) {
            // get signed file url from media server because for chat media, extra permissions have to be checked
            const resp = await api.getMedia({objectName})
            return resp.url
        }
        const objectRef = ref(storageRef, objectName);
        return await getDownloadURL(objectRef);
    } catch (err) {
        // TODO handle error
        return null
    }

}

// upload file to path (path including file name). Returns objectId on successful upload.
export async function uploadImage(file: File, path: string): Promise<string> {
    const fileRef = ref(storageRef, path);
    const upload = await uploadBytes(fileRef, file);
    return upload.ref.fullPath;
}