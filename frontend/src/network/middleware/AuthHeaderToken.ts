import {FetchParams, Middleware, ResponseContext} from "../../api-wrapper";
import 'firebase/auth';
import auth from "../../firebase/auth";

export class AuthHeaderToken implements Middleware {
    public async pre(context: ResponseContext): Promise<FetchParams | void> {
        const accessToken = await auth.currentUser?.getIdToken()
        console.log("jwt: " + accessToken) // TODO only print in dev environment

        return {
            url: context.url,
            init: accessToken ? {
                ...context.init,
                headers: new Headers({
                    ...context.init.headers,
                    Authorization: `Bearer ${accessToken}`,
                }),
            } : context.init,
        };
    }

    public post(context: ResponseContext): Promise<Response | void> {
        return Promise.resolve(context.response);
    }
}
