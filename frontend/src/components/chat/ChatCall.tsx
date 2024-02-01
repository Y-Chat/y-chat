import {Flex, Group, Stack} from "@mantine/core";
import React, {useEffect, useMemo, useState} from "react";
import {
    IconCameraRotate,
    IconMicrophone,
    IconMicrophoneOff,
    IconPhoneOff
} from "@tabler/icons-react";
import {CallSignaling} from "../../calling/signaling";
import {getMessaging, onMessage} from "firebase/messaging";
import firebaseApp from "../../firebase/firebaseApp";
import {useNavigate, useOutletContext, useSearchParams} from "react-router-dom";
import {registerNotificationTypeHandler, unregisterNotificationTypeHandler} from "../../firebase/messaging";
import {ShellOutletContext} from "../shell/ShellOutletContext";
import {api} from "../../network/api";
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

    /*useEffect( () => {
        const messaging = getMessaging(firebaseApp);
        registerNotificationTypeHandler(["SIGNALING_NEW_ANSWER", "SIGNALING_NEW_CANDIDATE", "CALL_ENDED"], handleNotifications)

        return () => {
            unregisterNotificationTypeHandler(["SIGNALING_NEW_ANSWER", "SIGNALING_NEW_CANDIDATE", "CALL_ENDED"])
        };
    }, [])*/

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
