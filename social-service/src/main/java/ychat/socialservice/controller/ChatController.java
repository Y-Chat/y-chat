package ychat.socialservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ychat.socialservice.SecurityConfig;
import ychat.socialservice.model.chat.ChatStatus;
import ychat.socialservice.service.dto.ChatDTO;
import ychat.socialservice.service.dto.ChatMemberDTO;
import ychat.socialservice.service.ChatService;

import java.util.UUID;

@RestController
@RequestMapping("/chats")
@ResponseStatus(HttpStatus.OK)
@Tag(
    name = "Chats Endpoint",
    description = "Manage chats, an abstraction over group and direct chats. Specifically, all " +
                  "operations common to both group and direct chats. A direct chat cannot be " +
                  "deleted explicitly but is deleted once both users have either been deleted or " +
                  "set the chat status to deleted."
)
public class ChatController {
    private final ChatService chatService;

    public ChatController(@NonNull ChatService chatService) {
        this.chatService = chatService;
    }

    // Chats start ---------------------------------------------------------------------------------
    @GetMapping("/{chatId}")
    @Operation(
        summary = "Fetch the general information about a chat for specific user.",
        description = "Returns the chat id and chat type. If it is a group chat, the group " +
                      "profile will also be returned. If it is direct chat, the user profile of " +
                      "the other user will be returned. If the other user of a direct chat does " +
                      "not exist anymore, it will still return the id of the other user but no " +
                      "longer the user profile."
    )
    public ChatDTO getChat(@PathVariable UUID chatId, @RequestParam UUID userId) {
        SecurityConfig.verifyUserAccess(userId);
        return chatService.getChat(chatId, userId);
    }

    @GetMapping
    @Operation(
        summary = "Fetch all chats which a user is part of.",
        description = "Returns a page of the same objects that getChat returns. Page size is not " +
                      "allowed to exceed " + ChatService.MAX_CHAT_PAGE_SIZE + "."
    )
    public Page<ChatDTO> getAllChats(@RequestParam UUID userId, Pageable pageable) {
        SecurityConfig.verifyUserAccess(userId);
        return chatService.getAllChats(userId, pageable);
    }

    @PostMapping("/directChats")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
        summary = "Create a direct chat between two users.",
        description = "A user cannot create a chat with themselves and there cannot exist more " +
                      "than one chat between two users. Returns the same object as getChat. " +
                      "UserId refers to the user in relation to which the ChatDTO should be " +
                      "returned."
    )
    public ChatDTO createDirectChat(@RequestParam UUID userId, @RequestParam UUID otherUserId) {
        SecurityConfig.verifyUserAccess(userId);
        return chatService.createDirectChat(userId, otherUserId);
    }
    // Chats end -----------------------------------------------------------------------------------

    // Members start -------------------------------------------------------------------------------
    @GetMapping("/{chatId}/members")
    @Operation(
        summary = "Fetch all members of a chat.",
        description = "Returns the user ids, profiles, and if it is a group chat, also the roles " +
                      "of each member. Page size is not allowed to exceed " +
                      ChatService.MAX_CHAT_MEMBER_PAGE_SIZE + "."
    )
    public Page<ChatMemberDTO> getChatMembers(@PathVariable UUID chatId, @RequestParam UUID userId,
                                              Pageable pageable) {
        SecurityConfig.verifyUserAccess(userId);
        return chatService.getChatMembers(chatId, userId, pageable);
    }

    @GetMapping("/{chatId}/members/{userId}/status")
    @Operation(
        summary = "Fetch the chat status for a given chat and user.",
        description = "If the user is not part of the chat, NOT_A_MEMBER is returned."
    )
    public ChatStatus getChatStatus(@PathVariable UUID chatId, @PathVariable UUID userId) {
        SecurityConfig.verifyUserAccess(userId);
        return chatService.getChatStatus(chatId, userId);
    }

    @PatchMapping("/{chatId}/members/{userId}/status")
    @Operation(
        summary = "Update the chat status for a given chat and user.",
        description = "One cannot update the chat status of a user who is not a member of the " +
                      "chat. Direct chats are not allowed to be set to NOT_A_MEMBER. Group chats " +
                      "are not allowed to be set to neither NOT_A_MEMBER nor DELETED. This " +
                      "endpoint cannot be used to exit a group. When both members of a direct " +
                      "chat have the chat as DELETED, then the chat will be deleted."
    )
    public ChatStatus setChatStatus(@PathVariable UUID chatId, @PathVariable UUID userId,
                                    @RequestBody ChatStatus chatStatus) {
        SecurityConfig.verifyUserAccess(userId);
        return chatService.setChatStatus(chatId, userId, chatStatus);
    }
    // Members end ---------------------------------------------------------------------------------
}