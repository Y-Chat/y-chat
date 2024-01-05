package ychat.socialservice.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ychat.socialservice.model.chat.ChatStatus;
import ychat.socialservice.model.chat.ChatType;
import ychat.socialservice.service.ChatService;

import java.util.List;
import java.util.Set;
import java.util.UUID;

/*
- TODO group chats
- I think explicitly is better for search than saying not found TODO
- A direct chat is deleted once both users have deleted the chat TODO
- Group chats are coupled to the lifetime of a group
 */

@RestController
@RequestMapping("/chats")
public class ChatController {
    private ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @GetMapping
    public ResponseEntity<List<ChatType>> getChatTypes(@RequestBody List<UUID> chatIds) {
        List<ChatType> chatTypes = chatService.getChatTypes(chatIds);
        return ResponseEntity.ok(chatTypes);
    }

    @PostMapping
    public ResponseEntity<Void> createDirectChat(
        @RequestParam(name = "fst-user-id") UUID fstUserId,
        @RequestParam(name = "snd-user-id") UUID sndUserId
    ) {
        chatService.createDirectChat(fstUserId, sndUserId);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/{chatId}/participants")
    public ResponseEntity<Set<UUID>> getParticipants(@PathVariable UUID chatId) {
        Set<UUID> userIds = chatService.getChatParticipants(chatId);
        return ResponseEntity.ok(userIds);
    }

    // Status start --------------------------------------------------------------------------------
    @PutMapping("/{chatId}/status")
    public ResponseEntity<Void> setStatus(@PathVariable UUID chatId,
                                          @RequestParam(name = "user-id") UUID userId,
                                          @RequestBody ChatStatus chatStatus) {
        chatService.setStatus(chatId, userId, chatStatus);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/{chatId}/status")
    public ResponseEntity<ChatStatus> getStatus(@PathVariable UUID chatId,
                                                @RequestParam(name = "user-id") UUID userId) {
        ChatStatus chatStatus = chatService.getStatus(chatId, userId);
        return ResponseEntity.ok(chatStatus);
    }
    // Status end ----------------------------------------------------------------------------------
}