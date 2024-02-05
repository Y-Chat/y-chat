import {create} from 'zustand'
import {persist, PersistStorage} from 'zustand/middleware'
import superjson from "superjson";
import {ImageWrapper} from "../model/ImageWrapper";
import {getImageUrl} from "../network/media";

interface ImagesState {
    // maps objectId to its respective ImageWrapper
    cachedImages: { [key: string]: ImageWrapper | undefined }
    // fetches the Image URL for the respective objectId from the media service if not yet in cache.
    // Refreshes URL if it's close to its expiry date
    fetchImageUrl: (objectId: string) => Promise<void>
}

const local: PersistStorage<ImagesState> = {
    getItem: (name) => {
        const str = localStorage.getItem(name)
        if (!str) return null
        return superjson.parse(str)
    },
    setItem: (name, value) => {
        localStorage.setItem(name, superjson.stringify(value))
    },
    removeItem: (name) => localStorage.removeItem(name),
}

export const useImagesStore = create<ImagesState>()(
    persist(
        (set, get) => (
            {
                cachedImages: {},
                fetchImageUrl: async (objectId: string) => {
                    const current = get().cachedImages;
                    const now = new Date();

                    const img = current[objectId]
                    if (img && img.expires > now) {
                        return;
                    }

                    const url = await getImageUrl(objectId);
                    const expiresIn = 2 * 60 * 60 * 1000
                    if (url) {
                        current[objectId] = {
                            url: url,
                            expires: new Date(now.getTime() + expiresIn)
                        }
                        set({cachedImages: {...current}})
                    }
                    return;
                }
            }
        ),
        {
            name: 'images-storage',
            storage: local,
        },
    ),
)
