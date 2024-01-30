import React from "react";
import {Button, Container, Group, Title, Text} from "@mantine/core";
import classes from './NotFound.module.css';
import {useNavigate} from "react-router-dom";

export function NotFound() {
    const navigate = useNavigate();
    return (
        <Container className={classes.root} mt={"50%"}>
            <div className={classes.label}>404</div>
            <Title className={classes.title}>You have found a secret place.</Title>
            <Text c="dimmed" size="lg" ta="center" className={classes.description}>
                This should not happen.
            </Text>
            <Group justify="center">
                <Button variant="subtle" size="md" onClick={() => navigate("/")}>
                    Take me back
                </Button>
            </Group>
        </Container>
    );
}