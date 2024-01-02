import React, {useState} from 'react';
import {BrowserRouter as Router, Navigate, Route, Routes} from "react-router-dom";
import {Button, Center, MantineProvider, Modal} from "@mantine/core";
import {MantineThemeOverride} from "@mantine/core/lib/core/MantineProvider/theme.types";
import {useAppStore} from "../state/store";
import AuthMain from "./auth/AuthMain";
import Shell from "./shell/Shell";
import ChatMain from "./chat/ChatMain";
import {AccountMain} from "./account/AccountMain";
import '@mantine/core/styles.css';
import '@mantine/notifications/styles.css';
import {Notifications} from "@mantine/notifications";
import {hasPermission, requestNotificationPermissions} from "../firebase/firebaseMessaging";

function App() {
    const [showPermissions, setShowPermissions] = useState<boolean>(!hasPermission)

    const user = useAppStore((state) => state.user);

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
                <Modal
                    centered
                    closeOnEscape={false}
                    closeOnClickOutside={false}
                    withCloseButton={false}
                    title={"Notification Permissions"}
                    opened={showPermissions}
                    onClose={() => {
                    }}>
                    <Center>
                        <Button
                            onClick={async () => {
                                const success = await requestNotificationPermissions();
                                setShowPermissions(!success);
                            }}
                        >
                            Request Permission
                        </Button>
                    </Center>
                </Modal>
                <Notifications autoClose={5000} position="top-right"/>
                <Router>
                    {user ?
                        <Routes>
                            <Route path="/" element={<Shell/>}>
                                <Route path="/" element={<ChatMain/>}/>
                                <Route path="/account" element={<AccountMain/>}/>
                            </Route>
                            <Route path="/*" element={<p>This should not happen</p>}/>
                        </Routes>
                        :
                        <Routes>
                            <Route path="/" element={<AuthMain/>}/>
                            <Route path="/*" element={<Navigate to={"/"} replace/>}/>
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
