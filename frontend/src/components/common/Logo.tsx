import React from "react";
import {useMantineTheme} from "@mantine/core";

function Logo(props: React.ComponentPropsWithoutRef<'svg'>) {
    const theme = useMantineTheme();

    return (
        <svg
            {...props}
            xmlns="http://www.w3.org/2000/svg"
            viewBox="0 0 200 200"
            width="100%"
            height="100%"
        >
            <g>
                <path
                    fill={theme.colors["mainColors"][6]}
                    d="M125.84 197.554l.003-74.853L197.669 2.927l-20.01.014-58.8 98.648L58.953 2.927 2.117 2.919l71.85 119.533-.038 75.131z"
                ></path>
                <path d="M25.994 16.008l25.98.065 59.945 99.401.05 68.957-23.912.116-.1-64.228z"></path>
            </g>
        </svg>
    );
}

export default Logo;
