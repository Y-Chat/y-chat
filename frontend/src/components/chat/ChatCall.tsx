import {ActionIcon, Flex, Group} from "@mantine/core";
import React, {useEffect, useMemo, useState} from "react";
import {IconCameraRotate, IconMicrophone, IconMicrophoneOff, IconPhoneOff} from "@tabler/icons-react";
import {useCallingStore} from "../../state/callingStore";

export default function ChatCall() {
    const [microphoneOn, setMicrophoneOn] = useState(true);

    const endCall = useCallingStore((state) => state.endCall);
    const signaling = useCallingStore((state) => state.signaling);
    const setMicState = useCallingStore((state) => state.setMicState);
    const switchCamera = useCallingStore((state) => state.switchCamera);
    const audio = useMemo(() => new Audio("/dialing.mp3"), []);

    useEffect(() => {
        let timeout: NodeJS.Timeout | undefined = undefined;
        if (signaling?.callState === "PENDING") {
            timeout = setTimeout(() => {
                audio.volume = 0.1
                audio.loop = true;
                audio.play().catch(() => {
                    console.error("can't start notification audio autoplay, because it's being blocked by the browser")
                })
            }, 3500)
        } else {
            audio.pause()
            clearTimeout(timeout)
        }

        return () => {
            audio.pause()
            clearTimeout(timeout)
        }
    }, [audio, signaling]);

    useEffect(() => {
        const localStream = signaling?.localStream;
        const remoteStream = signaling?.remoteStream;
        const webcamVideo = document.getElementById("webcamVideo") as HTMLVideoElement | null;
        const remoteVideo = document.getElementById("remoteVideo") as HTMLVideoElement | null;
        if (localStream && webcamVideo) {
            try {
                webcamVideo.srcObject = localStream;
            } catch (err) {
                console.error(err)
            }
        }
        if (remoteStream && remoteVideo) {
            try {
                remoteVideo.srcObject = remoteStream;
            } catch (err) {
                console.error(err)
            }
        }
    }, []);

    useEffect(() => {
        setMicState(microphoneOn)
    }, [setMicState, microphoneOn]);

    return (
        <div style={{width: "100vw", height: "100%", position: "fixed"}}>
            <div style={{position: "absolute", width: "100vw", height: "100%"}}>
                <Flex style={{height: "100vh", width: "100vw"}} justify={"center"}>
                    <video id={"remoteVideo"} autoPlay playsInline style={{height: "100%", maxWidth: "100vw"}}/>
                </Flex>
            </div>
            <div style={{position: "absolute", bottom: "5%", width: "100%"}}>
                <Group justify={"center"} gap={"2%"}>
                    <ActionIcon
                        size={"xl"}
                        radius={"xl"}
                        variant={"light"}
                        color="dimmed"
                        onClick={() => {
                            switchCamera();
                        }}
                    >
                        <IconCameraRotate stroke={1.5}/>
                    </ActionIcon>
                    <ActionIcon
                        size={"xl"}
                        radius={"xl"}
                        variant={"light"}
                        color="dimmed"
                        onClick={() => {
                            endCall();
                        }}
                    >
                        <IconPhoneOff stroke={1.5}/>
                    </ActionIcon>
                    <ActionIcon
                        size={"xl"}
                        radius={"xl"}
                        variant={"light"}
                        color="dimmed"
                        onClick={() => {
                            setMicrophoneOn((prev) => !prev)
                        }}
                    >
                        {microphoneOn ?
                            <IconMicrophone stroke={1.5}/>
                            :
                            <IconMicrophoneOff stroke={1.5}/>
                        }

                    </ActionIcon>
                </Group>
            </div>
            <div style={{position: "absolute", top: "3vh", right: "3vh"}}>
                <video id={"webcamVideo"} autoPlay playsInline
                       onLoadStart={(x) => x.currentTarget.volume = 0}
                       style={{maxHeight: "20vh", maxWidth: "40vw"}}/>
            </div>
        </div>
    )
}
