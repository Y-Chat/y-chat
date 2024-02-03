import React from "react";

export type ShellOutletContext  =
    {
        setHeader: React.Dispatch<React.SetStateAction<JSX.Element>>,
        setCollapseHeader: React.Dispatch<React.SetStateAction<boolean>>
    }
