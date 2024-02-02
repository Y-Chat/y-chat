import {useCallingStore} from "../../state/callingStore";
import {Outlet, useNavigate} from "react-router-dom";
import React, {useEffect} from "react";
import {registerNotificationTypeHandler, unregisterNotificationTypeHandler} from "../../firebase/messaging";

export default function CallingWrapper() {
    const callSignaling = useCallingStore((state) => state.signaling);
    const handleNotifications = useCallingStore((state) => state.handleNotifications);
    const navigate = useNavigate()

    useEffect(() => {
        console.log("signaling changed", callSignaling, window.location.pathname)
        if(callSignaling && !window.location.pathname.startsWith("/call")) {
            navigate("/call")
        }
        if(!callSignaling && window.location.pathname.startsWith("/call")) {
            navigate("/")
        }
    }, [callSignaling]);

    useEffect(() => {
        const types = ["SIGNALING_NEW_ANSWER", "SIGNALING_NEW_CANDIDATE", "CALL_ENDED"];
        registerNotificationTypeHandler(types, handleNotifications)

        return () => {
            unregisterNotificationTypeHandler(types);
        }
    }, []);

    return (<Outlet/>)
}
