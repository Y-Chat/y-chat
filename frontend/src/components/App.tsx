import React, {useEffect} from 'react';
import {BrowserRouter as Router, Route, Routes} from "react-router-dom";
import {LoadingOverlay, MantineProvider} from "@mantine/core";
import {transformUser, useUserStore} from "../state/userStore";
import AuthMain from "./auth/AuthMain";
import Shell from "./shell/Shell";
import ChatLoader from "./chat/ChatLoader";
import {AccountMain} from "./account/AccountMain";
import '@mantine/core/styles.css';
import '@mantine/notifications/styles.css';
import {Notifications} from "@mantine/notifications";
import {useAuthState} from "react-firebase-hooks/auth";
import auth from "../firebase/auth";
import ChatCall from "./chat/ChatCall";
import {NewGroupChat} from "./newChat/NewGroupChat";
import {NotFound} from "./404/NotFound";
import {Welcome} from "./common/Welcome";
import {isMobile} from "react-device-detect";
import {HowToInstall} from "./common/HowToInstall";
import {accessToken, api} from "../network/api";
import {getMessaging, getToken} from "firebase/messaging";
import firebaseApp from "../firebase/firebaseApp";
import {setupNotificationHandler, unsubscribeNotifications, vapidKey} from "../firebase/messaging";
import {useSettingsStore} from "../state/settingsStore";
import CallingWrapper from "./shell/CallingWrapper";
import getUuidByString from "uuid-by-string";

function App() {

    const [firebaseUser, loading] = useAuthState(auth);
    const user = useUserStore((state) => state.user);
    const primaryColor = useSettingsStore((state) => state.primaryColor);
    const showAppInstructions = (isMobile && !window.matchMedia('(display-mode: standalone)').matches) && process.env.NODE_ENV !== "development";
    const setUser = useUserStore((state) => state.setUser)

    useEffect(() => {
        if(!auth.currentUser) return;
        api.getUser({userId: getUuidByString(auth.currentUser.uid, 3)}).then((user) => {
            setUser(transformUser(user, auth.currentUser?.email!))
        }).catch((err) => console.error(err));
    }, [auth.currentUser]);

    useEffect(() => {
        const messaging = getMessaging(firebaseApp);

        if(!auth.currentUser) {
            unsubscribeNotifications()
        } else {
            setupNotificationHandler(auth.currentUser.uid)
        }

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
            return api.updateToken({notificationToken: tokens.notificationToken}, {headers: new Headers({Authorization: `Bearer ${tokens.accessToken}`})})
        }).catch((err) => {
            console.log('An error occurred while retrieving token. ', err);
        })
    }, [auth.currentUser, navigator.serviceWorker.controller?.state]);

    return (
        <MantineProvider theme={{primaryColor}} defaultColorScheme="dark" forceColorScheme="dark">
            {!showAppInstructions ?
                <>
                    <Notifications zIndex={10000} autoClose={5000} position="top-right"/>
                    <LoadingOverlay visible={loading} zIndex={1000} overlayProps={{radius: 0, blur: 10}}/>
                    <Router>
                        {user && firebaseUser ?
                            <Routes>
                                <Route path={"/"} element={<CallingWrapper/>}>
                                    <Route path="/" element={<Shell/>}>
                                        <Route path="/" element={<Welcome/>}/>
                                        <Route path="/account" element={<AccountMain/>}/>
                                        <Route path="/newGroup" element={<NewGroupChat/>}/>
                                        <Route path="/chat/:chatId" element={<ChatLoader/>}/>
                                        <Route path={"/call"} element={<ChatCall/>}/>
                                        <Route path="/*" element={<NotFound/>}/>
                                    </Route>
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
