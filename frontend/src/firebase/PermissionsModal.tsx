import React, {useState} from "react";
import {Button, Center, Container, Group, Modal, Text} from "@mantine/core";
import {IconExclamationCircle} from "@tabler/icons-react";
import {hasPermission, requestNotificationPermissions} from "./firebaseMessaging";

export function PermissionsModal() {
    const [showPermissions, setShowPermissions] = useState<boolean>(!hasPermission)
    const [loadingPermissions, setLoadingPermissions] = useState<boolean>(false)

    return (
        <Modal
            overlayProps={{backgroundOpacity: 0.5, blur: 4}}
            centered
            closeOnEscape={false}
            closeOnClickOutside={false}
            withCloseButton={false}
            title={<Group><IconExclamationCircle/><Text>Permission Required</Text></Group>}
            opened={showPermissions}
            onClose={() => {
            }}>
            <Center>
                <Container>
                    <Text>This application requires permission to send you notifications in order to
                        work.</Text>
                    <Center>
                        <Button mt="md"
                                loading={loadingPermissions}
                                onClick={async () => {
                                    setLoadingPermissions(true)
                                    const success = await requestNotificationPermissions();
                                    setLoadingPermissions(false)
                                    setShowPermissions(!success);
                                }}
                        >
                            Grant Permission
                        </Button>
                    </Center>
                </Container>

            </Center>
        </Modal>
    );

}
