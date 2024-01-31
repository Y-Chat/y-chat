import React, {useState} from "react";
import {Outlet} from "react-router-dom";
import {AppShell, Burger, Container, Divider, Group, ScrollArea} from "@mantine/core";
import {useDisclosure} from "@mantine/hooks";
import {ShellOutletContext} from "./ShellOutletContext";
import AccountBtn from "./AccountBtn";
import {ContactList} from "./ContactList";
import {IconBar} from "./IconBar";


function Shell() {
    const [opened, {toggle}] = useDisclosure();
    const [header, setHeader] = useState(<></>);
    const [collapseHeader, setCollapseHeader] = useState(false);

    // this just exists to guarantee type safety for ShellOutletContext
    const outletContext: ShellOutletContext = {
        setHeader,
        setCollapseHeader
    }

    return (
        <AppShell
            zIndex={2000}
            header={{height: 90, collapsed: collapseHeader}}
            navbar={{
                width: 300,
                breakpoint: 'xl',
                collapsed: {mobile: !opened},
            }}
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

            {!collapseHeader && <AppShell.Navbar withBorder={false}>
                <ScrollArea style={{flex: 1}} pl={"md"} pr={"md"}>
                    <ContactList toggleNav={toggle}/>
                </ScrollArea>
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
