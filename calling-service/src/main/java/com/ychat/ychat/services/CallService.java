package com.ychat.ychat.services;

import com.asyncapi.gen.notification.model.*;
import com.asyncapi.gen.notification.model.SignalingCandidate;
import com.openapi.gen.calling.dto.*;
import com.ychat.ychat.repositories.CallMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CallService {

    private final CallMessageRepository callMessageRepository;

    private final NotificationServiceConnector notificationServiceConnector;

    private final Random random = new Random();

    public CallService(@Autowired CallMessageRepository callMessageRepository, @Autowired NotificationServiceConnector notificationServiceConnector) {
        this.callMessageRepository = callMessageRepository;
        this.notificationServiceConnector = notificationServiceConnector;
    }

    public Call createCall(CreateCallRequest callRequest, UUID requesterId) {
        com.ychat.ychat.models.Call call = new com.ychat.ychat.models.Call(
                UUID.randomUUID(),
                requesterId,
                callRequest.getCalleeId(),
                new com.ychat.ychat.models.Call.SignalingOffer(
                        callRequest.getOffer().getSdp(),
                        callRequest.getOffer().getType()
                ),
                LocalDateTime.now()
        );
        callMessageRepository.save(call);

        Notification notification = new Notification();
        AnonymousSchema14 signalingNewOffer = new AnonymousSchema14();
        signalingNewOffer.setCallId(call.getId().toString());
        signalingNewOffer.setCallerId(call.getCallerId().toString());
        signalingNewOffer.setCalleeId(call.getCalleeId().toString());
        signalingNewOffer.setOffer(call.getOffer().toAsyncAPI());
        notification.setSignalingNewOffer(signalingNewOffer);
        notificationServiceConnector.onNotification(random.nextInt(), notification);

        return call.toOpenAPI();
    }

    public ResponseEntity<Void> answerCall(AnswerCallRequest answerCallRequest, UUID requesterId) {
        var call = callMessageRepository.findById(answerCallRequest.getCallId());
        if(call.isEmpty()) return ResponseEntity.status(404).build();
        if(!call.get().getCalleeId().equals(requesterId)) return ResponseEntity.status(403).build();

        if(!answerCallRequest.getAccept()) {
            call.get().setCallState(com.ychat.ychat.models.Call.CallState.DENIED);
            callMessageRepository.save(call.get());

            Notification notification = new Notification();
            AnonymousSchema33 callEnded = new AnonymousSchema33();
            callEnded.setCallId(call.get().getId().toString());
            callEnded.setReceiverId(call.get().getCallerId().toString());
            notification.setCallEnded(callEnded);
            notificationServiceConnector.onNotification(random.nextInt(), notification);

            return ResponseEntity.ok().build();
        }
        call.get().setCallState(com.ychat.ychat.models.Call.CallState.ONGOING);

        call.get().setAnswer(new com.ychat.ychat.models.Call.SignalingAnswer(
                answerCallRequest.getAnswer().getSdp(),
                answerCallRequest.getAnswer().getType()
        ));
        callMessageRepository.save(call.get());

        Notification notification = new Notification();
        AnonymousSchema20 signalingNewAnswer = new AnonymousSchema20();
        signalingNewAnswer.setCallId(call.get().getId().toString());
        signalingNewAnswer.setCalleeId(call.get().getCalleeId().toString());
        signalingNewAnswer.setCallerId(call.get().getCallerId().toString());
        signalingNewAnswer.setAnswer(call.get().getAnswer().toAsyncAPI());
        notification.setSignalingNewAnswer(signalingNewAnswer);
        notificationServiceConnector.onNotification(random.nextInt(), notification);

        return ResponseEntity.ok().build();
    }

    public ResponseEntity<Void> postNewSignalingCandidate(PostNewSignalingCandidateRequest postNewSignalingCandidateRequest, UUID requesterId) {
        var call = callMessageRepository.findById(postNewSignalingCandidateRequest.getCallId());
        if(call.isEmpty()) return ResponseEntity.status(404).build();
        if(!call.get().getCalleeId().equals(requesterId) && !call.get().getCallerId().equals(requesterId)) return ResponseEntity.status(403).build();

        var candidate = new com.ychat.ychat.models.Call.SignalingCandidate();
        candidate.setCandidate(postNewSignalingCandidateRequest.getCandidate().getCandidate());
        candidate.setSdpMid(postNewSignalingCandidateRequest.getCandidate().getSdpMid());
        candidate.setSdpMLineIndex(postNewSignalingCandidateRequest.getCandidate().getSdpMLineIndex().doubleValue());
        candidate.setUsernameFragment(postNewSignalingCandidateRequest.getCandidate().getUsernameFragment());

        if(call.get().getCallerId().equals(requesterId)) {
            var offerCandidates = call.get().getOfferCandidates();
            offerCandidates.add(candidate);
            call.get().setOfferCandidates(offerCandidates);
        } else {
            var answerCandidates = call.get().getAnswerCandidates();
            answerCandidates.add(candidate);
            call.get().setAnswerCandidates(answerCandidates);
        }
        callMessageRepository.save(call.get());

        if(call.get().getCallState().equals(com.ychat.ychat.models.Call.CallState.ONGOING)) {
            Notification notification = new Notification();
            AnonymousSchema26 signalingNewCandidate = new AnonymousSchema26();
            signalingNewCandidate.setCallId(call.get().getId().toString());
            signalingNewCandidate.setReceiverId(requesterId.equals(call.get().getCalleeId()) ?
                    call.get().getCallerId().toString() :
                    call.get().getCalleeId().toString()
            );
            signalingNewCandidate.setCandidate(candidate.toAsyncAPI());
            notification.setSignalingNewCandidate(signalingNewCandidate);
            notificationServiceConnector.onNotification(random.nextInt(), notification);
        }

        return ResponseEntity.ok().build();
    }

    public ResponseEntity<Void> endCall(EndCallRequest endCallRequest, UUID requesterId) {
        var call = callMessageRepository.findById(endCallRequest.getCallId());
        if(call.isEmpty()) return ResponseEntity.status(404).build();
        if(!call.get().getCalleeId().equals(requesterId) && !call.get().getCallerId().equals(requesterId)) return ResponseEntity.status(403).build();
        call.get().setCallState(com.ychat.ychat.models.Call.CallState.ENDED);
        callMessageRepository.save(call.get());

        Notification notification = new Notification();
        AnonymousSchema33 anonymousSchema33 = new AnonymousSchema33();
        anonymousSchema33.setCallId(call.get().getId().toString());
        anonymousSchema33.setReceiverId(requesterId.equals(call.get().getCalleeId()) ?
                call.get().getCallerId().toString() :
                call.get().getCalleeId().toString()
        );

        notification.setCallEnded(anonymousSchema33);
        notificationServiceConnector.onNotification(random.nextInt(), notification);

        return ResponseEntity.ok().build();
    }

    public ResponseEntity<List<com.openapi.gen.calling.dto.SignalingCandidate>> getCandidates(UUID callId, UUID requesterId) {
        var call = callMessageRepository.findById(callId);
        if(call.isEmpty()) return ResponseEntity.status(404).build();
        if(!call.get().getCalleeId().equals(requesterId) && !call.get().getCallerId().equals(requesterId)) return ResponseEntity.status(403).build();

        Set<com.ychat.ychat.models.Call.SignalingCandidate> candidateSet =
                call.get().getCallerId().equals(requesterId) ?
                    call.get().getAnswerCandidates():
                    call.get().getOfferCandidates();

        List<com.openapi.gen.calling.dto.SignalingCandidate> candidateList = candidateSet.stream().map(com.ychat.ychat.models.Call.SignalingCandidate::toOpenAPI).toList();

        return ResponseEntity.ok(candidateList);
    }

}
