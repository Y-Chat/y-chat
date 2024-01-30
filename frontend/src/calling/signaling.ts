import { api } from '../network/api'

const servers = {
    iceServers: [
        {
            urls: ['stun:stun1.l.google.com:19302', 'stun:stun2.l.google.com:19302'],
        },
        {
            urls: "stun:relay.metered.ca:80",
        },
        {
            urls: "stun:stun.relay.metered.ca:80",
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

export class CallSignaling {
    peerConnection = new RTCPeerConnection(servers);
    localStream: MediaStream;
    remoteStream: MediaStream;
    webcamVideo: HTMLVideoElement | null;
    remoteVideo: HTMLVideoElement | null;

    constructor() {
        this.peerConnection = new RTCPeerConnection(servers);
        this.remoteStream = new MediaStream();
        this.localStream = new MediaStream();
        this.webcamVideo = document.getElementById("webcamVideo") as HTMLVideoElement | null;
        this.remoteVideo = null;
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
        })
    }

    async createCall() {
        const mockedCalleeId = "b52684f9-724b-3e55-8581-f581030b9ccb"
        const mockedCallerId = "a3fbd22e-993e-3ccf-a6a4-3e4898437e56"

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

        api.createCall({createCallRequest: {
            calleeId: mockedCalleeId,
            offer: {
                type: ""
            }
        }})
    }

    async acceptCall() {
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
}
