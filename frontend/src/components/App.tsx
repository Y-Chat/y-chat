import React, {useEffect} from 'react';
import {BrowserRouter as Router, Route, Routes} from "react-router-dom";
import {LoadingOverlay, MantineProvider} from "@mantine/core";
import {MantineThemeOverride} from "@mantine/core/lib/core/MantineProvider/theme.types";
import {useUserStore} from "../state/userStore";
import AuthMain from "./auth/AuthMain";
import Shell from "./shell/Shell";
import ChatLoader from "./chat/ChatLoader";
import {AccountMain} from "./account/AccountMain";
import '@mantine/core/styles.css';
import '@mantine/notifications/styles.css';
import {Notifications} from "@mantine/notifications";
import {PermissionsModal} from "./common/PermissionsModal";
import {useAuthState} from "react-firebase-hooks/auth";
import auth from "../firebase/auth";
import ChatCall from "./chat/ChatCall";
import {NewGroupChat} from "./newChat/NewGroupChat";
import {NotFound} from "./404/NotFound";
import {Welcome} from "./common/Welcome";
import {isMobile} from "react-device-detect";
import {HowToInstall} from "./common/HowToInstall";
import {api} from "../network/api";
import {getMessaging, getToken} from "firebase/messaging";
import firebaseApp from "../firebase/firebaseApp";
import {vapidKey} from "../firebase/messaging";

function App() {

    const [firebaseUser, loading] = useAuthState(auth);
    const user = useUserStore((state) => state.user);

    // otherwise show how to install instruction
    const showApp = (window.matchMedia('(display-mode: standalone)').matches && isMobile) || process.env.NODE_ENV == "development"

    useEffect(() => {
        const messaging = getMessaging(firebaseApp);

        if(auth.currentUser === null) return;

        auth.currentUser?.getIdToken().then((accessToken) => {
            return accessToken;
        }).then(async (accessToken) => {
            const notificationToken = await getToken(messaging, {vapidKey: vapidKey});

            if (!notificationToken)
                console.log('No registration token available. Request permission to generate one.');

            if (process.env.NODE_ENV === "development") {
                console.log("FBC token: " + notificationToken);
            }

            return {notificationToken: notificationToken, accessToken: accessToken};
        }).then((tokens) => {
            api.updateToken({notificationToken: tokens.notificationToken}, {headers: new Headers({Authorization: `Bearer ${tokens.accessToken}`})})
        }).catch((err) => {
            console.log('An error occurred while retrieving token. ', err);
        })
    }, [auth.currentUser]);

    const theme: MantineThemeOverride = {
        primaryColor: "mainColors",
        primaryShade: 6,
        colors: {
            "mainColors": [
                "#f3edff",
                "#e0d7fa",
                "#beabf0",
                "#9a7ce6",
                "#7c56de",
                "#683dd9",
                "#5f2fd8",
                "#4f23c0",
                "#451eac",
                "#3a1899"
            ]
        }
    }

    return (
        <MantineProvider theme={theme} defaultColorScheme="dark">
            {showApp ?
                <>
                    <PermissionsModal/>
                    <Notifications autoClose={5000} position="top-right" zIndex={2001}/>
                    <LoadingOverlay visible={loading} zIndex={1000} overlayProps={{radius: 0, blur: 10}}/>
                    <Router>
                        {user && firebaseUser ?
                            <Routes>
                                <Route path="/" element={<Shell/>}>
                                    <Route path="/" element={<Welcome/>}/>
                                    <Route path="/account" element={<AccountMain/>}/>
                                    <Route path="/newGroup" element={<NewGroupChat/>}/>
                                    <Route path="/chat/:chatId" element={<ChatLoader/>}/>
                                    <Route path={"/call"} element={<ChatCall/>}/>
                                    <Route path="/*" element={<NotFound/>}/>
                                </Route>
                            </Routes>
                            :
                            <Routes>
                                <Route path="/" element={<AuthMain/>}/>
                                <Route path="/*" element={<NotFound/>}/>
                            </Routes>
                        }
                    </Router>
                </>
                :
                <HowToInstall/>}
        </MantineProvider>
    );
}

export default App;
