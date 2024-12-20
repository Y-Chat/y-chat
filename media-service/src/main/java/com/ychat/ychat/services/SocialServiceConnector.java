package com.ychat.ychat.services;

import com.openapi.gen.social.ApiClient;
import com.openapi.gen.social.ApiException;
import com.openapi.gen.social.api.InternalEndpointApi;
import com.openapi.gen.social.dto.ChatMemberDTO;
import com.openapi.gen.social.dto.PageChatMemberDTO;
import com.openapi.gen.social.dto.Pageable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.UUID;

@Service
public class SocialServiceConnector {

    InternalEndpointApi socialServiceInternalEndpointApi;

    public SocialServiceConnector(@Value("${ychat.social.url}") String socialServiceURL) throws MalformedURLException {
        ApiClient apiClient = new ApiClient();
        var url = new URL(socialServiceURL);
        apiClient.setHost(url.getHost());
        apiClient.setPort(url.getPort());
        apiClient.setScheme(url.getProtocol());
        this.socialServiceInternalEndpointApi = new InternalEndpointApi(apiClient);
    }

    public boolean canUserAccessChat(UUID userId, UUID chatId) {
        try {
            return socialServiceInternalEndpointApi.shouldReceive(userId, chatId);
        } catch (ApiException e) {
            throw new RuntimeException(e);
        }
    }

    public PageChatMember getChatMembersInternal(UUID chatId, Pageable pageable) {
        try {
            return new PageChatMember(socialServiceInternalEndpointApi.getChatMembersInternal(chatId, pageable));
        } catch (ApiException e) {
            return null;
        }
    }

    public class PageChatMember {

        private Integer pageSize;
        private Integer pageNumber;
        private List<ChatMemberDTO> chatMembers;

        public PageChatMember(PageChatMemberDTO chatMemberDTO) {
            this.chatMembers = chatMemberDTO.getContent();
            this.pageNumber = chatMemberDTO.getPageable() != null ? chatMemberDTO.getPageable().getPageNumber() : Integer.valueOf(0);
            this.pageSize = chatMemberDTO.getPageable() != null ? chatMemberDTO.getPageable().getPageNumber() : Integer.valueOf(20);
        }

        public Integer getPageSize() {
            return pageSize;
        }

        public Integer getPageNumber() {
            return pageNumber;
        }

        public List<ChatMemberDTO> getChatMembers() {
            return chatMembers;
        }
    }
}
