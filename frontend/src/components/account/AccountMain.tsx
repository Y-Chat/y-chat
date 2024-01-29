import React, {useEffect, useState} from "react";
import {
    Avatar,
    Center,
    Text,
    Divider,
    Group,
    Stack,
    SimpleGrid,
    Card,
    Input,
    Container,
    Button,
    rem,
    ColorPicker
} from "@mantine/core";
import {useForm} from "@mantine/form";
import {TextInput} from "@mantine/core";
import {useUserStore} from "../../state/userStore";
import MenuDrawer from "../menu/MenuDrawer";
import {IconLogout, IconUpload, IconX} from "@tabler/icons-react";
import {Dropzone, IMAGE_MIME_TYPE} from "@mantine/dropzone";
import {signOut} from "firebase/auth";
import auth from "../../firebase/auth";
import {getImageUrl, uploadImage} from "../../network/media";
import {api} from "../../network/api";
import {useNavigate} from "react-router-dom";

export function AccountMain() {
    const [uploadingAvatar, setUploadingAvatar] = useState(false);
    const [accentColor, setAccentColor] = useState('#fff');
    const [logoutLoading, setLogoutLoading] = useState(false);
    const sizeHeader = 10;
    const user = useUserStore((state) => state.user)!; // this view can only be rendered if user is not null!
    const setUser = useUserStore((state) => state.setUser);
    const [userProfilePictureURL, setUserProfilePictureURL] = useState<string | null>(null);
    const navigate = useNavigate()
    const form = useForm({
        initialValues: {
            email: user?.email,
            firstName: user?.firstName,
            lastName: user?.lastName,
        },

        validate: {
            // TODO
            //email: (val) => (/^\S+@\S+$/.test(val) ? null : 'Invalid email'),
            //password: (val) => (val.length <= 6 ? 'Password should include at least 6 characters' : null),
        },
    });

    useEffect(() => {
        if (user.profilePictureId) {
            getImageUrl(user.profilePictureId).then(url => {
                setUserProfilePictureURL(url);
            })
        }
    }, [user.profilePictureId])

    return (
        <>
            <header>
                <div style={{
                    height: `${sizeHeader}vh`,
                    width: "100%",
                    zIndex: 1,
                }}>
                    <Group justify="space-between" pl={10} h={"100%"} pr={10}>
                        <MenuDrawer/>
                        <Text fz="xl" fw={500}>Account Settings</Text>
                        <span/>
                    </Group>
                    <Divider/>
                </div>
            </header>

            <Container p='md'>
                <form onSubmit={form.onSubmit(() => {
                })}>
                    <Stack justify="flex-start" align="stretch">
                        <Center mb={10}>
                            <Dropzone
                                maxFiles={1}
                                multiple={false}
                                pos={"relative"}
                                loading={uploadingAvatar}
                                onDrop={(files) => {
                                    setUploadingAvatar(true);
                                    try {
                                        const uid = auth.currentUser?.uid
                                        if (!uid)
                                            return;

                                        files.forEach((file) => {
                                            uploadImage(file, `profilePictures/${uid}/${file.name}`).then(objectId => {
                                                return api.updateUserProfile({
                                                    userId: user.id,
                                                    userProfileDTO: {
                                                        firstName: user.firstName,
                                                        lastName: user.lastName,
                                                        profilePictureId: objectId
                                                    }
                                                })
                                            }).then(userProfile => {
                                                setUser({
                                                    ...user,
                                                    profilePictureId: userProfile.profilePictureId || null
                                                })
                                            }).catch(err => {
                                                // TODO handle error
                                            });
                                            setUploadingAvatar(false);
                                        })
                                    } catch (e) {
                                        setUploadingAvatar(false);
                                        // TODO handle error
                                    }
                                }}
                                onReject={(files) => console.log('rejected files', files)}
                                accept={IMAGE_MIME_TYPE}
                            >
                                <Group justify="center" gap="xl" style={{pointerEvents: 'none'}}>
                                    <Dropzone.Accept>
                                        <Avatar size={120}>
                                            <IconUpload
                                                style={{
                                                    width: rem(52),
                                                    height: rem(52),
                                                    color: 'var(--mantine-color-blue-6)'
                                                }}
                                                stroke={1.5}
                                            />
                                        </Avatar>
                                    </Dropzone.Accept>
                                    <Dropzone.Reject>
                                        <Avatar size={120}>
                                            <IconX
                                                style={{
                                                    width: rem(52),
                                                    height: rem(52),
                                                    color: 'var(--mantine-color-red-6)'
                                                }}
                                                stroke={1.5}
                                            />
                                        </Avatar>
                                    </Dropzone.Reject>
                                    <Dropzone.Idle>
                                        <Avatar
                                            src={userProfilePictureURL}
                                            size={120} color={accentColor}/>
                                    </Dropzone.Idle>
                                </Group>
                            </Dropzone>

                        </Center>
                        <Group grow>
                            <TextInput
                                size="md"
                                label="First Name"
                                placeholder="Max"
                                value={form.values.firstName}
                                onChange={(event) => form.setFieldValue('firstName', event.currentTarget.value)}
                                radius="md"
                            />
                            <TextInput
                                size="md"
                                label="Last Name"
                                placeholder="Mustermann"
                                value={form.values.lastName}
                                onChange={(event) => form.setFieldValue('lastName', event.currentTarget.value)}
                                radius="md"
                            />
                        </Group>
                        <TextInput
                            size="md"
                            label="Email"
                            placeholder="max@mustermann.com"
                            value={form.values.email}
                            onChange={(event) => form.setFieldValue('email', event.currentTarget.value)}
                            radius="md"
                        />
                        <Divider m='xs'/>
                        <Input.Wrapper
                            size="md"
                            label="Your Balance"
                        >
                            <Center><Text size="md" c="green">{user?.balance}€</Text></Center>
                        </Input.Wrapper>
                        <SimpleGrid cols={3} verticalSpacing="sm">
                            {["+5€", "+10€", "+50€"].map(e =>
                                <Card key={e} withBorder>
                                    <Center>{e}</Center>
                                </Card>
                            )}
                        </SimpleGrid>
                        <Divider m='xs'/>
                        <Input.Wrapper
                            size="md"
                            label="Pick your accent color"
                        >
                            <Center pt={"md"}>
                                <ColorPicker
                                    format="hex"
                                    value={accentColor}
                                    onChange={setAccentColor}
                                    withPicker={false}
                                    fullWidth
                                    swatches={[
                                        "white", "#6BD731", "#0969FF", "#4C5897", "#8931B2", "#F01879", "#C91A25"
                                    ]}
                                />
                            </Center>
                        </Input.Wrapper>
                        <Divider m='xs'/>
                        <Button
                            loading={logoutLoading}
                            rightSection={<IconLogout size={14}/>}
                            variant="default"
                            onClick={() => {
                                setLogoutLoading(true)
                                signOut(auth).then(() => {
                                    setLogoutLoading(false);
                                    setUser(null);
                                    navigate("/");
                                }).catch(() => {
                                    setLogoutLoading(false)
                                    // TODO maybe handle error
                                })
                            }}
                        >
                            <Text>Logout</Text>
                        </Button>
                    </Stack>
                </form>
            </Container>
        </>

    )
        ;
}