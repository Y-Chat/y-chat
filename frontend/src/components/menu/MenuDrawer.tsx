import React from "react";
import {
    Burger,
    Container,
    Drawer,
} from "@mantine/core";
import {useDisclosure} from "@mantine/hooks";
import AccountBtn from "./AccountBtn";
import {ContactList} from "../contacts/ContactList";

function MenuDrawer() {
    const [opened, {open, close}] = useDisclosure(false);

    return (
        <>
            <Burger
                opened={opened}
                onClick={open}
                size="sm"/>
            <Drawer
                padding={0}
                opened={opened}
                onClose={close}
                size="80%"
                withCloseButton={false}
                overlayProps={{backgroundOpacity: 0.5, blur: 4}}
            >
                <Drawer.Header>
                    <Container w={"100%"} p={"md"}>
                        <AccountBtn toggleNav={close}/>
                    </Container>

                </Drawer.Header>
                <Container p={"md"}>
                    <ContactList toggleNav={close}/>
                </Container>
            </Drawer>
        </>
    );
}

export default MenuDrawer;