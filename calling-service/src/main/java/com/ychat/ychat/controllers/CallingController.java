package com.ychat.ychat.controllers;

import com.openapi.gen.calling.api.CallApi;
import com.openapi.gen.calling.dto.Call;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CallingController implements CallApi {

   @Override
   public ResponseEntity<Call> createCall(Call call) {
      return ResponseEntity.status(501).build();
   }
}
