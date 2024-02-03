import React, {useState} from "react";
import {Outlet} from "react-router-dom";
import {AppShell, Burger, Container, Divider, em, Group, ScrollArea} from "@mantine/core";
import {useDisclosure, useMediaQuery} from "@mantine/hooks";
import {ShellOutletContext} from "./ShellOutletContext";
import AccountBtn from "./AccountBtn";
import {ContactList} from "./ContactList";
import {IconBar} from "./IconBar";


function Shell() {
    const [opened, {toggle}] = useDisclosure();
    const [header, setHeader] = useState(<></>);
    const [collapseHeader, setCollapseHeader] = useState(false);
    const isMobile = useMediaQuery(`(max-width: ${em(770)})`);

    // this just exists to guarantee type safety for ShellOutletContext
    const outletContext: ShellOutletContext = {
        setHeader,
        setCollapseHeader
    }

    function renderHeader(){
    }

    return (
        <AppShell
            header={{ height: 90 }}
            navbar={{ width: 400, breakpoint: 'sm', collapsed: { mobile: !opened } }}
            zIndex={2000}
            padding={0}
        >
            {!collapseHeader && <AppShell.Header p={"md"}>
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
            </AppShell.Header>}

            {!collapseHeader &&
                <AppShell.Navbar withBorder>
                    <AppShell.Section grow pl={"md"} pr={"md"} component={ScrollArea}>
                        <ContactList toggleNav={toggle}/>
                    </AppShell.Section>
                    <Divider size={1}/>
                    <Container h={90} p={"md"} m={0}>
                        <AccountBtn toggleNav={toggle}/>
                    </Container>
                </AppShell.Navbar>}

            <AppShell.Main>
                <Outlet context={outletContext}/>
            </AppShell.Main>
        </AppShell>
    );
}

export default Shell;
