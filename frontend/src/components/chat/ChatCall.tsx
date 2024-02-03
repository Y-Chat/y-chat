import {Flex, Group, Stack} from "@mantine/core";
import React, {useEffect, useMemo, useState} from "react";
import {
    IconCameraRotate,
    IconMicrophone,
    IconMicrophoneOff,
    IconPhoneOff
} from "@tabler/icons-react";
import {useOutletContext} from "react-router-dom";
import {ShellOutletContext} from "../shell/ShellOutletContext";
import {useCallingStore} from "../../state/callingStore";

export default function ChatCall() {
    const [microphoneOn, setMicrophoneOn] = useState(true);
    const { setCollapseHeader } = useOutletContext<ShellOutletContext>();

    const endCall = useCallingStore((state) => state.endCall);
    const signaling = useCallingStore((state) => state.signaling);
    const setMicState = useCallingStore((state) => state.setMicState);
    const switchCamera = useCallingStore((state) => state.switchCamera);

    useEffect(() => {
        setCollapseHeader(true)
        return () => {
            setCollapseHeader(false)
        }
    }, []);

    useEffect(() => {
        const localStream = signaling?.localStream;
        const remoteStream = signaling?.remoteStream;
        const webcamVideo = document.getElementById("webcamVideo") as HTMLVideoElement | null;
        const remoteVideo = document.getElementById("remoteVideo") as HTMLVideoElement | null;
        if(localStream && webcamVideo) {
            try {
                webcamVideo.srcObject = localStream;
            }
            catch (err){
                console.error(err)
            }
        }
        if(remoteStream && remoteVideo) {
            try {
                remoteVideo.srcObject = remoteStream;
            }
            catch (err) {
                console.error(err)
            }
        }
    }, []);

    useEffect(() => {
        setMicState(microphoneOn)
    }, [setMicState, microphoneOn]);

    return (
        <div style={{width: "100vw", height: "100%"}}>
            <div style={{position: "absolute", width: "100vw", height: "100%"}}>
                <Flex style={{height: "100vh", width: "100vw"}} justify={"center"}>
                    <video id={"remoteVideo"} autoPlay playsInline style={{height: "100%", maxWidth: "100vw"}}/>
                </Flex>
            </div>
            <div style={{position: "absolute", bottom: "5%", width: "100%"}}>
                <Group justify={"center"} gap={"2%"}>
                    <IconCameraRotate
                        size={"9vh"}
                        color={"black"}
                        style={{
                            backgroundColor: "white",
                            padding: "10px",
                            borderRadius: 100
                        }}
                        onClick={() => {
                            switchCamera()
                        }}
                    />
                    <IconPhoneOff
                        size={"9vh"}
                        color={"white"}
                        style={{
                            backgroundColor: "red",
                            padding: "8px",
                            borderRadius: 100
                        }}
                        onClick={() => {
                            endCall().then((x) => {

                            })
                        }}
                    />
                    {microphoneOn ? <IconMicrophoneOff
                        size={"9vh"}
                        color={"black"}
                        style={{
                            backgroundColor: "white",
                            padding: "10px",
                            borderRadius: 100
                        }}
                        onClick={() => {
                            setMicrophoneOn((prev) => !prev)
                        }}
                    /> : <IconMicrophone
                        size={"9vh"}
                        color={"black"}
                        style={{
                            backgroundColor: "white",
                            padding: "10px",
                            borderRadius: 100
                        }}
                        onClick={() => {
                            setMicrophoneOn((prev) => !prev)
                        }}
                    />}
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
