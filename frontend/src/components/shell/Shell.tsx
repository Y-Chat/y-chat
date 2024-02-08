import React, {useEffect, useState} from "react";
import {Outlet, useLocation, useNavigate} from "react-router-dom";
import {AppShell, Burger, Center, Container, Divider, em, Group, ScrollArea} from "@mantine/core";
import {useDisclosure, useMediaQuery} from "@mantine/hooks";
import {ShellOutletContext} from "./ShellOutletContext";
import AccountBtn from "./AccountBtn";
import {ChatsList} from "./ChatsList";
import {IconBar} from "./IconBar";
import {setNotificationNavigate} from "../../firebase/messaging";


function Shell() {
    const [opened, {toggle}] = useDisclosure();
    const [header, setHeader] = useState(<></>);
    const [hideShell, setHideShell] = useState(false);
    const isMobile = useMediaQuery(`(max-width: ${em(770)})`);
    const location = useLocation();
    const navigate = useNavigate();
    setNotificationNavigate(navigate)

    // this just exists to guarantee type safety for ShellOutletContext
    const outletContext: ShellOutletContext = {
        setHeader,
        setHideShell
    }

    useEffect(() => {
        if(location.pathname === "/call") {
            setHideShell(true)
        } else {
            setHideShell(false)
        }
    }, [location.pathname]);

    function renderHeader() {
        if (isMobile) {
            return (
                <Group h={"100%"} gap={0} grow justify="space-between">
                    <Burger
                        style={{
                            flexGrow: 0,
                        }}
                        opened={opened}
                        onClick={toggle}
                        hiddenFrom="sm"
                        size="sm"
                    />
                    {
                        opened ? <IconBar toggleNav={toggle}/> : header
                    }
                </Group>
            );
        } else {
            return (
                <Group gap={0}>
                    <Group w={400 - 16} h={"100%"} gap={0} grow justify="space-between">
                        <IconBar toggleNav={toggle}/>
                    </Group>
                    <Divider orientation={"vertical"}></Divider>
                    <Container>
                        <Center>
                            {header}
                        </Center>
                    </Container>
                </Group>
            );
        }
    }

    return (
        !hideShell ?
            <AppShell
                header={{height: 90}}
                navbar={{width: 400, breakpoint: 'sm', collapsed: {mobile: !opened}}}
                zIndex={2000}
                padding={0}
            >
                {
                    <AppShell.Header p={"md"}>
                        {renderHeader()}
                    </AppShell.Header>
                }

                {
                    <AppShell.Navbar withBorder>
                        <AppShell.Section grow component={ScrollArea}>
                            <ChatsList toggleNav={toggle}/>
                        </AppShell.Section>
                        <Divider size={1}/>
                        <AppShell.Section h={90} pl={"md"} pr={"md"}>
                            <AccountBtn toggleNav={toggle}/>
                        </AppShell.Section>
                    </AppShell.Navbar>
                }

                <AppShell.Main>
                    <Outlet context={outletContext}/>
                </AppShell.Main>
            </AppShell> :
            <Outlet context={outletContext}/>
    );
}

export default Shell;
