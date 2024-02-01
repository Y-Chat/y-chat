package com.ychat.ychat.controllers;

import com.openapi.gen.calling.api.CallApi;
import com.openapi.gen.calling.dto.*;
import com.ychat.ychat.SecurityConfig;
import com.ychat.ychat.services.CallService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CallingController implements CallApi {

   private final CallService callService;

   public CallingController(@Autowired CallService callService) {
      this.callService = callService;
   }

   @Override
   public ResponseEntity<Call> createCall(CreateCallRequest createCallRequest) {
      var requesterId = SecurityConfig.getRequesterUUID();
      return ResponseEntity.ok(callService.createCall(createCallRequest, requesterId));
   }

   @Override
   public ResponseEntity<Void> answerCall(AnswerCallRequest answerCallRequest) {
      var requesterId = SecurityConfig.getRequesterUUID();
      return callService.answerCall(answerCallRequest, requesterId);
   }

   @Override
   public ResponseEntity<Void> postNewSignalingCandidate(PostNewSignalingCandidateRequest postNewSignalingCandidateRequest) {
      var requesterId = SecurityConfig.getRequesterUUID();
      return callService.postNewSignalingCandidate(postNewSignalingCandidateRequest, requesterId);
   }

   @Override
   public ResponseEntity<Void> endCall(EndCallRequest endCallRequest) {
      var requesterId = SecurityConfig.getRequesterUUID();
      return callService.endCall(endCallRequest, requesterId);
   }
}
