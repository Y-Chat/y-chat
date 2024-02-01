import {create} from "zustand";
import {api} from "../network/api";
import {Call} from "../api-wrapper";
import {Notifications} from "@mantine/notifications";

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

type CallStatus = "STARTING" | "PENDING" | "ENDED" | "ONGOING" | "DENIED";

interface CallState {
    peerConnection: RTCPeerConnection;
    localStream: MediaStream;
    remoteStream: MediaStream;
    webcamVideo: HTMLVideoElement | null;
    remoteVideo: HTMLVideoElement | null;
    call: Call;
    callState: CallStatus;
}

interface CallingState {
    signaling: CallState | null,
    setOwnWebcamStream: (withAudio: boolean) => void,
    startCall: (calleeId: string) => Promise<void>,
    acceptCall: (callId: string) => Promise<void>,
    denyCall: (callId: string) => Promise<void>,
    endCall: () => Promise<void>
}

// Is intentionally not persisted
export const useCallingStore = create<CallingState>((set,get) => ({
    signaling: null,
    setOwnWebcamStream: (withAudio: boolean) => {
        const signaling = get().signaling;
        if(signaling === null) return;
        const webcamVideo = document.getElementById("webcamVideo") as HTMLVideoElement | null;
        navigator.mediaDevices.getUserMedia({ video: true, audio: withAudio }).then((x) => {
            const localStream = x;
            console.log("webcamVideo: ", signaling.webcamVideo)
            if(webcamVideo) {
                webcamVideo.srcObject = localStream;
            }
            set({signaling: {...signaling, webcamVideo: webcamVideo, localStream: localStream}})
        }).catch((err) => {
            console.error(err)
        })
    },
    startCall: async (calleeId: string) => {
        const oldSignaling = get().signaling;
        if(oldSignaling){
           await get().endCall()
        }

        const peerConnection = new RTCPeerConnection();
        const localStream = new MediaStream();
        const remoteStream = new MediaStream();
        const webcamVideo = document.getElementById("webcamVideo") as HTMLVideoElement | null;
        const remoteVideo = null;
        const callStatus: CallStatus = "PENDING";

        const offerDescription = await peerConnection.createOffer();
        await peerConnection.setLocalDescription(offerDescription);

        const offer = {
            sdp: offerDescription.sdp,
            type: offerDescription.type,
        };

        const call = await api.createCall({createCallRequest: {
            calleeId: calleeId,
            offer: offer
        }}).catch((err) => {
            console.error(err)
            return null;
        });

        if(call) {
            set({signaling: {
                call: call,
                callState: callStatus,
                peerConnection: peerConnection,
                localStream: localStream,
                remoteStream: remoteStream,
                webcamVideo: webcamVideo,
                remoteVideo: remoteVideo
            }})
        }
    },
    acceptCall: async (callId) => {},
    denyCall: async (callId) => {
        const notificationId = `callNotification-callId-${callId}`

        const signaling = get().signaling;
        if(!signaling) return;
        api.answerCall({answerCallRequest: {callId: callId, accept: false}})
        Notifications.hide(notificationId)
        set({signaling: null})
    },
    endCall: async () => {
        const callId = get().signaling?.call?.id;
        if(!callId) return;
        api.endCall({endCallRequest: {callId: callId}})
        set({signaling: null})
    }
}))
