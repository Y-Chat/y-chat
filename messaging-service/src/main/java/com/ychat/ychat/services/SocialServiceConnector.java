package com.ychat.ychat.services;

import com.openapi.gen.social.ApiClient;
import com.openapi.gen.social.ApiException;
import com.openapi.gen.social.api.InternalEndpointApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class SocialServiceConnector {

    @Value("${ychat.social.url}")
    private String socialServiceURL;

    InternalEndpointApi socialServiceInternalEndpointApi;

    public SocialServiceConnector() {
        ApiClient apiClient = new ApiClient();
        apiClient.setHost(socialServiceURL);
        this.socialServiceInternalEndpointApi = new InternalEndpointApi();
    }

    public boolean canUserAccessChat(UUID userId, UUID chatId) {
        try {
            // TODO
            socialServiceInternalEndpointApi.canReceive();
        } catch (ApiException e) {
            throw new RuntimeException(e);
        }
        return true;
    }


}
