import {Flex, Group, Stack} from "@mantine/core";
import React, {useEffect, useMemo, useState} from "react";
import {
    IconCameraRotate,
    IconMicrophone,
    IconMicrophoneOff,
    IconPhoneOff
} from "@tabler/icons-react";
import {CallSignaling} from "../../calling/signaling";

export default function ChatCall() {
    const callSignaling = useMemo(() => new CallSignaling(), [])
    const [microphoneOn, setMicrophoneOn] = useState(true);

    useEffect(() => {
        callSignaling.setOwnMedia(microphoneOn)
    }, [microphoneOn])

    return (
        <div>
            <div style={{position: "absolute", width: "100%", height: "100%"}}>
                <Flex style={{height: "100%"}} justify={"center"}>
                    <img src={"/call-selfie-placeholder.jpg"} style={{height: "100%"}}/>
                </Flex>
            </div>
            <div style={{position: "absolute", bottom: "5%", width: "100%"}}>
                <Group justify={"center"} gap={"2%"}>
                    <IconCameraRotate
                        size={"9vh"}
                        color={"black"}
                        style={{
                            backgroundColor: "white",
                            padding: "25px",
                            borderRadius: 100
                        }}
                    />
                    <IconPhoneOff
                        size={"9vh"}
                        color={"white"}
                        style={{
                            backgroundColor: "red",
                            padding: "10px",
                            borderRadius: 100
                        }}
                    />
                    {microphoneOn ? <IconMicrophoneOff
                        size={"9vh"}
                        color={"black"}
                        style={{
                            backgroundColor: "white",
                            padding: "25px",
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
                            padding: "25px",
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
                {/*<img src={"/call-selfie-placeholder-2.jpg"} style={{maxHeight: "20vh", maxWidth: "40vw"}}/>*/}
            </div>
        </div>
    )
}
