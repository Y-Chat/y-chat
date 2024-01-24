package com.ychat.ychat.controllers;

import com.openapi.gen.payment.api.FundsApi;
import com.openapi.gen.payment.dto.AccountBalance;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import java.util.UUID;

@Controller
public class FundController implements FundsApi {
    @Override
    public ResponseEntity<AccountBalance> getFunds(UUID userId) {
        // TODO
        return FundsApi.super.getFunds(userId);
    }
}
