import {create} from "zustand";
import {api} from "../network/api";
import {Call} from "../api-wrapper";
import {Notifications} from "@mantine/notifications";
import {MessagePayload} from "firebase/messaging";
import {callIdToCallNotificationId} from "../notifications/notifications";
import adapter from "webrtc-adapter"
import { Notification } from "../firebase/messaging";

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
    localCachedIceCandidates: RTCIceCandidate[],
    remoteCachedIceCandidates: {[callId: string]: RTCIceCandidate[]},
    facingMode: "user" | "environment",
    startCall: (calleeId: string) => Promise<void>,
    acceptCall: (callId: string, offerSdp: string, offerType: string) => Promise<void>,
    denyCall: (callId: string) => Promise<void>,
    endCall: () => Promise<void>,
    switchCamera: () => Promise<void>,
    handleNotifications: (payload: Notification) => void,
    setMicState: (micState: boolean) => void;
}

console.log("WebRTC Adapter detected browser: " + adapter.browserDetails.browser);

// Is intentionally not persisted
export const useCallingStore = create<CallingState>((set,get) => ({
    signaling: null,
    facingMode: "user",
    localCachedIceCandidates: [],
    remoteCachedIceCandidates: {},
    switchCamera: async () => {
        const signaling = get().signaling;
        if(!signaling) return;
        const newFacingMode = get().facingMode === "user" ? "environment" : "user";
        await navigator.mediaDevices.getUserMedia({ video: {facingMode: newFacingMode}, audio: true })
            .then((stream) => {
                const [videoTrack] = stream.getVideoTracks();
                const sender = signaling.peerConnection.getSenders().find((s) => s.track?.kind === videoTrack.kind);
                console.log("Found sender, replacing track", sender)
                sender?.replaceTrack(videoTrack)
            })
            .catch((x) => {
                console.error(x)
            });
    },
    setMicState: (micState) => {
        const signaling = get().signaling;
        if(signaling) {
            if(signaling.localStream.getAudioTracks().length == 0) return;
            const audioTrack = signaling.localStream.getAudioTracks()[0];
            if(!audioTrack) return;
            audioTrack.enabled = micState;
        }
    },
    startCall: async (calleeId: string) => {
        const oldSignaling = get().signaling;
        if(oldSignaling){
           await get().endCall()
        }

        const peerConnection = new RTCPeerConnection(servers);
        const localStream = await navigator.mediaDevices.getUserMedia({ video: {facingMode: get().facingMode}, audio: true })
            .catch((x) => {
                console.error(x)
                return new MediaStream();
            });
        const remoteStream = new MediaStream();
        const webcamVideo = document.getElementById("webcamVideo") as HTMLVideoElement | null;
        const remoteVideo = document.getElementById("remoteVideo") as HTMLVideoElement | null;
        const callStatus: CallStatus = "PENDING";

        if(webcamVideo) {
            try {
                webcamVideo.srcObject = localStream;
            }
            catch (err){
                console.error(err)
            }
        }
        if(remoteVideo) {
            try {
                remoteVideo.srcObject = remoteStream;
            }
            catch (err) {
                console.error(err)
            }
        }

        localStream?.getTracks().forEach((track) => {
            peerConnection.addTrack(track, localStream)
        })

        peerConnection.createDataChannel("callData", { negotiated: true, id: 100, ordered: false });

        peerConnection.ontrack = (event) => {
            console.log("ontrack")
            event.streams[0].getTracks().forEach((track) => {
                try {
                    remoteStream.addTrack(track);
                }
                catch (err) {
                    console.error(err)
                }
            })
        }

        peerConnection.onconnectionstatechange = (event) => {
            console.log("onconnectionstatechange", peerConnection.connectionState)
        }
        peerConnection.oniceconnectionstatechange = (event) => {
            console.log("oniceconnectionstatechange", peerConnection.iceConnectionState)
        }
        peerConnection.onicegatheringstatechange = (event) => {
            console.log("onicegatheringstatechange", peerConnection.iceGatheringState)
        }

        peerConnection.onicecandidate = (event) => {
            console.log("onicecandidate", event)
            const signaling = get().signaling;
            if(!event.candidate) return;
            if(!signaling) {
                set((state) => {
                    if(!event.candidate) return state;
                    let oldCachedIceCandidates = state.localCachedIceCandidates;
                    console.log("writing local cached ice candidate:", event.candidate)
                    oldCachedIceCandidates = [...oldCachedIceCandidates, event.candidate]
                    return {
                        ...state,
                        localCachedIceCandidates: oldCachedIceCandidates
                    };
                })
            } else {
                api.postNewSignalingCandidate({postNewSignalingCandidateRequest: {
                    callId: signaling.callId,
                    candidate: {
                        candidate: event.candidate.candidate,
                        sdpMid: event.candidate.sdpMid ?? undefined,
                        sdpMLineIndex: event.candidate.sdpMLineIndex ?? undefined,
                        usernameFragment: event.candidate.usernameFragment ?? undefined
                    }
                }}).catch((err) => console.error(err))
            }
        }

        const offerDescription = await peerConnection.createOffer();
        await peerConnection.setLocalDescription(offerDescription);

        const offer = {
            sdp: offerDescription.sdp,
            type: offerDescription.type,
        };
        console.log("created offer", offer)

        const call = await api.createCall({createCallRequest: {
            calleeId: calleeId,
            offer: offer
        }}).then((call) => {

            set((state) => {
                console.log("reading local cached ice candidates: " , state.localCachedIceCandidates)
                state.localCachedIceCandidates.forEach((candidate) => {
                    api.postNewSignalingCandidate({postNewSignalingCandidateRequest: {
                            callId: call.id,
                            candidate: {
                                candidate: candidate.candidate,
                                sdpMid: candidate.sdpMid ?? undefined,
                                sdpMLineIndex: candidate.sdpMLineIndex ?? undefined,
                                usernameFragment: candidate.usernameFragment ?? undefined
                            }
                        }}).catch((err) => console.error(err))
                })
                return {...state, localCachedIceCandidates: []};
            })

            return call;
        }).catch((err) => {
            console.error(err)
            return null;
        });

        peerConnection.onsignalingstatechange = (event) => {
            console.log("onsignalingstatechange", peerConnection.signalingState)
            if(peerConnection.signalingState === "stable" && call) {
                set((state) => {
                    console.log("reading remote cached ice candidates: " , state.remoteCachedIceCandidates)
                    state.remoteCachedIceCandidates[call?.id]?.forEach((candidate) => {
                        state.signaling?.peerConnection.addIceCandidate(candidate).catch((err) => console.error(err))
                    })
                    const cachedIceCandidates = state.remoteCachedIceCandidates;
                    delete cachedIceCandidates[call?.id];
                    return {...state, remoteCachedIceCandidates: cachedIceCandidates};
                })
            }
        }

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
        const audio = new Audio("/start_call.mp3")
        audio.volume = 0.1
        audio.play().catch(() => {
            console.error("can't start notification audio autoplay, because it's being blocked by the browser")
        })
    },
    acceptCall: async (callId: string, offerSdp: string, offerType: string) => {
        const oldSignaling = get().signaling;
        if(oldSignaling){
            await get().endCall()
        }

        const peerConnection = new RTCPeerConnection(servers);
        const localStream = await navigator.mediaDevices.getUserMedia({ video: {facingMode: get().facingMode}, audio: true })
            .catch((x) => {
                console.error(x)
                return new MediaStream();
            });
        const remoteStream = new MediaStream();
        const webcamVideo = document.getElementById("webcamVideo") as HTMLVideoElement | null;
        const remoteVideo = document.getElementById("remoteVideo") as HTMLVideoElement | null;
        const callStatus: CallStatus = "ONGOING";

        if(webcamVideo) {
            webcamVideo.srcObject = localStream;
        }
        if(remoteVideo) {
            remoteVideo.srcObject = remoteStream;
        }

        localStream?.getTracks().forEach((track) => {
            peerConnection.addTrack(track, localStream)
        })

        peerConnection.createDataChannel("callData", { negotiated: true, id: 100, ordered: false });

        peerConnection.ontrack = (event) => {
            console.log("ontrack")
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
            if(peerConnection.signalingState === "stable" && callId) {
                set((state) => {
                    console.log("reading cached remote ice candidates: " , state.remoteCachedIceCandidates[callId])
                    state.remoteCachedIceCandidates[callId]?.forEach((candidate) => {
                        peerConnection.addIceCandidate(candidate).catch((err) => console.error(err))
                        console.log("added remote ice candidate")
                    })
                    const cachedIceCandidates = state.remoteCachedIceCandidates;
                    delete cachedIceCandidates[callId];
                    return {...state, remoteCachedIceCandidates: cachedIceCandidates};
                })
            }
        }
        peerConnection.onicegatheringstatechange = (event) => {
            console.log("onicegatheringstatechange", peerConnection.iceGatheringState)
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
            }}).catch((err) => console.error(err))
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

        await api.answerCall({answerCallRequest: {
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
        }).catch((err) => console.error(err)).then(() => {
            return api.getSignalingCandidates({callId: callId})
        }).then((candidates) => {
            if(peerConnection && peerConnection.remoteDescription !== null) {
                candidates.forEach((candidate) => peerConnection.addIceCandidate(candidate))
            }
        }).catch((err) => console.error(err))
    },
    denyCall: async (callId) => {
        const notificationId = callIdToCallNotificationId(callId)

        api.answerCall({answerCallRequest: {callId: callId, accept: false}}).catch((err) => console.error(err))
        Notifications.hide(notificationId)
        set({signaling: null})
    },
    endCall: async () => {
        const signaling = get().signaling;
        if(!signaling) return;
        api.endCall({endCallRequest: {callId: signaling.callId}}).catch((err) => console.error(err))
        signaling.peerConnection.close()
        signaling.localStream.getTracks().forEach((x) => x.stop())
        set({signaling: null})
        const audio = new Audio("/call_end.mp3")
        audio.volume = 0.1
        audio.play().catch(() => {
            console.error("can't start notification audio autoplay, because it's being blocked by the browser")
        })
    },
    handleNotifications: (payload) => {
        if(payload.type === "SIGNALING_NEW_ANSWER") {
            if(!payload || !payload.callId || !payload.calleeId || !payload.answerSdp || !payload.answerType) return;
            console.log("Got new SIGNALING_NEW_ANSWER")

            const signaling = get().signaling;
            if(!signaling || signaling.callState !== "PENDING") return;

            const answerDescription = new RTCSessionDescription({
                type: payload.answerType as RTCSdpType,
                sdp: payload.answerSdp
            });
            signaling.peerConnection.setRemoteDescription(answerDescription).then((x) => {
                set((state) => ({
                    ...state,
                    signaling: state.signaling ? {
                        ...state.signaling,
                        callState: "ONGOING"
                    } : null,
                }))
            }).catch((err) => console.error(err))
        } else if(payload.type === "SIGNALING_NEW_CANDIDATE") {
            console.log("received candidate")
            if(!payload || !payload.candidateCandidate || !payload.candidateSdpMid || !payload.candidateSdpMLineIndex || !payload.candidateUsernameFragment || !payload.callId) return;
            console.log("Got new SIGNALING_NEW_CANDIDATE")

            const iceCandidate = new RTCIceCandidate({
                candidate: payload.candidateCandidate,
                sdpMid: payload.candidateSdpMid,
                sdpMLineIndex: Number.parseInt(payload.candidateSdpMLineIndex),
                usernameFragment: payload.candidateUsernameFragment
            })

            const signaling = get().signaling;
            if(!signaling || !signaling.peerConnection.remoteDescription) {
                set((state) => {
                    const callId = payload.callId;
                    if(!callId) return state;
                    const oldCachedIceCandidates = state.remoteCachedIceCandidates;
                    console.log("writing remote cached ice candidate:", callId, iceCandidate)
                    oldCachedIceCandidates[callId] = [...(callId in oldCachedIceCandidates ? oldCachedIceCandidates[callId] : []), iceCandidate]
                    return {
                        ...state,
                        remoteCachedIceCandidates: oldCachedIceCandidates
                    };
                })
                return;
            }
            signaling.peerConnection.addIceCandidate(iceCandidate).catch((err) => console.error(err))
            console.log("added remote ice candidate")
        } else if(payload.type === "CALL_ENDED") {
            if(!payload || !payload.callId) return;
            Notifications.hide(callIdToCallNotificationId(payload.callId))
            if(!get().signaling || get().signaling?.callId !== payload.callId) return;
            get().endCall()
        }
    }
}))
