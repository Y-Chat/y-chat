import React from "react";

export type ShellOutletContext  =
    {
        setHeader: React.Dispatch<React.SetStateAction<JSX.Element>>,
        setHideShell: React.Dispatch<React.SetStateAction<boolean>>
    }
