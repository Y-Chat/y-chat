import React from 'react';
import {BrowserRouter as Router, Route, Routes} from "react-router-dom";
import {LoadingOverlay, MantineProvider} from "@mantine/core";
import {MantineThemeOverride} from "@mantine/core/lib/core/MantineProvider/theme.types";
import {useUserStore} from "../state/userStore";
import AuthMain from "./auth/AuthMain";
import Shell from "./shell/Shell";
import ChatMain from "./chat/ChatMain";
import {AccountMain} from "./account/AccountMain";
import '@mantine/core/styles.css';
import '@mantine/notifications/styles.css';
import {Notifications} from "@mantine/notifications";
import {PermissionsModal} from "./common/PermissionsModal";
import {useAuthState} from "react-firebase-hooks/auth";
import auth from "../firebase/auth";
import {useChatsStore} from "../state/chatsStore";
import {NewGroupChat} from "./newChat/NewGroupChat";
import {NotFound} from "./404/NotFound";

function App() {

    const [firebaseUser, loading] = useAuthState(auth);
    const user = useUserStore((state) => state.user);

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
        <div>
            {/*<BrowserView>*/}
            {/*    <NotMobile/>*/}
            {/*</BrowserView>*/}
            {/*<MobileView>*/}
            <MantineProvider theme={theme} defaultColorScheme="dark">
                <PermissionsModal/>
                <Notifications autoClose={5000} position="top-right"/>
                <LoadingOverlay visible={loading} zIndex={1000} overlayProps={{radius: 0, blur: 10}}/>
                <Router>
                    {user && firebaseUser ?
                        <Routes>
                            <Route path="/" element={<Shell/>}>
                                <Route path="/" element={<ChatMain/>}/>
                                <Route path="/account" element={<AccountMain/>}/>
                                <Route path="/newGroup" element={<NewGroupChat/>}/>
                            </Route>
                            <Route path="/*" element={<NotFound/>}/>
                        </Routes>
                        :
                        <Routes>
                            <Route path="/" element={<AuthMain/>}/>
                            <Route path="/*" element={<NotFound/>}/>
                        </Routes>
                    }
                </Router>
            </MantineProvider>
            {/*</MobileView>*/
            }
        </div>
    );
}

export default App;
