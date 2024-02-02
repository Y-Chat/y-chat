import {create} from "zustand";
import {api} from "../network/api";
import {Call} from "../api-wrapper";
import {Notifications} from "@mantine/notifications";
import {MessagePayload} from "firebase/messaging";
import {callIdToCallNotificationId} from "../notifications/notifications";
import adapter from "webrtc-adapter"

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

console.log("WebRTC Adapter detected browser: " + adapter.browserDetails.browser);

// Is intentionally not persisted
export const useCallingStore = create<CallingState>((set,get) => ({
    signaling: null,
    setOwnWebcamStream: (withAudio: boolean) => {
        const signaling = get().signaling;
        if(signaling === null) return;
        const webcamVideo = document.getElementById("webcamVideo") as HTMLVideoElement | null;
        navigator.mediaDevices.getUserMedia({ video: true, audio: withAudio }).then((x) => {
            const localStream = x;

            const peerConnection = get().signaling?.peerConnection;

            if(peerConnection) {
                peerConnection.getSenders().forEach(x => peerConnection.removeTrack(x))
                localStream.getTracks().forEach((track) => {
                    peerConnection.addTrack(track, localStream)
                })
            }

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

        const peerConnection = new RTCPeerConnection(servers);
        const localStream = new MediaStream();
        const remoteStream = new MediaStream();
        const webcamVideo = document.getElementById("webcamVideo") as HTMLVideoElement | null;
        const remoteVideo = document.getElementById("remoteVideo") as HTMLVideoElement | null;
        const callStatus: CallStatus = "PENDING";

        localStream.getTracks().forEach((track) => {
            peerConnection.addTrack(track, localStream)
        })

        peerConnection.ontrack = (event) => {
            event.streams[0].getTracks().forEach((track) => {
                remoteStream.addTrack(track);
            })
        }

        peerConnection.onconnectionstatechange = (event) => {
            console.log("onconnectionstatechange", peerConnection.connectionState)
        }
        peerConnection.oniceconnectionstatechange = (event) => {
            console.log("oniceconnectionstatechange", peerConnection.iceConnectionState)
        }
        peerConnection.onsignalingstatechange = (event) => {
            console.log("onsignalingstatechange", peerConnection.signalingState)
        }
        peerConnection.onicegatheringstatechange = (event) => {
            console.log("onicegatheringstatechange", peerConnection.iceGatheringState)
        }

        const offerDescription = await peerConnection.createOffer();
        await peerConnection.setLocalDescription(offerDescription);

        const offer = {
            sdp: offerDescription.sdp,
            type: offerDescription.type,
        };
        console.log("created offer", offer)

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

            peerConnection.onicecandidate = (event) => {
                console.log("onicecandidate", event)
                if(!event.candidate) return;
                api.postNewSignalingCandidate({postNewSignalingCandidateRequest: {
                        callId: call.id,
                        candidate: {
                            candidate: event.candidate.candidate,
                            sdpMid: event.candidate.sdpMid ?? undefined,
                            sdpMLineIndex: event.candidate.sdpMLineIndex ?? undefined,
                            usernameFragment: event.candidate.usernameFragment ?? undefined
                        }
                    }})
            }
        }
    },
    acceptCall: async (callId: string, offerSdp: string, offerType: string) => {
        const oldSignaling = get().signaling;
        if(oldSignaling){
            await get().endCall()
        }

        const peerConnection = new RTCPeerConnection(servers);
        const localStream = new MediaStream();
        const remoteStream = new MediaStream();
        const webcamVideo = document.getElementById("webcamVideo") as HTMLVideoElement | null;
        const remoteVideo = document.getElementById("remoteVideo") as HTMLVideoElement | null;
        const callStatus: CallStatus = "ONGOING";

        localStream.getTracks().forEach((track) => {
            peerConnection.addTrack(track, localStream)
        })

        peerConnection.onconnectionstatechange = (event) => {
            console.log("onconnectionstatechange", peerConnection.connectionState)
        }
        peerConnection.oniceconnectionstatechange = (event) => {
            console.log("oniceconnectionstatechange", peerConnection.iceConnectionState)
        }
        peerConnection.onsignalingstatechange = (event) => {
            console.log("onsignalingstatechange", peerConnection.signalingState)
        }
        peerConnection.onicegatheringstatechange = (event) => {
            console.log("onicegatheringstatechange", peerConnection.iceGatheringState)
        }

        peerConnection.ontrack = (event) => {
            event.streams[0].getTracks().forEach((track) => {
                remoteStream.addTrack(track);
            })
        }

        peerConnection.onicecandidate = (event) => {
            console.log("onicecandidate", event)
            if(!event.candidate) return;
            api.postNewSignalingCandidate({postNewSignalingCandidateRequest: {
                callId: callId,
                candidate: {
                    candidate: event.candidate.candidate,
                    sdpMid: event.candidate.sdpMid ?? undefined,
                    sdpMLineIndex: event.candidate.sdpMLineIndex ?? undefined,
                    usernameFragment: event.candidate.usernameFragment ?? undefined
                }
            }})
        }

        const offer: RTCSessionDescriptionInit = {
            sdp: offerSdp,
            type: offerType as RTCSdpType
        }

        console.log("received offer", offer)

        await peerConnection.setRemoteDescription(new RTCSessionDescription(offer))
        console.log("after setting offer", JSON.stringify(peerConnection.localDescription), JSON.stringify(peerConnection.remoteDescription))
        const answerDescription = await peerConnection.createAnswer();
        console.log("after generating answer", JSON.stringify(peerConnection.localDescription), JSON.stringify(peerConnection.remoteDescription))
        await peerConnection.setLocalDescription(answerDescription);
        console.log("after setting answer", JSON.stringify(peerConnection.localDescription), JSON.stringify(peerConnection.remoteDescription))

        const answer = {
            sdp: answerDescription.sdp,
            type: answerDescription.type,
        };
        console.log("created answer", answer)

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

            const signaling = get().signaling;
            if(!signaling || signaling.callState !== "PENDING") return;

            const answerDescription = new RTCSessionDescription({
                type: answerType as RTCSdpType,
                sdp: answerSdp
            });
            signaling.peerConnection.setRemoteDescription(answerDescription)

            set((state) => ({
                ...state,
                signaling: state.signaling ? {
                    ...state.signaling,
                    callState: "ONGOING"
                } : null,
            }))
        } else if(type === "SIGNALING_NEW_CANDIDATE") {
            const callId = payload.data["call-id"];
            const candidate = payload.data["candidate-candidate"];
            const sdpMid = payload.data["candidate-sdp-mid"];
            const usernameFragment = payload.data["candidate-username-fragment"];
            const sdpMLineIndex = payload.data["candidate-sdp-m-line-index"];
            console.log("Got new SIGNALING_NEW_CANDIDATE")

            const signaling = get().signaling;
            if(!signaling) return;
            const iceCandidate = new RTCIceCandidate({
                candidate: candidate,
                sdpMid: sdpMid,
                sdpMLineIndex: Number.parseInt(sdpMLineIndex),
                usernameFragment: usernameFragment
            })
            signaling.peerConnection.addIceCandidate(iceCandidate)
        } else if(type === "CALL_ENDED") {
            const callId = payload.data["call-id"];
            Notifications.hide(callIdToCallNotificationId(callId))
            if(!get().signaling || get().signaling?.callId !== callId) return;
            get().endCall()
        }
    }
}))
