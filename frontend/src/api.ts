import {Configuration, DefaultApi} from "./api-wrapper";
import {timeout} from "workbox-core/_private";

const fallbackApiEndpoint = "http://localhost:7500"

export let api = new DefaultApi(new Configuration({basePath: process.env.REACT_APP_API ?? fallbackApiEndpoint}))

export function setApiAccessToken(token: any) {
    if(!token) {
        api = new DefaultApi(new Configuration({basePath: process.env.REACT_APP_API ?? fallbackApiEndpoint}))
    } else {
        api = new DefaultApi(new Configuration({basePath: process.env.REACT_APP_API ?? fallbackApiEndpoint, accessToken: token}))
    }
}
