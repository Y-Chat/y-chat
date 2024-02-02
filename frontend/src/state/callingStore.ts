import {create} from "zustand";
import {api} from "../network/api";
import {Call} from "../api-wrapper";
import {Notifications} from "@mantine/notifications";
import {MessagePayload} from "firebase/messaging";
import {callIdToCallNotificationId} from "../notifications/notifications";

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
    callId: string;
    callState: CallStatus;
}

interface CallingState {
    signaling: CallState | null,
    setOwnWebcamStream: (withAudio: boolean) => void,
    startCall: (calleeId: string) => Promise<void>,
    acceptCall: (callId: string, offerSdp: string, offerType: string) => Promise<void>,
    denyCall: (callId: string) => Promise<void>,
    endCall: () => Promise<void>,
    handleNotifications: (payload: MessagePayload) => void
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

        // TODO Change to chatId instead of calleeId (blocked by social service)
        const call = await api.createCall({createCallRequest: {
            calleeId: calleeId,
            offer: offer
        }}).catch((err) => {
            console.error(err)
            return null;
        });

        if(call) {
            set({signaling: {
                callId: call.id,
                callState: callStatus,
                peerConnection: peerConnection,
                localStream: localStream,
                remoteStream: remoteStream,
                webcamVideo: webcamVideo,
                remoteVideo: remoteVideo
            }})
        }
    },
    acceptCall: async (callId: string, offerSdp: string, offerType: string) => {
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

        const offer: RTCSessionDescriptionInit = {
            sdp: offerSdp,
            type: offerType as RTCSdpType
        }

        await peerConnection.setRemoteDescription(new RTCSessionDescription(offer))
        const answerDescription = await peerConnection.createAnswer();
        await peerConnection.setLocalDescription(answerDescription);

        const answer = {
            sdp: answerDescription.sdp,
            type: answerDescription.type,
        };

        api.answerCall({answerCallRequest: {
            callId: callId,
            accept: true,
            answer: answer
        }}).then(() => {
            set({signaling: {
                callId: callId,
                callState: callStatus,
                peerConnection: peerConnection,
                localStream: localStream,
                remoteStream: remoteStream,
                webcamVideo: webcamVideo,
                remoteVideo: remoteVideo
        }})
        }).catch((err) => console.error(err))
    },
    denyCall: async (callId) => {
        const notificationId = callIdToCallNotificationId(callId)

        api.answerCall({answerCallRequest: {callId: callId, accept: false}}).catch((err) => console.error(err))
        Notifications.hide(notificationId)
        set({signaling: null})
    },
    endCall: async () => {
        const callId = get().signaling?.callId;
        if(!callId) return;
        api.endCall({endCallRequest: {callId: callId}}).catch((err) => console.error(err))
        set({signaling: null})
    },
    handleNotifications: (payload) => {
        if(!payload.data || !("type" in payload.data)) return;
        const type = payload.data["type"]

        if(type === "SIGNALING_NEW_ANSWER") {
            const callId = payload.data["call-id"];
            const calleeId = payload.data["callee-id"];
            const answerSdp = payload.data["answer-sdp"];
            const answerType = payload.data["answer-type"];
            console.log("Got new SIGNALING_NEW_ANSWER")

            if(!get().signaling) return;
            // TODO
        } else if(type === "SIGNALING_NEW_CANDIDATE") {
            const callId = payload.data["call-id"];
            const candidate = payload.data["candidate-candidate"];
            const sdpMid = payload.data["candidate-sdp-mid"];
            const usernameFragment = payload.data["candidate-username-fragment"];
            const sdpMLineIndex = payload.data["candidate-sdp-m-line-index"];
            console.log("Got new SIGNALING_NEW_CANDIDATE")

            if(!get().signaling) return;
            // TODO
        } else if(type === "CALL_ENDED") {
            const callId = payload.data["call-id"];
            Notifications.hide(callIdToCallNotificationId(callId))
            if(!get().signaling || get().signaling?.callId !== callId) return;
            get().endCall()
        }
    }
}))
