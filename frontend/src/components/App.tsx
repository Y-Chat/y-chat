import React from 'react';
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
import {NewGroupChat} from "./newChat/NewGroupChat";
import {NotFound} from "./404/NotFound";
import {Welcome} from "./common/Welcome";
import {isMobile} from "react-device-detect";
import {HowToInstall} from "./common/HowToInstall";
import {useSettingsStore} from "../state/settingsStore";

function App() {

    const [firebaseUser, loading] = useAuthState(auth);
    const user = useUserStore((state) => state.user);
    const primaryColor = useSettingsStore((state) => state.primaryColor);

    // otherwise show how to install instruction
    const showApp = (window.matchMedia('(display-mode: standalone)').matches && isMobile) || process.env.NODE_ENV == "development" || true // TODO remove true when actually in prod
    return (
        <MantineProvider theme={{primaryColor}} defaultColorScheme="dark" forceColorScheme="dark">
            {showApp ?
                <>
                    <PermissionsModal/>
                    <Notifications zIndex={10000} autoClose={5000} position="top-right"/>
                    <LoadingOverlay visible={loading} zIndex={1000} overlayProps={{radius: 0, blur: 10}}/>
                    <Router>
                        {user && firebaseUser ?
                            <Routes>
                                <Route path="/" element={<Shell/>}>
                                    <Route path="/" element={<Welcome/>}/>
                                    <Route path="/account" element={<AccountMain/>}/>
                                    <Route path="/newGroup" element={<NewGroupChat/>}/>
                                    <Route path="/chat/:chatId" element={<ChatLoader/>}/>
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
