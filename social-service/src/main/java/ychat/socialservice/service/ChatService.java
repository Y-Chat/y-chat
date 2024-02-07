package ychat.socialservice.service;

import jakarta.validation.constraints.NotNull;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import ychat.socialservice.model.group.Group;
import ychat.socialservice.repository.*;
import ychat.socialservice.service.dto.ChatDTO;
import ychat.socialservice.service.dto.ChatMemberDTO;
import ychat.socialservice.model.chat.*;
import ychat.socialservice.model.user.User;
import ychat.socialservice.service.dto.DTOConverter;
import ychat.socialservice.util.IllegalUserInputException;

import java.util.*;

/**
 * Performs the business logic on chats and their members. The methods are designed to
 * be simple and readable, not performant. The code will be optimized when we discover
 * bottlenecks.
 */
@Validated
@Service
@Transactional(readOnly = true)
public class ChatService {
    public static final int MAX_CHAT_PAGE_SIZE = 1000;
    public static final int MAX_CHAT_MEMBER_PAGE_SIZE = 1000;

    private final UserService userService;
    private final ChatMemberRepository chatMemberRepo;
    private final DirectChatRepository directChatRepo;
    private final DirectChatMemberRepository directChatMemberRepo;

    public ChatService(@NonNull UserService userService,
                       @NonNull DirectChatRepository directChatRepo,
                       @NonNull ChatMemberRepository chatMemberRepo,
                       @NonNull DirectChatMemberRepository directChatMemberRepo) {
        this.userService = userService;
        this.directChatRepo = directChatRepo;
        this.chatMemberRepo = chatMemberRepo;
        this.directChatMemberRepo = directChatMemberRepo;
    }

    public ChatMember findChatMemberByIdsOrThrow(UUID userId, UUID chatId) {
        Optional<ChatMember> optionalChatMember =
            chatMemberRepo.findByUserIdAndChatId(userId, chatId);
        if (optionalChatMember.isEmpty()) {
            throw new IllegalUserInputException(
                userId + " is not a member of chat " + chatId + "."
            );
        }
        return optionalChatMember.get();
    }

    // Chats start ---------------------------------------------------------------------------------
    public ChatDTO getChat(@NotNull UUID chatId, @NotNull UUID userId) {
        ChatMember chatMember = findChatMemberByIdsOrThrow(userId, chatId);
        User user = chatMember.getUser();
        Chat chat = chatMember.getChat();
        return DTOConverter.convertToDTO(chat, user);
    }

    public Page<ChatDTO> getAllChats(@NotNull UUID userId, @NotNull Pageable pageable) {
        if (pageable.isUnpaged() || pageable.getPageSize() > MAX_CHAT_PAGE_SIZE) {
            throw new IllegalUserInputException(
                "Get all chats request must be paged with maximum page size: "
                + MAX_CHAT_PAGE_SIZE
            );
        }
        User user = userService.findUserByIdOrThrow(userId);
        Page<ChatMember> chatMembers = chatMemberRepo.findAllByUserId(userId, pageable);
        return chatMembers.map(chatMember -> DTOConverter.convertToDTO(chatMember.getChat(), user));
    }

    @Transactional
    public ChatDTO createDirectChat(@NotNull UUID userId, @NotNull UUID otherUserId) {
        if (userId.equals(otherUserId)) {
            throw new IllegalUserInputException(
                "User is not allowed to establish a direct chat with themselves " + userId + "."
            );
        }
        if (directChatMemberRepo.existsBetweenTwoUsers(userId, otherUserId)) {
            throw new IllegalUserInputException(
                "Direct chat between " + userId + " and " + otherUserId + " already exists."
            );
        }
        User user = userService.findUserByIdOrThrow(userId);
        User otherUser = userService.findUserByIdOrThrow(otherUserId);
        DirectChat directChat = new DirectChat(user, otherUser);
        directChatRepo.save(directChat);
        return DTOConverter.convertToDTO(directChat, user);
    }
    // Chats end ---------------------------------------------------------------------------------

    // Members start -------------------------------------------------------------------------------
    public Page<ChatMemberDTO> getChatMembers(@NotNull UUID chatId, @NotNull UUID userId,
                                              @NotNull Pageable pageable) {
        if (pageable.isUnpaged() || pageable.getPageSize() > MAX_CHAT_PAGE_SIZE) {
            throw new IllegalUserInputException(
                "Get all chat members request must be paged with maximum page size: "
                + MAX_CHAT_MEMBER_PAGE_SIZE
            );
        }
        ChatMember chatMember = findChatMemberByIdsOrThrow(userId, chatId);
        Chat chat = chatMember.getChat();
        Page<ChatMember> chatMembers = chatMemberRepo.findAllByChatId(chat.getId(), pageable);
        return chatMembers.map(DTOConverter::convertToDTO);
    }

    public Page<ChatMemberDTO> getChatMembersInternal(@NotNull UUID chatId, @NotNull Pageable pageable) {
        if (pageable.isUnpaged() || pageable.getPageSize() > MAX_CHAT_PAGE_SIZE) {
            throw new IllegalUserInputException(
                    "Get all chat members request must be paged with maximum page size: "
                            + MAX_CHAT_MEMBER_PAGE_SIZE
            );
        }
        Page<ChatMember> chatMembers = chatMemberRepo.findAllByChatId(chatId, pageable);
        return chatMembers.map(DTOConverter::convertToDTO);
    }

    public ChatStatus getChatStatus(@NotNull UUID chatId, @NotNull UUID userId) {
        Optional<ChatMember> optionalChatMember =
            chatMemberRepo.findByUserIdAndChatId(userId, chatId);
        if (optionalChatMember.isEmpty())
            return ChatStatus.NOT_A_MEMBER;
        ChatMember chatMember = optionalChatMember.get();
        return chatMember.getChatStatus();
    }

    @Transactional
    public ChatStatus setChatStatus(@NotNull UUID chatId, @NotNull UUID userId,
                                    @NotNull ChatStatus chatStatus) {
        if (chatStatus == ChatStatus.NOT_A_MEMBER)
            throw new IllegalUserInputException("NOT_A_MEMBER is not allowed for a chat status.");
        ChatMember chatMember = findChatMemberByIdsOrThrow(userId, chatId);
        Chat chat = chatMember.getChat();
        User user = chatMember.getUser();
        if (chatStatus == ChatStatus.DELETED) {
            if (chat.getClass() == Group.class) {
                throw new IllegalUserInputException(
                    "DELETED is not allowed as a status for groups."
                );
            }
            if (chat.toDeleteIfUserRemoved(user)) {
                // Deletes chat membership via cascading as well
                directChatRepo.delete((DirectChat) chat);
                return ChatStatus.DELETED;
            }
        }
        chatMember.setChatStatus(chatStatus);
        return chatMember.getChatStatus();
    }
    // Members end ---------------------------------------------------------------------------------
}
