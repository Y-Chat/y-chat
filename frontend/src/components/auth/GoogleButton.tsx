import {Button} from '@mantine/core';
import {deleteUser, GoogleAuthProvider, signInWithPopup} from "firebase/auth";
import auth from "../../firebase/auth";
import {api} from "../../network/api";
import getUuidByString from "uuid-by-string";
import {showErrorNotification, showSuccessNotification} from "../../notifications/notifications";

function GoogleIcon(props: React.ComponentPropsWithoutRef<'svg'>) {
    return (
        <svg
            xmlns="http://www.w3.org/2000/svg"
            preserveAspectRatio="xMidYMid"
            viewBox="0 0 256 262"
            style={{width: '0.9rem', height: '0.9rem'}}
            {...props}
        >
            <path
                fill="#4285F4"
                d="M255.878 133.451c0-10.734-.871-18.567-2.756-26.69H130.55v48.448h71.947c-1.45 12.04-9.283 30.172-26.69 42.356l-.244 1.622 38.755 30.023 2.685.268c24.659-22.774 38.875-56.282 38.875-96.027"
            />
            <path
                fill="#34A853"
                d="M130.55 261.1c35.248 0 64.839-11.605 86.453-31.622l-41.196-31.913c-11.024 7.688-25.82 13.055-45.257 13.055-34.523 0-63.824-22.773-74.269-54.25l-1.531.13-40.298 31.187-.527 1.465C35.393 231.798 79.49 261.1 130.55 261.1"
            />
            <path
                fill="#FBBC05"
                d="M56.281 156.37c-2.756-8.123-4.351-16.827-4.351-25.82 0-8.994 1.595-17.697 4.206-25.82l-.073-1.73L15.26 71.312l-1.335.635C5.077 89.644 0 109.517 0 130.55s5.077 40.905 13.925 58.602l42.356-32.782"
            />
            <path
                fill="#EB4335"
                d="M130.55 50.479c24.514 0 41.05 10.589 50.479 19.438l36.844-35.974C195.245 12.91 165.798 0 130.55 0 79.49 0 35.393 29.301 13.925 71.947l42.211 32.783c10.59-31.477 39.891-54.251 74.414-54.251"
            />
        </svg>
    );
}

interface GoogleButtonProps {
    loadingState: boolean,
    setLoading: (loadingState: boolean) => void,
    login: () => void
}

export function GoogleButton({loadingState, setLoading, login}: GoogleButtonProps) {
    const provider = new GoogleAuthProvider();
    provider.addScope('https://www.googleapis.com/auth/userinfo.profile');

    return (
        <Button
            radius="xl"
            disabled={loadingState}
            onClick={() => {
                signInWithPopup(auth, provider)
                    .then((userCredentials) => {
                        const providerData = userCredentials.user.providerData[0];
                        let firstName = " ";
                        let lastName = " ";

                        if (providerData && providerData.displayName) {
                            const parts = providerData.displayName.split(/\s+/);
                            firstName = parts[0] || " "
                            lastName = parts[1] || " "
                        }
                        api.createUser({
                            userId: getUuidByString(userCredentials.user.uid, 3),
                            userProfileDTO: {
                                firstName: firstName,
                                lastName: lastName,
                            }
                        }).then(user => {
                            showSuccessNotification("Registration successful", "Registration successful");
                            login();
                            setLoading(false);
                        }).catch(err => {
                            if (err.response.status && err.response.status == 409) {
                                // then login because user existed already
                                return login();
                            }
                            if (auth.currentUser) {
                                deleteUser(auth.currentUser).then(r => {
                                    showErrorNotification("Something went wrong during registration. Please try again.", "Registration Unsuccessful");
                                    setLoading(false);
                                }).catch(err => {
                                    // if this fails again, the user has to manually reset the user. Might think of a smarter solution in the future
                                    setLoading(false);
                                });
                            }
                        })

                    }).catch((error) => {
                    showErrorNotification("Something went wrong during registration. Please try again.", "Registration Unsuccessful");
                    setLoading(false);
                });
            }}
            leftSection={<GoogleIcon/>}
            variant="default"
        >Google</Button>
    );
}