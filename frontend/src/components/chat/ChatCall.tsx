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
import {useNavigate, useSearchParams} from "react-router-dom";
import {registerNotificationTypeHandler, unregisterNotificationTypeHandler} from "../../firebase/messaging";

export default function ChatCall() {
    const [searchParams, setSearchParams] = useSearchParams();
    const navigate = useNavigate();
    const callSignaling = useMemo(() => {
        return new CallSignaling({
            callUser: searchParams.get("callUser") ?? undefined,
            acceptCall: searchParams.get("acceptCall") ?? undefined,
        });
    }, [])
    const [microphoneOn, setMicrophoneOn] = useState(true);

    useEffect(() => {
        if(!searchParams.has("acceptCall") && !searchParams.has("callUser")) {
            callSignaling.endCall();
            navigate("/")
        }
    }, [searchParams]);

    useEffect(() => {
        callSignaling.setOwnMedia(microphoneOn)
    }, [microphoneOn])

    useEffect( () => {
        const messaging = getMessaging(firebaseApp);
        registerNotificationTypeHandler(["SIGNALING_NEW_ANSWER", "SIGNALING_NEW_CANDIDATE", "CALL_ENDED"], callSignaling.handleNotifications)

        console.log("helper1")
        callSignaling.createCall();

        return () => {
            unregisterNotificationTypeHandler(["SIGNALING_NEW_ANSWER", "SIGNALING_NEW_CANDIDATE", "CALL_ENDED"])
        };
    }, [])

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
