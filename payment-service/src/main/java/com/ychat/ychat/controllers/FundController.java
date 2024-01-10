package com.ychat.ychat.controllers;

import com.openapi.gen.payment.api.FundsApi;
import com.openapi.gen.payment.dto.AccountBalance;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

public class FundController implements FundsApi {
    @Override
    public ResponseEntity<AccountBalance> getFunds(UUID userId) {
        return FundsApi.super.getFunds(userId);
    }
}
