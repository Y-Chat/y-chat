import React from 'react';
import {MemoryRouter as Router, Route, Routes} from "react-router-dom";
import '@mantine/core/styles.css';
import {MantineProvider} from "@mantine/core";
import {MantineThemeOverride} from "@mantine/core/lib/core/MantineProvider/theme.types";
import {useAppStore} from "./state/store";
import AuthMain from "./components/auth/AuthMain";
import Shell from "./components/shell/Shell";

function App() {

    const user = useAppStore((state) => state.user);

    // TODO dev mode to disable auth and stuff
    const devMode: boolean = process.env.DEV_MODE == "true"

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
                <Router>
                    {user ?
                        <Routes>
                            <Route path="/" element={<Shell/>}/>
                        </Routes>
                        :
                        <Routes>
                            <Route path="/" element={<AuthMain/>}/>
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
