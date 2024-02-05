package ychat.socialservice.service;


import lombok.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import ychat.socialservice.model.chat.DirectChatMember;
import ychat.socialservice.model.group.Group;
import ychat.socialservice.model.group.GroupMember;
import ychat.socialservice.repository.ChatMemberRepository;
import ychat.socialservice.model.chat.ChatMember;

import java.util.Optional;
import java.util.UUID;

/**
 * Performs the business logic for the internal requirements. It does so mainly via the other
 * services.
 */
@Validated
@Service
@Transactional(readOnly = true)
public class InternalService {
    private final UserService userService;
    private final ChatMemberRepository chatMemberRepo;

    public InternalService(@NonNull UserService userService,
                            @NonNull ChatMemberRepository chatMemberRepo) {
        this.userService = userService;
        this.chatMemberRepo = chatMemberRepo;
    }

    public boolean shouldReceive(UUID userId, UUID chatId) {
        Optional<ChatMember> optionalChatMember =
            chatMemberRepo.findByUserIdAndChatId(userId, chatId);
        if (optionalChatMember.isEmpty())
            return false;
        ChatMember chatMember = optionalChatMember.get();
        if (chatMember.getClass() == GroupMember.class)
            return true;
        UUID otherUserId = ((DirectChatMember) chatMember).getOtherUserId();
        return userService.isBlockedUser(userId, otherUserId) == null;
    }
}
