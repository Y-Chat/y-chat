import {FetchParams, Middleware, ResponseContext} from "../../api-wrapper";
import 'firebase/auth';
import auth from "../../firebase/auth";

export class AuthHeaderToken implements Middleware {
    public async pre(context: ResponseContext): Promise<FetchParams | void> {
        const accessToken = await auth.currentUser?.getIdToken()
        return {
            url: context.url,
            init: {
                ...context.init,
                headers: new Headers({
                    ...context.init.headers,
                    Authorization: `Bearer ${accessToken}`,
                }),
            },
        };
    }

    public post(context: ResponseContext): Promise<Response | void> {
        return Promise.resolve(context.response);
    }
}