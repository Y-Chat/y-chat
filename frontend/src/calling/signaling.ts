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

    constructor() {
        this.peerConnection = new RTCPeerConnection(servers);
        this.remoteStream = new MediaStream();
        this.localStream = new MediaStream();
        navigator.mediaDevices.getUserMedia({ video: true, audio: true }).then((x) => {
            this.localStream = x;
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

        api.createCall({call: {
            id: "c7d5906b-df61-45bd-b44e-b3b8d4c8946a", // Is ignored by server, will be fixed so we don't need to pass a random uuid here
            timestamp: new Date(),
            calleeId: mockedCalleeId,
            callerId: mockedCallerId
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