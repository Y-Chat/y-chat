package ychat.socialservice.controller;

import lombok.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ychat.socialservice.service.ChatDTO;
import ychat.socialservice.model.chat.ChatStatus;
import ychat.socialservice.service.ChatService;
import ychat.socialservice.service.ChatType;

import java.util.Set;
import java.util.UUID;

/**
 * This endpoint manages chats, an abstraction over group and direct chats, specifically creating
 * direct chats, the chat type, and handling the chat status for the members. Group chats can be
 * created at the groups endpoint.
 * <p>
 * A direct chat cannot be deleted explicitly but is deleted once both members have set the chat
 * status to deleted. The deletion includes the entries to chat members.
 */
@RestController
@RequestMapping("/chats")
public class ChatController {
    private final ChatService chatService;

    public ChatController(@NonNull ChatService chatService) {
        this.chatService = chatService;
    }

    // Lifecycle and chat type start ---------------------------------------------------------------
    /**
     * Creates a direct chat between two users. A user cannot create a chat with themselves and
     * there cannot exist more than one chat between two users.
     *
     * @param fstUserId the id of the first user
     * @param sndUserId the id of the second user
     * @return the id of the newly created direct chat
     */
    @PostMapping
    public ResponseEntity<UUID> createDirectChat(
        @RequestParam(name = "fstUserId") UUID fstUserId,
        @RequestParam(name = "sndUserId") UUID sndUserId
    ) {
        UUID chatId = chatService.createDirectChat(fstUserId, sndUserId);
        return ResponseEntity.status(HttpStatus.CREATED).body(chatId);
    }

    /**
     * Fetches the chat type, i.e. direct or group chat, for a given chat.
     *
     * @param chatId the id of the chat
     * @return the chat type
     */
    @GetMapping("/{chatId}")
    public ResponseEntity<ChatType> getType(@PathVariable UUID chatId) {
        ChatType chatType = chatService.getType(chatId);
        return ResponseEntity.status(HttpStatus.OK).body(chatType);
    }
    // Lifecycle and chat type end -----------------------------------------------------------------

    // Members start -------------------------------------------------------------------------------
    /**
     * Gets all members of a chat. This will either be two or equal to the number of members in a
     * group, thus the response is bounded in size.
     *
     * @param chatId the id of the chat
     * @return a list of user ids corresponding to the chat members
     */
    @GetMapping("/{chatId}/members")
    public ResponseEntity<Set<UUID>> getMembers(@PathVariable UUID chatId) {
        Set<UUID> userIds = chatService.getMembers(chatId);
        return ResponseEntity.status(HttpStatus.OK).body(userIds);
    }

    /**
     * Fetches the status of a chat for a given chat member. This can also be used to check if a
     * user is a member of a chat, if they are not, NOT_A_MEMBER is returned.
     *
     * @param chatId the id of the chat
     * @param userId the id of the chat member
     * @return the chat status of the chat member
     */
    @GetMapping("/{chatId}/members/{userId}")
    public ResponseEntity<ChatStatus> getStatus(@PathVariable UUID chatId,
                                                @PathVariable UUID userId) {
        ChatStatus chatStatus = chatService.getStatus(chatId, userId);
        return ResponseEntity.status(HttpStatus.OK).body(chatStatus);
    }

    /**
     * Updates the status of a chat for a given chat member. All chats can be archived and active,
     * only direct chats can be deleted. To achieve the same effect for a group, the user needs to
     * leave the group. NOT_A_MEMBER cannot be used.
     *
     * @param chatId the id of the chat
     * @param userId the id of the chat member
     * @param chatStatus the new chat status
     * @return no content
     */
    @PutMapping("/{chatId}/members/{userId}")
    public ResponseEntity<Void> setStatus(@PathVariable UUID chatId,
                                          @PathVariable UUID userId,
                                          @RequestBody ChatStatus chatStatus) {
        chatService.setStatus(chatId, userId, chatStatus);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
    // Members end ---------------------------------------------------------------------------------
}