import React from "react";
import {
    Burger,
    Drawer,
    ScrollArea,
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
                    <AccountBtn toggleNav={close}/>
                </Drawer.Header>
                <ScrollArea type="scroll" scrollbarSize={2} scrollHideDelay={500}>
                    <ContactList toggleNav={close}/>
                </ScrollArea>
            </Drawer>
        </>
    );
}

export default MenuDrawer;