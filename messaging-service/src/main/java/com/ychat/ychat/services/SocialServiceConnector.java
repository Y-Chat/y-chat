package com.ychat.ychat.services;

import com.openapi.gen.social.ApiClient;
import com.openapi.gen.social.ApiException;
import com.openapi.gen.social.api.InternalEndpointApi;
import com.openapi.gen.social.dto.ChatMemberDTO;
import com.openapi.gen.social.dto.PageChatMemberDTO;
import com.openapi.gen.social.dto.Pageable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
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
            return socialServiceInternalEndpointApi.shouldReceive(userId, chatId);
        } catch (ApiException e) {
            throw new RuntimeException(e);
        }
    }

    public PageChatMember getChatMembersInternal(UUID chatId, UUID userId, Pageable pageable) {
        try {
            return new PageChatMember(socialServiceInternalEndpointApi.getChatMembersInternal(chatId, userId, pageable));
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
