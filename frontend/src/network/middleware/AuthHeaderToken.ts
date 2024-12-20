import {FetchParams, Middleware, ResponseContext} from "../../api-wrapper";
import 'firebase/auth';
import auth from "../../firebase/auth";

let printedOnce = false;

export class AuthHeaderToken implements Middleware {
    public async pre(context: ResponseContext): Promise<FetchParams | void> {
        const accessToken = await auth.currentUser?.getIdToken();
        if (process.env.NODE_ENV === "development" && !printedOnce) {
            console.log("jwt: " + accessToken);
            printedOnce = true;
        }

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
