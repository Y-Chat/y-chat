import React from "react";
import {Avatar, Group, Indicator, Text} from "@mantine/core";

interface ContactListEntryProp {
    avatar: string | null,
    firstName: string,
    lastName: string,
    username: string,
    newMessages: number
}

function ContactListEntry({avatar, firstName, lastName, username, newMessages}: ContactListEntryProp) {

    //TODO date
    const today = new Date();

    return (
        <Group justify="space-between">
            <Group gap="sm">
                <Indicator disabled={!newMessages}>
                    <Avatar size={40} src={avatar} radius={40}/>
                </Indicator>
                <div style={{marginLeft: 5}}>
                    <Text fz="sm" fw={500}>
                        {`${firstName} ${lastName}`}
                    </Text>
                    <Text c="dimmed" fz="xs">
                        {`@${username}`}
                    </Text>
                </div>
            </Group>
            <Text c="dimmed" fz="xs">{`${today.getDate()}.${today.getMonth()}.${today.getFullYear()}`}</Text>
        </Group>
    );
}

export default ContactListEntry;