package ychat.socialservice.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
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
@Service
public class ChatService {
    public static final int MAX_CHAT_PAGE_SIZE = 1000;
    public static final int MAX_CHAT_MEMBER_PAGE_SIZE = 1000;

    private final UserService userService;
    private final ChatRepository chatRepo;
    private final ChatMemberRepository chatMemberRepo;
    private final DirectChatRepository directChatRepo;
    private final DirectChatMemberRepository directChatMemberRepo;

    public ChatService(@NonNull UserService userService,
                       @NonNull ChatRepository chatRepo,
                       @NonNull DirectChatRepository directChatRepo,
                       @NonNull ChatMemberRepository chatMemberRepo,
                       @NonNull DirectChatMemberRepository directChatMemberRepo) {
        this.userService = userService;
        this.chatRepo = chatRepo;
        this.directChatRepo = directChatRepo;
        this.chatMemberRepo = chatMemberRepo;
        this.directChatMemberRepo = directChatMemberRepo;
    }

    public Chat findChatByIdOrThrow(UUID chatId) {
        Optional<Chat> optionalChat = chatRepo.findById(chatId);
        if (optionalChat.isEmpty())
            throw new EntityNotFoundException("Chat does not exist: " + chatId);
        return optionalChat.get();
    }

    public Optional<ChatMember> findChatMemberByIdsOrThrow(UUID chatId, UUID userId) {
        Chat chat = findChatByIdOrThrow(chatId);
        User user = userService.findUserByIdOrThrow(userId);
        return chatMemberRepo.findByUserAndChat(user, chat);
    }

    // Chats start ---------------------------------------------------------------------------------
    public ChatDTO getChat(UUID chatId, UUID userId) {
        Chat chat = findChatByIdOrThrow(chatId);
        User user = userService.findUserByIdOrThrow(userId);
        return DTOConverter.convertToDTO(chat, user);
    }

    public Page<ChatDTO> getAllChats(UUID userId, Pageable pageable) {
        User user = userService.findUserByIdOrThrow(userId);
        Page<ChatMember> chatMembers = chatMemberRepo.findAllByUser(user, pageable);
        return chatMembers.map(chatMember -> DTOConverter.convertToDTO(chatMember.getChat(), user));
    }

    public ChatDTO createDirectChat(UUID userId, UUID otherUserId) {
        User user = userService.findUserByIdOrThrow(userId);
        User otherUser = userService.findUserByIdOrThrow(otherUserId);
        if (directChatMemberRepo.existsBetweenTwoUsers(user, otherUser)) {
            throw new IllegalUserInputException(
                "Direct chat between " + user + " and " + otherUser + " already exists."
            );
        }
        DirectChat directChat = new DirectChat(user, otherUser);
        directChatRepo.save(directChat);
        return DTOConverter.convertToDTO(directChat, user);
    }
    // Chats end ---------------------------------------------------------------------------------

    // Members start -------------------------------------------------------------------------------
    public Page<ChatMemberDTO> getChatMembers(UUID chatId, Pageable pageable) {
        Chat chat = findChatByIdOrThrow(chatId);
        Page<ChatMember> chatMembers = chatMemberRepo.findAllByChat(chat, pageable);
        return chatMembers.map(DTOConverter::convertToDTO);
    }

    public ChatStatus getChatStatus(UUID chatId, UUID userId) {
        Optional<ChatMember> optionalChatMember = findChatMemberByIdsOrThrow(chatId, userId);
        if (optionalChatMember.isEmpty())
            return ChatStatus.NOT_A_MEMBER;
        ChatMember chatMember = optionalChatMember.get();
        return chatMember.getChatStatus();
    }

    public ChatStatus setChatStatus(UUID chatId, UUID userId, ChatStatus chatStatus) {
        if (chatStatus == ChatStatus.NOT_A_MEMBER)
            throw new IllegalUserInputException("NOT_A_MEMBER is not allowed for a chat status.");

        Optional<ChatMember> optionalChatMember = findChatMemberByIdsOrThrow(chatId, userId);
        if (optionalChatMember.isEmpty())
            throw new IllegalUserInputException(userId + " not a member of " + chatId + ".");
        ChatMember chatMember = optionalChatMember.get();
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
                chatRepo.delete(chat);
                return ChatStatus.DELETED;
            }
        }
        chatMember.setChatStatus(chatStatus);
        chatMemberRepo.save(chatMember);
        return chatMember.getChatStatus();
    }
    // Members end ---------------------------------------------------------------------------------
}
