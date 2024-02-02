import {Flex, Group, Stack} from "@mantine/core";
import React, {useEffect, useMemo, useState} from "react";
import {
    IconCameraRotate,
    IconMicrophone,
    IconMicrophoneOff,
    IconPhoneOff
} from "@tabler/icons-react";
import {useNavigate, useOutletContext, useSearchParams} from "react-router-dom";
import {ShellOutletContext} from "../shell/ShellOutletContext";
import {useCallingStore} from "../../state/callingStore";

export default function ChatCall() {
    const [searchParams, setSearchParams] = useSearchParams();
    const navigate = useNavigate();
    const [microphoneOn, setMicrophoneOn] = useState(true);
    const { setCollapseHeader } = useOutletContext<ShellOutletContext>();

    const endCall = useCallingStore((state) => state.endCall);
    const startCall = useCallingStore((state) => state.startCall);
    const setOwnWebcamStream = useCallingStore((state) => state.setOwnWebcamStream);

    useEffect(() => {
        setOwnWebcamStream(microphoneOn)
    }, [microphoneOn])

    useEffect(() => {
        setCollapseHeader(true)
        return () => {
            setCollapseHeader(false)
        }
    }, []);

    return (
        <div style={{width: "100vw", height: "100%"}}>
            <div style={{position: "absolute", width: "100vw", height: "100%"}}>
                <Flex style={{height: "100vh", width: "100vw"}} justify={"center"}>
                    {/*<img src={"/call-selfie-placeholder.jpg"} style={{height: "100%", maxWidth: "100vw"}}/>*/}
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
                <video id={"webcamVideo"} autoPlay playsInline style={{maxHeight: "20vh", maxWidth: "40vw"}}/>
            </div>
        </div>
    )
}
