import {Configuration, DefaultApi} from "../api-wrapper";
import {AuthHeaderToken} from "./middleware/AuthHeaderToken";

const fallbackApiEndpoint = "http://localhost:7500"

export let api = new DefaultApi(new Configuration({
    basePath: process.env.REACT_APP_API ?? fallbackApiEndpoint,
    middleware: [new AuthHeaderToken()]
}))

export let accessToken: string | null = ""

export function setApiAccessToken(token?: string) {
    if (!token) {
        api = new DefaultApi(new Configuration({
            basePath: process.env.REACT_APP_API ?? fallbackApiEndpoint,
            middleware: [new AuthHeaderToken()]
        }))
        accessToken = null;
    } else {
        api = new DefaultApi(new Configuration({
            basePath: process.env.REACT_APP_API ?? fallbackApiEndpoint,
            accessToken: token,
            middleware: [new AuthHeaderToken()]
        }))
        console.log("setting accessToken to " + token)
        accessToken = token;
    }
}
