import { api } from '../network/api'
import { showNotification } from "../notifications/notifications";
import { MessagePayload } from "firebase/messaging"

const servers = {
    iceServers: [
        {
            urls: ['stun:stun1.l.google.com:19302', 'stun:stun2.l.google.com:19302', "stun:relay.metered.ca:80", "stun:stun.relay.metered.ca:80"],
        },
        {
            urls: "turn:standard.relay.metered.ca:80",
            username: "55044823855cd2e340b8b092",
            credential: "ToVgRRuaYXWtXoqd",
        },
        {
            urls: "turn:standard.relay.metered.ca:80?transport=tcp",
            username: "55044823855cd2e340b8b092",
            credential: "ToVgRRuaYXWtXoqd",
        },
        {
            urls: "turn:standard.relay.metered.ca:443",
            username: "55044823855cd2e340b8b092",
            credential: "ToVgRRuaYXWtXoqd",
        },
        {
            urls: "turns:standard.relay.metered.ca:443?transport=tcp",
            username: "55044823855cd2e340b8b092",
            credential: "ToVgRRuaYXWtXoqd",
        },
    ],
    iceCandidatePoolSize: 10,
};

interface CallSignalingProps {
    acceptCall?: string,
    callUser?: string
}

type CallState = "STARTING" | "PENDING" | "ENDED" | "ONGOING" | "DENIED";

export class CallSignaling {
    peerConnection = new RTCPeerConnection(servers);
    localStream: MediaStream;
    remoteStream: MediaStream;
    webcamVideo: HTMLVideoElement | null;
    remoteVideo: HTMLVideoElement | null;
    acceptCallId: string | undefined;
    callUserId: string | undefined;
    callState: CallState;

    constructor({ acceptCall, callUser }: CallSignalingProps) {
        this.peerConnection = new RTCPeerConnection(servers);
        this.remoteStream = new MediaStream();
        this.localStream = new MediaStream();
        this.webcamVideo = document.getElementById("webcamVideo") as HTMLVideoElement | null;
        this.remoteVideo = null;
        this.acceptCallId = acceptCall;
        this.callUserId = callUser;
        this.callState = "STARTING";
        this.setOwnMedia(true)
    }

    setOwnMedia(withAudio: boolean) {
        this.webcamVideo = document.getElementById("webcamVideo") as HTMLVideoElement | null;
        navigator.mediaDevices.getUserMedia({ video: true, audio: withAudio }).then((x) => {
            this.localStream = x;
            console.log("webcamVideo: ", this.webcamVideo)
            if(this.webcamVideo) {
                this.webcamVideo.srcObject = this.localStream;
            }
        }).catch((err) => {
            console.error(err)
        })
    }

    async createCall() {
        if(this.callUserId === undefined || this.callState != "STARTING") return;
        this.callState = "PENDING";

        this.localStream.getTracks().forEach((track) => {
            this.peerConnection.addTrack(track, this.localStream);
        });

        this.peerConnection.ontrack = (event) => {
            event.streams[0].getTracks().forEach((track) => {
                this.remoteStream.addTrack(track);
            });
        };

        const offerDescription = await this.peerConnection.createOffer();
        await this.peerConnection.setLocalDescription(offerDescription);

        const offer = {
            sdp: offerDescription.sdp,
            type: offerDescription.type,
        };

        await api.createCall({createCallRequest: {
            calleeId: this.callUserId,
            offer: offer
        }}).catch((err) => console.error(err));
    }

    handleNotifications(payload: MessagePayload) {
        console.log("handle signaling notifications", payload)
    }

    async acceptCall() {
        if(this.acceptCallId === undefined) return;

        this.localStream.getTracks().forEach((track) => {
            this.peerConnection.addTrack(track, this.localStream);
        });

        this.peerConnection.ontrack = (event) => {
            event.streams[0].getTracks().forEach((track) => {
                this.remoteStream.addTrack(track);
            });
        };

        var answerDescription = await this.peerConnection.createAnswer();
        var answer = {
            type: answerDescription.type,
            sdp: answerDescription.sdp
        }
    }

    async denyCall() {

    }

    async endCall() {

    }
}
