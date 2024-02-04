package ychat.socialservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ychat.socialservice.SecurityConfig;
import ychat.socialservice.service.ChatService;
import ychat.socialservice.service.InternalService;
import ychat.socialservice.service.dto.ChatMemberDTO;

import java.util.UUID;

@RestController
@RequestMapping("/internal")
@ResponseStatus(HttpStatus.OK)
@Validated
@Tag(
    name = "Internal Endpoint",
    description = "Specialised endpoints for the use of internal services. Cannot be requested " +
                  "from outside the API gateway and does not require authentication."
)
public class InternalController {
    private final InternalService internalService;

    private final ChatService chatService;

    public InternalController(@NonNull InternalService internalService, @Autowired ChatService chatService) {
        this.internalService = internalService;
        this.chatService = chatService;
    }

    @GetMapping("/{userId}/shouldReceive/{chatId}")
    @Operation(
        summary = "Check for a given user if they should receive a message sent to the given chat.",
        description = "Returns a boolean value which responds to the action."
    )
    public boolean shouldReceive(@PathVariable UUID userId, @PathVariable UUID chatId) {
        return internalService.shouldReceive(userId, chatId);
    }

    @GetMapping("/{chatId}/members")
    @Operation(
            summary = "Fetch all members of a chat.",
            description = "Returns the user ids, profiles, and if it is a group chat, also the roles " +
                    "of each member. Page size is not allowed to exceed " +
                    ChatService.MAX_CHAT_MEMBER_PAGE_SIZE + "."
    )
    public Page<ChatMemberDTO> getChatMembers(@PathVariable UUID chatId, Pageable pageable) {
        return chatService.getChatMembersInternal(chatId, pageable);
    }
}
