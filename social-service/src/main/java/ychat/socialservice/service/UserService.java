package ychat.socialservice.service;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ychat.socialservice.model.chat.Chat;
import ychat.socialservice.model.chat.ChatMember;
import ychat.socialservice.repository.*;
import ychat.socialservice.service.dto.*;
import ychat.socialservice.util.IllegalUserInputException;
import ychat.socialservice.model.user.*;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Performs the business logic on the user and some of their relations. The methods are designed to
 * be simple and readable, not performant. The code will be optimized when we discover
 * bottlenecks.
 */
@Service
public class UserService {
    public static final int MAX_BLOCKED_USER_PAGE_SIZE = 1000;

    private final UserRepository userRepo;
    private final BlockedUserRepository blockedUserRepo;
    private final ChatRepository chatRepo;
    private final ChatMemberRepository chatMemberRepo;

    public UserService(@NonNull UserRepository userRepo,
                       @NonNull BlockedUserRepository blockedUserRepo,
                       @NonNull ChatRepository chatRepo,
                       @NonNull ChatMemberRepository chatMemberRepo) {
        this.userRepo = userRepo;
        this.blockedUserRepo = blockedUserRepo;
        this.chatRepo = chatRepo;
        this.chatMemberRepo = chatMemberRepo;
    }

    public User findUserByIdOrThrow(UUID userId) {
        Optional<User> optionalUser = userRepo.findById(userId);
        if (optionalUser.isEmpty())
            throw new EntityNotFoundException("User does not exist: " + userId);
        return optionalUser.get();
    }

    private void deleteChatMember(User user, ChatMember chatMember) {
        Chat chat = chatMember.getChat();
        if (chat.toDeleteIfUserRemoved(user)) {
            chatRepo.delete(chat);
        } else {
            chatMemberRepo.delete(chatMember);
        }
    }

    // User start ----------------------------------------------------------------------------------
    public UserDTO createUser(UUID userId, UserProfileDTO userProfileDTO) {
        if (userRepo.existsById(userId))
            throw new EntityExistsException("User exists already: " + userId);
        UserProfile userProfile = DTOConverter.convertToEntity(userProfileDTO);
        User user = new User(userId, userProfile);
        userRepo.save(user);
        return DTOConverter.convertToDTO(user);
    }

    public UserDTO getUser(UUID userId) {
        User user = findUserByIdOrThrow(userId);
        return DTOConverter.convertToDTO(user);
    }

    public void deleteUser(UUID userId) {
        User user = findUserByIdOrThrow(userId);
        Pageable pageable = PageRequest.of(0, ChatService.MAX_CHAT_MEMBER_PAGE_SIZE);
        Page<ChatMember> chatMembers;
        do {
            chatMembers = chatMemberRepo.findAllByUser(user, pageable);
            for (ChatMember chatMember : chatMembers)
                deleteChatMember(user, chatMember);
            pageable = pageable.next();
        } while (chatMembers.hasNext());
        // Deletes the blocked user entries via cascading
        userRepo.delete(user);
    }
    // User end ------------------------------------------------------------------------------------

    // Profiles and settings start -----------------------------------------------------------------
    public UserProfileDTO getUserProfile(UUID userId) {
        User user = findUserByIdOrThrow(userId);
        return DTOConverter.convertToDTO(user.getUserProfile());
    }

    public void updateUserProfile(UUID userId, UserProfileDTO userProfileDTO) {
        User user = findUserByIdOrThrow(userId);
        UserProfile userProfile = user.getUserProfile();
        userProfile.setFirstName(userProfileDTO.firstName());
        userProfile.setLastName(userProfileDTO.lastName());
        if (userProfileDTO.removeProfilePictureId())
            userProfile.removeProfilePictureId();
        else
            userProfile.setProfilePictureId(userProfileDTO.profilePictureId());
        userProfile.setProfileDescription(userProfileDTO.profileDescription());
        userRepo.save(user);
    }

    public UserSettingsDTO getUserSettings(UUID userId) {
        User user = findUserByIdOrThrow(userId);
        return DTOConverter.convertToDTO(user.getUserSettings());
    }

    public void updateUserSettings(UUID userId, UserSettingsDTO userSettingsDTO) {
        User user = findUserByIdOrThrow(userId);
        UserSettings userSettings = user.getUserSettings();
        userSettings.setReadReceipts(userSettingsDTO.readReceipts());
        userSettings.setLastSeen(userSettingsDTO.lastSeen());
        userRepo.save(user);
    }
    // Profiles and settings end -------------------------------------------------------------------

    // Blocking start ------------------------------------------------------------------------------
    public Page<BlockedUserDTO> getBlockedUsers(UUID userId, Pageable pageable) {
        User user = findUserByIdOrThrow(userId);
        Page<BlockedUser> blockedUsers = blockedUserRepo.findAllByFromUser(user, pageable);
        return blockedUsers.map(DTOConverter::convertToDTO);
    }

    public LocalDateTime isBlockedUser(UUID userId, UUID isBlockedId) {
        User user = findUserByIdOrThrow(userId);
        User isBlockedUser = findUserByIdOrThrow(isBlockedId);
        Optional<BlockedUser> blockedUserOptional =
            blockedUserRepo.findByFromUserAndToUser(user, isBlockedUser);
        if (blockedUserOptional.isEmpty())
            return null;
        BlockedUser blockedUser = blockedUserOptional.get();
        return blockedUser.getCreated();
    }

    public void addBlockedUser(UUID userId, UUID blockUserId) {
        User user = findUserByIdOrThrow(userId);
        User blockUser = findUserByIdOrThrow(blockUserId);
        user.addBlockedUser(blockUser);
        userRepo.save(user);
    }

    public void removeBlockedUser(UUID userId, UUID unblockUserId) {
        User user = findUserByIdOrThrow(userId);
        User unblockUser = findUserByIdOrThrow(unblockUserId);
        if (user.isBlockedUser(unblockUser)) {
            user.removeBlockedUser(unblockUser);
            userRepo.save(user);
        } else {
            throw new IllegalUserInputException(
                user + " does not have " + unblockUser + " blocked."
            );
        }
    }
    // Blocking end --------------------------------------------------------------------------------
}
