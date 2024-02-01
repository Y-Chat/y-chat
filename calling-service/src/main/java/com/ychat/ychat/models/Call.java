package com.ychat.ychat.models;

import com.asyncapi.gen.notification.model.SignalingOffer;
import jakarta.validation.Valid;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.*;

@Document
public class Call {

    public Call(UUID id, UUID callerId, UUID calleeId, SignalingOffer offer, LocalDateTime timestamp) {
        this.id = id;
        this.callerId = callerId;
        this.calleeId = calleeId;
        this.callState = CallState.PENDING;
        this.offer = offer;
        this.answer = null;
        this.offerCandidates = new HashSet<>();
        this.answerCandidates = new HashSet<>();
        this.timestamp = timestamp;
    }

    @Id
    private UUID id;

    private UUID callerId;

    private UUID calleeId;

    private LocalDateTime timestamp;

    private CallState callState;

    private SignalingOffer offer;

    private SignalingAnswer answer;

    private Set<SignalingCandidate> offerCandidates;

    private Set<SignalingCandidate> answerCandidates;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getCallerId() {
        return callerId;
    }

    public void setCallerId(UUID callerId) {
        this.callerId = callerId;
    }

    public UUID getCalleeId() {
        return calleeId;
    }

    public void setCalleeId(UUID calleeId) {
        this.calleeId = calleeId;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public com.openapi.gen.calling.dto.Call toOpenAPI() {
        return new com.openapi.gen.calling.dto.Call(this.id, this.callerId, this.calleeId, this.timestamp);
    }

    public CallState getCallState() {
        return callState;
    }

    public void setCallState(CallState callState) {
        this.callState = callState;
    }

    public SignalingOffer getOffer() {
        return offer;
    }

    public void setOffer(SignalingOffer offer) {
        this.offer = offer;
    }

    public SignalingAnswer getAnswer() {
        return answer;
    }

    public void setAnswer(SignalingAnswer answer) {
        this.answer = answer;
    }

    public Set<SignalingCandidate> getOfferCandidates() {
        return offerCandidates;
    }

    public void setOfferCandidates(Set<SignalingCandidate> offerCandidates) {
        this.offerCandidates = offerCandidates;
    }

    public Set<SignalingCandidate> getAnswerCandidates() {
        return answerCandidates;
    }

    public void setAnswerCandidates(Set<SignalingCandidate> answerCandidates) {
        this.answerCandidates = answerCandidates;
    }

    public static enum CallState {
        PENDING, DENIED, ONGOING, ENDED;
    }

    public static class SignalingOffer {
        private String sdp;
        private String type;

        public SignalingOffer(String sdp, String type) {
            this.sdp = sdp;
            this.type = type;
        }

        public String getSdp() {
            return sdp;
        }

        public void setSdp(String sdp) {
            this.sdp = sdp;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public com.asyncapi.gen.notification.model.SignalingOffer toAsyncAPI() {
            var offer = new com.asyncapi.gen.notification.model.SignalingOffer();
            offer.setSdp(this.sdp);
            offer.setType(this.type);
            return offer;
        }
    }

    public static class SignalingAnswer {
        private String sdp;
        private String type;

        public SignalingAnswer(String sdp, String type) {
            this.sdp = sdp;
            this.type = type;
        }

        public String getSdp() {
            return sdp;
        }

        public void setSdp(String sdp) {
            this.sdp = sdp;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public com.asyncapi.gen.notification.model.SignalingAnswer toAsyncAPI() {
            var answer = new com.asyncapi.gen.notification.model.SignalingAnswer();
            answer.setSdp(this.sdp);
            answer.setType(this.type);
            return answer;
        }
    }

    public static class SignalingCandidate {

        private String candidate;

        private Double sdpMLineIndex;

        private String sdpMid;

        private String usernameFragment;

        public String getCandidate() {
            return candidate;
        }

        public void setCandidate(String candidate) {
            this.candidate = candidate;
        }

        public Double getSdpMLineIndex() {
            return sdpMLineIndex;
        }

        public void setSdpMLineIndex(Double sdpMLineIndex) {
            this.sdpMLineIndex = sdpMLineIndex;
        }

        public String getSdpMid() {
            return sdpMid;
        }

        public void setSdpMid(String sdpMid) {
            this.sdpMid = sdpMid;
        }

        public String getUsernameFragment() {
            return usernameFragment;
        }

        public void setUsernameFragment(String usernameFragment) {
            this.usernameFragment = usernameFragment;
        }

        public com.asyncapi.gen.notification.model.SignalingCandidate toAsyncAPI() {
            var candidate = new com.asyncapi.gen.notification.model.SignalingCandidate();
            candidate.setCandidate(this.candidate);
            candidate.setSdpMid(this.sdpMid);
            candidate.setSdpMLineIndex(this.sdpMLineIndex);
            candidate.setUsernameFragment(this.usernameFragment);
            return candidate;
        }
    }
}
