import React, {useState} from "react";
import {ActionIcon} from "@mantine/core";
import {IconUserPlus} from "@tabler/icons-react";
import {api} from "../../network/api";
import {useUserStore} from "../../state/userStore";
import {useChatsStore} from "../../state/chatsStore";
import {showErrorNotification} from "../../notifications/notifications";

interface NewDirectChatProps {
    email: string
}

export function NewDirectChat({email}: NewDirectChatProps) {
    const [isLoading, setIsLoading] = useState(false)
    const isValidEmail = /^\S+@\S+$/.test(email) || true // TODO enable valid mail check again if social service endpoint ist fixed.
    const user = useUserStore(state => state.user)!
    const fetchChats = useChatsStore(state => state.fetchChats)
    return (
        <ActionIcon
            loading={isLoading}
            onClick={async () => {
                setIsLoading(true);
                try {
                    const chat = await api.createDirectChat({userId: user.id, otherUserId: email}) // TODO API must accept email instead of uid!
                    await fetchChats();
                } catch (err) {
                    // TODO handle err
                    showErrorNotification("It seems like the email you entered is not registered in our system.","User not found"); // ignore other errors for now
                    setIsLoading(false);
                }
                setIsLoading(false);
            }}
            variant="transparent"
            //disabled={!isValidEmail}
        >
            <IconUserPlus/>
        </ActionIcon>
    );
}