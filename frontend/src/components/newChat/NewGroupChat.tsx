import React, {useEffect, useState} from "react";
import {Avatar, Button, Center, Container, Group, rem, Stack, TagsInput, Text, TextInput} from "@mantine/core";
import {useForm} from "@mantine/form";
import {Dropzone, FileWithPath, IMAGE_MIME_TYPE} from "@mantine/dropzone";
import {IconPlus, IconUpload, IconX} from "@tabler/icons-react";
import {api} from "../../network/api";
import {useUserStore} from "../../state/userStore";
import {useChatsStore} from "../../state/chatsStore";
import {uploadImage} from "../../network/media";
import {useNavigate, useOutletContext} from "react-router-dom";
import {ShellOutletContext} from "../shell/ShellOutletContext";

export function NewGroupChat() {
    const [searchValue, setSearchValue] = useState('');
    const [formLoading, setFormLoading] = useState(false);
    const [avatarPreview, setAvatarPreview] = useState<{ file: FileWithPath | null, previewUrl: string }>({
        file: null,
        previewUrl: ""
    });
    const navigate = useNavigate();
    const user = useUserStore(state => state.user)!;
    const fetchChats = useChatsStore(state => state.fetchChats);
    const {setHeader} = useOutletContext<ShellOutletContext>();
    const form = useForm<{ groupName: string, groupDescription: string, groupMembers: string[], avatar: string }>({
        initialValues: {
            groupName: "",
            avatar: "",
            groupDescription: "",
            groupMembers: []
        },

        validate: {
            groupMembers: (val) => {
                if (val.length <= 0) {
                    return null;
                }
                return val.every(memberMail => /^\S+@\S+$/.test(memberMail)) ? null : 'You can only enter valid email addresses.'
            }
            ,
        },
    });

    function checkAndSetGroupMembers(gm: string[]) {
        const newGms = gm.filter(member => !form.values.groupMembers.includes(member));
        newGms.map(newGm => {
        });
    }

    useEffect(() => {
        setHeader(
            <Center>
                <Text size={"lg"}>Create a new group</Text>
            </Center>
        );
    }, []);

    return (
        <Container p="md">
            <Stack justify="flex-start" align="stretch">
            </Stack>
            <form onSubmit={form.onSubmit(async () => {
                setFormLoading(true);
                try {
                    const group = await api.createGroup({
                        userId: user.id,
                        groupProfileDTO: {
                            groupName: form.values.groupName,
                            profileDescription: form.values.groupDescription
                        }
                    });

                    if (avatarPreview.file) {
                        const objectId = await uploadImage(avatarPreview.file, `chats/${group.id}/${avatarPreview.file.name}`);
                        await api.updateGroupProfile({
                            groupId: group.id,
                            groupProfileDTO: {
                                profilePictureId: objectId,
                                groupName: form.values.groupName,
                                profileDescription: form.values.groupDescription
                            }
                        });
                    }
                    // TODO add group members api.addGroupMember()
                    await fetchChats();
                    navigate(`/chat/${group.id}`);
                    form.reset();
                    setAvatarPreview({file: null, previewUrl: ""})
                } catch (err) {
                    // TODO handle error
                    setFormLoading(false);
                }
                setFormLoading(false);
            })}>
                <Stack>
                    <Dropzone
                        maxFiles={1}
                        multiple={false}
                        pos={"relative"}
                        onDrop={(files) => {
                            files.forEach(file => {
                                setAvatarPreview({file: file, previewUrl: URL.createObjectURL(file)});
                            })
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
                                <Avatar src={avatarPreview.previewUrl} size={120}/>
                            </Dropzone.Idle>
                        </Group>
                    </Dropzone>
                    <TextInput
                        required
                        disabled={formLoading}
                        size="md"
                        label="Group Name"
                        placeholder="Enter the name of your group"
                        {...form.getInputProps('groupName')}
                    />
                    <TextInput
                        disabled={formLoading}
                        size="md"
                        label="Group Description"
                        placeholder="Give your group a description"
                        {...form.getInputProps('groupDescription')}
                    />
                    <TagsInput
                        disabled={formLoading}
                        size="md"
                        clearable
                        searchValue={searchValue}
                        onSearchChange={setSearchValue}
                        label="Group Members"
                        placeholder="Email of users you would like to add..."
                        data={searchValue ? [...form.values.groupMembers, searchValue] : form.values.groupMembers}
                        {...form.getInputProps('groupMembers')}
                    />
                    <Button type="submit" loading={formLoading} leftSection={<IconPlus/>}
                            mt="md">Create</Button>
                </Stack>
            </form>
        </Container>
    );
}
