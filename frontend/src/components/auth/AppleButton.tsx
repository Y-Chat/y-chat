import {Button, ButtonProps} from '@mantine/core';
import {IconBrandApple, IconBrandTwitter} from "@tabler/icons-react";

export function AppleButton(props: ButtonProps & React.ComponentPropsWithoutRef<'button'>) {
    return (
        <Button
            disabled
            radius="xl"
            leftSection={<IconBrandApple style={{width: '1rem', height: '1rem'}} color="#A2AAAD"/>}
            variant="default"
            {...props}
        >Apple</Button>
    );
}