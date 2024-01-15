import {Configuration, DefaultApi} from "../api-wrapper";
import {AuthHeaderToken} from "./middleware/AuthHeaderToken";

const fallbackApiEndpoint = "http://localhost:7500"

export let api = new DefaultApi(new Configuration({
    basePath: process.env.REACT_APP_API ?? fallbackApiEndpoint,
    middleware: [new AuthHeaderToken()]
}))

export function setApiAccessToken(token: any) {
    if (!token) {
        api = new DefaultApi(new Configuration({basePath: process.env.REACT_APP_API ?? fallbackApiEndpoint}))
    } else {
        api = new DefaultApi(new Configuration({
            basePath: process.env.REACT_APP_API ?? fallbackApiEndpoint,
            accessToken: token
        }))
    }
}
