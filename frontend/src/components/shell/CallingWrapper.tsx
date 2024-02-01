import {useCallingStore} from "../../state/callingStore";
import {Outlet, useNavigate} from "react-router-dom";
import React, {useEffect} from "react";

export default function CallingWrapper() {
    const callSignaling = useCallingStore((state) => state.signaling);
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

    return (<Outlet/>)
}
