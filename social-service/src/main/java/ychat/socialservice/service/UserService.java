package ychat.socialservice.service;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.NotNull;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import ychat.socialservice.model.chat.Chat;
import ychat.socialservice.model.chat.ChatMember;
import ychat.socialservice.model.util.CreateDTO;
import ychat.socialservice.model.util.UpdateDTO;
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
@Validated
@Service
@Transactional(readOnly = true)
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

    // User start ----------------------------------------------------------------------------------
    @Transactional
    public UserDTO createUser(
        @NotNull UUID userId,
        @NotNull @Validated(CreateDTO.class) UserProfileDTO userProfileDTO
    ) {
        if (userRepo.existsById(userId))
            throw new EntityExistsException("User exists already: " + userId);
        UserProfile userProfile = DTOConverter.convertToEntity(userProfileDTO);
        User user = new User(userId, userProfile);
        userRepo.save(user);
        return DTOConverter.convertToDTO(user);
    }

    public UserDTO getUser(@NotNull UUID userId) {
        User user = findUserByIdOrThrow(userId);
        return DTOConverter.convertToDTO(user);
    }

    @Transactional
    public void deleteUser(@NotNull UUID userId) {
        User user = findUserByIdOrThrow(userId);
        Pageable pageable = PageRequest.of(0, ChatService.MAX_CHAT_MEMBER_PAGE_SIZE);
        Page<ChatMember> chatMembers;
        do {
            chatMembers = chatMemberRepo.findAllByUserId(userId, pageable);
            for (ChatMember chatMember : chatMembers) {
                Chat chat = chatMember.getChat();
                if (chat.toDeleteIfUserRemoved(user)) {
                    // Cascade deletes the chat members as well
                    chatRepo.delete(chat);
                } else {
                    chat.removeMember(user);
                }
            }
            pageable = pageable.next();
        } while (chatMembers.hasNext());
        // Deletes the blocked user entries via cascading
        chatRepo.flush(); // Needs to happen to not have problems with foreign keys
        userRepo.delete(user);
    }
    // User end ------------------------------------------------------------------------------------

    // Profiles and settings start -----------------------------------------------------------------
    public UserProfileDTO getUserProfile(@NotNull UUID userId) {
        User user = findUserByIdOrThrow(userId);
        return DTOConverter.convertToDTO(user.getUserProfile());
    }

    @Transactional
    public UserProfileDTO updateUserProfile(
        @NotNull UUID userId,
        @NotNull @Validated(UpdateDTO.class) UserProfileDTO userProfileDTO
    ) {
        if (userProfileDTO.removeProfilePictureId() != null
                && userProfileDTO.profilePictureId() != null) {
            throw new IllegalUserInputException(
                "It is not allowed to set both profilePictureId and removeProfilePicture."
            );
        }
        User user = findUserByIdOrThrow(userId);
        UserProfile userProfile = user.getUserProfile();
        userProfile.setFirstName(userProfileDTO.firstName());
        userProfile.setLastName(userProfileDTO.lastName());
        if (userProfileDTO.removeProfilePictureId() != null
                && userProfileDTO.removeProfilePictureId())
            userProfile.removeProfilePictureId();
        else
            userProfile.setProfilePictureId(userProfileDTO.profilePictureId());
        userProfile.setProfileDescription(userProfileDTO.profileDescription());
        return DTOConverter.convertToDTO(user.getUserProfile());
    }

    public UserSettingsDTO getUserSettings(@NotNull UUID userId) {
        User user = findUserByIdOrThrow(userId);
        return DTOConverter.convertToDTO(user.getUserSettings());
    }

    @Transactional
    public UserSettingsDTO updateUserSettings(
        @NotNull UUID userId,
        @NotNull @Validated(UpdateDTO.class) UserSettingsDTO userSettingsDTO
    ) {
        User user = findUserByIdOrThrow(userId);
        UserSettings userSettings = user.getUserSettings();
        userSettings.setReadReceipts(userSettingsDTO.readReceipts());
        userSettings.setLastSeen(userSettingsDTO.lastSeen());
        return DTOConverter.convertToDTO(user.getUserSettings());
    }
    // Profiles and settings end -------------------------------------------------------------------

    // Blocking start ------------------------------------------------------------------------------
    public Page<BlockedUserDTO> getBlockedUsers(@NotNull UUID userId, @NotNull Pageable pageable) {
        if (pageable.isUnpaged() || pageable.getPageSize() > MAX_BLOCKED_USER_PAGE_SIZE) {
            throw new IllegalUserInputException(
                "Get blocked user request must be paged with maximum page size: "
                + MAX_BLOCKED_USER_PAGE_SIZE + "."
            );
        }
        Page<BlockedUser> blockedUsers = blockedUserRepo.findAllByFromUserId(userId, pageable);
        return blockedUsers.map(DTOConverter::convertToDTO);
    }

    public LocalDateTime isBlockedUser(@NotNull UUID userId, @NotNull UUID isBlockedId) {
        Optional<BlockedUser> blockedUserOptional =
            blockedUserRepo.findByFromUserIdAndToUserId(userId, isBlockedId);
        if (blockedUserOptional.isEmpty())
            return null;
        BlockedUser blockedUser = blockedUserOptional.get();
        return blockedUser.getCreated();
    }

    @Transactional
    public BlockedUserDTO addBlockedUser(@NotNull UUID userId, @NotNull UUID blockUserId) {
        if (userId.equals(blockUserId))
            throw new IllegalUserInputException("User cannot block themselves: " + userId);
        User user = findUserByIdOrThrow(userId);
        User blockUser = findUserByIdOrThrow(blockUserId);
        BlockedUser blockedUser = user.addBlockedUser(blockUser);
        if (blockedUser == null) {
            throw new IllegalUserInputException(
                user + " already has " + blockUserId + " blocked."
            );
        }
        return DTOConverter.convertToDTO(blockedUser);
    }

    @Transactional
    public void removeBlockedUser(@NotNull UUID userId, @NotNull UUID unblockUserId) {
        User user = findUserByIdOrThrow(userId);
        User unblockUser = findUserByIdOrThrow(unblockUserId);
        if (!user.isBlockedUser(unblockUser)) {
            throw new IllegalUserInputException(
                user + " does not have " + unblockUser + " blocked."
            );
        }
        user.removeBlockedUser(unblockUser);
    }
    // Blocking end --------------------------------------------------------------------------------
}
