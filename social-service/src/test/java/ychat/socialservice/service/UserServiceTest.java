package ychat.socialservice.service;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ychat.socialservice.model.chat.ChatMember;
import ychat.socialservice.model.chat.DirectChatMember;
import ychat.socialservice.model.chat.DirectChatMemberBuilder;
import ychat.socialservice.model.group.GroupMember;
import ychat.socialservice.model.group.GroupMemberBuilder;
import ychat.socialservice.model.user.*;
import ychat.socialservice.repository.BlockedUserRepository;
import ychat.socialservice.repository.ChatMemberRepository;
import ychat.socialservice.repository.ChatRepository;
import ychat.socialservice.repository.UserRepository;
import ychat.socialservice.service.dto.*;
import ychat.socialservice.util.IllegalUserInputException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepo;
    @Mock
    private BlockedUserRepository blockedUserRepo;
    @Mock
    private ChatRepository chatRepo;
    @Mock
    private ChatMemberRepository chatMemberRepo;
    @InjectMocks
    private UserService userService;

    @Test
    void FindUserByIdOrThrow_Exists_ReturnUser() {
        User user = new UserBuilder().build();

        when(userRepo.findById(eq(user.getId()))).thenReturn(Optional.of(user));

        User retUser = userService.findUserByIdOrThrow(user.getId());

        assertNotNull(retUser);
        assertEquals(user, retUser);

        verify(userRepo, times(1)).findById(eq(user.getId()));
        verifyNoMoreInteractions(userRepo, blockedUserRepo, chatRepo, chatMemberRepo);
    }

    @Test
    void FindUserByIdOrThrow_NotExists_Throws() {
        UUID userId = new UUID(0,0);

        when(userRepo.findById(eq(userId))).thenReturn(Optional.empty());

        assertThrows(
            EntityNotFoundException.class,
            () -> userService.findUserByIdOrThrow(userId)
        );

        verify(userRepo, times(1)).findById(eq(userId));
        verifyNoMoreInteractions(userRepo, blockedUserRepo, chatRepo, chatMemberRepo);
    }

    // User start ----------------------------------------------------------------------------------
    @Test
    void CreateUser_Exists_Throws() {
        UUID userId = new UUID(0,0);
        UserProfileDTO userProfileDTO = DTOConverter.convertToDTO(new UserProfileBuilder().build());

        when(userRepo.existsById(eq(userId))).thenReturn(true);

        assertThrows(
            EntityExistsException.class,
            () -> userService.createUser(userId, userProfileDTO)
        );

        verify(userRepo, times(1)).existsById(eq(userId));
        verifyNoMoreInteractions(userRepo, blockedUserRepo, chatRepo, chatMemberRepo);
    }

    @Test
    void CreateUser_NotExists_ReturnsUser() {
        UUID userId = new UUID(0, 0);
        UserProfileDTO userProfileDTO = DTOConverter.convertToDTO(new UserProfileBuilder().build());

        when(userRepo.existsById(eq(userId))).thenReturn(false);

        UserDTO userDTO = userService.createUser(userId, userProfileDTO);

        assertNotNull(userDTO);
        assertEquals(userId, userDTO.id());


        verify(userRepo, times(1)).existsById(eq(userId));
        verify(userRepo, times(1)).save(any(User.class));
        verifyNoMoreInteractions(userRepo, blockedUserRepo, chatRepo, chatMemberRepo);
    }

    @Test
    void GetUser_Valid_ReturnsUser() {
        User user = new UserBuilder().build();

        when(userRepo.findById(eq(user.getId()))).thenReturn(Optional.of(user));

        UserDTO userDTO = userService.getUser(user.getId());

        assertNotNull(userDTO);
        assertEquals(user.getId(), userDTO.id());

        verify(userRepo, times(1)).findById(eq(user.getId()));
        verifyNoMoreInteractions(userRepo, blockedUserRepo, chatRepo, chatMemberRepo);
    }

    // This would need to be extended to check whether the page iteration is correct
    @Test
    void DeleteUser_Valid_Success() {
        User fstUser = new UserBuilder().withId(new UUID(0,0)).build();
        User sndUser = new UserBuilder().withId(new UUID(0,1)).build();
        GroupMember groupMember = new GroupMemberBuilder().withUser(fstUser).build();
        DirectChatMember directChatMember =
            new DirectChatMemberBuilder().withFstuser(fstUser).withSndUser(sndUser).build();
        List<ChatMember> chats = new ArrayList<>();
        chats.add(groupMember);
        chats.add(directChatMember);
        Pageable pageable = PageRequest.of(0, UserService.MAX_BLOCKED_USER_PAGE_SIZE);

        when(userRepo.findById(eq(fstUser.getId()))).thenReturn(Optional.of(fstUser));
        when(chatMemberRepo.findAllByUserId(eq(fstUser.getId()), eq(pageable)))
                .thenReturn(new PageImpl<>(chats));

        userService.deleteUser(fstUser.getId());

        verify(userRepo, times(1)).findById(eq(fstUser.getId()));
        verify(chatMemberRepo, times(1))
            .findAllByUserId(eq(fstUser.getId()), eq(pageable));
        verify(chatRepo, times(1)).delete(eq(groupMember.getGroup()));
        verify(chatRepo, times(1)).flush();
        verify(userRepo, times(1)).delete(eq(fstUser));
        verifyNoMoreInteractions(userRepo, blockedUserRepo, chatRepo, chatMemberRepo);
    }
    // User end ------------------------------------------------------------------------------------

    // Profiles and settings start -----------------------------------------------------------------
    @Test
    void GetUserProfile_Valid_ReturnsUserProfile() {
        User user = new UserBuilder().build();

        when(userRepo.findById(eq(user.getId()))).thenReturn(Optional.of(user));

        UserProfileDTO userProfileDTO = userService.getUserProfile(user.getId());

        assertNotNull(userProfileDTO);
        assertEquals(user.getUserProfile().getFirstName(), userProfileDTO.firstName());
        assertEquals(user.getUserProfile().getLastName(), userProfileDTO.lastName());

        verify(userRepo, times(1)).findById(eq(user.getId()));
        verifyNoMoreInteractions(userRepo, blockedUserRepo, chatRepo, chatMemberRepo);
    }

    @Test
    void UpdateUserProfile_InvalidPartialUpdate_Throws() {
        UUID userId = new UUID(0,0);
        UserProfileDTO userProfileDTO = new UserProfileDTO(
            null, null, "newid",
            true, null
        );

        assertThrows(
            IllegalUserInputException.class,
            () -> userService.updateUserProfile(userId, userProfileDTO)
        );

        verifyNoInteractions(userRepo, blockedUserRepo, chatRepo, chatMemberRepo);
    }

    @Test
    void UpdateUserProfile_ValidPartialUpdate_ReturnsUserProfile() {
        User user = new UserBuilder().build();
        UserProfile userProfile = user.getUserProfile();
        userProfile.setFirstName("firstname");
        userProfile.setLastName("lastname");
        userProfile.setProfilePictureId("newid");
        UserProfileDTO userProfileDTO = new UserProfileDTO(
            "firstname_changed", null, null,
            true, null
        );

        when(userRepo.findById(eq(user.getId()))).thenReturn(Optional.of(user));

        UserProfileDTO retUserProfileDTO =
            userService.updateUserProfile(user.getId(), userProfileDTO);

        assertNotNull(retUserProfileDTO);
        assertEquals(retUserProfileDTO.firstName(), userProfileDTO.firstName());
        assertEquals(retUserProfileDTO.lastName(), userProfile.getLastName());
        assertNull(retUserProfileDTO.profilePictureId());

        verify(userRepo, times(1)).findById(eq(user.getId()));
        verifyNoMoreInteractions(userRepo, blockedUserRepo, chatRepo, chatMemberRepo);
    }

    @Test
    void GetUserSettings_Valid_ReturnsSettings() {
        User user = new UserBuilder().build();

        when(userRepo.findById(eq(user.getId()))).thenReturn(Optional.of(user));

        UserSettingsDTO userSettingsDTO = userService.getUserSettings(user.getId());

        assertNotNull(userSettingsDTO);
        assertEquals(user.getUserSettings().isReadReceipts(), userSettingsDTO.readReceipts());
        assertEquals(user.getUserSettings().isLastSeen(), userSettingsDTO.lastSeen());

        verify(userRepo, times(1)).findById(eq(user.getId()));
        verifyNoMoreInteractions(userRepo, blockedUserRepo, chatRepo, chatMemberRepo);
    }

    @Test
    void UpdateUserSettings_ValidPartialUpdate_ReturnsSettings() {
        User user = new UserBuilder().build();
        UserSettings userSettings = user.getUserSettings();
        userSettings.setReadReceipts(true);
        userSettings.setLastSeen(true);
        UserSettingsDTO userSettingsDTO = new UserSettingsDTO(null, false);

        when(userRepo.findById(eq(user.getId()))).thenReturn(Optional.of(user));

        UserSettingsDTO retUserSettingsDTO =
            userService.updateUserSettings(user.getId(), userSettingsDTO);

        assertNotNull(retUserSettingsDTO);
        assertEquals(retUserSettingsDTO.readReceipts(), userSettings.isReadReceipts());
        assertEquals(retUserSettingsDTO.lastSeen(), userSettingsDTO.lastSeen());

        verify(userRepo, times(1)).findById(eq(user.getId()));
        verifyNoMoreInteractions(userRepo, blockedUserRepo, chatRepo, chatMemberRepo);
    }
    // Profiles and settings end -------------------------------------------------------------------

    // Blocking start ------------------------------------------------------------------------------
    @Test
    void GetBlockedUsers_PageSizeTooLarge_Throws() {
        UUID id = UUID.randomUUID();
        int tooLargePageSize = UserService.MAX_BLOCKED_USER_PAGE_SIZE + 1;
        Pageable pageable = PageRequest.of(0,tooLargePageSize);

        assertThrows(
            IllegalUserInputException.class,
            () -> userService.getBlockedUsers(id, pageable)
        );

        verifyNoInteractions(userRepo, blockedUserRepo, chatRepo, chatMemberRepo);
    }

    @Test
    void GetBlockedUsers_Unpaged_Throws() {
        UUID id = UUID.randomUUID();
        Pageable unpaged = Pageable.unpaged();

        assertThrows(
            IllegalUserInputException.class,
            () -> userService.getBlockedUsers(id, unpaged)
        );

        verifyNoInteractions(userRepo, blockedUserRepo, chatRepo, chatMemberRepo);
    }

    @Test
    void GetBlockedUsers_Paged_ReturnsBlockedUsers() {
        BlockedUser blockedUser = new BlockedUserBuilder().build();
        List<BlockedUser> blockedUsers = new ArrayList<>();
        blockedUsers.add(blockedUser);
        User fromUser = blockedUser.getFromUser();
        User toUser = blockedUser.getToUser();
        Pageable pageable = PageRequest.of(0, UserService.MAX_BLOCKED_USER_PAGE_SIZE);

        when(blockedUserRepo.findAllByFromUserId(eq(fromUser.getId()), eq(pageable)))
            .thenReturn(new PageImpl<>(blockedUsers));

        Page<BlockedUserDTO> blockedUsersPage =
            userService.getBlockedUsers(fromUser.getId(), pageable);

        assertNotNull(blockedUsersPage);
        List<BlockedUserDTO> blockedUserDTOs = blockedUsersPage.getContent();
        assertEquals(1, blockedUserDTOs.size());
        for (BlockedUserDTO blockedUserDTO : blockedUserDTOs) {
            assertEquals(toUser.getId(), blockedUserDTO.id());
        }

        verify(blockedUserRepo, times(1))
            .findAllByFromUserId(eq(fromUser.getId()), eq(pageable));
        verifyNoMoreInteractions(userRepo, blockedUserRepo, chatRepo, chatMemberRepo);
    }

    @Test
    void IsBlockedUser_UserIsBlocked_ReturnsTimestamp() {
        BlockedUser blockedUser = new BlockedUserBuilder().build();
        User fromUser = blockedUser.getFromUser();
        User toUser = blockedUser.getToUser();

        when(blockedUserRepo.findByFromUserIdAndToUserId(eq(fromUser.getId()), eq(toUser.getId())))
            .thenReturn(Optional.of(blockedUser));

        LocalDateTime blockedAt = userService.isBlockedUser(fromUser.getId(), toUser.getId());

        assertNotNull(blockedAt);
        assertEquals(blockedAt, blockedUser.getCreated());

        verify(blockedUserRepo, times(1))
            .findByFromUserIdAndToUserId(eq(fromUser.getId()), eq(toUser.getId()));
        verifyNoMoreInteractions(userRepo, blockedUserRepo, chatRepo, chatMemberRepo);
    }

    @Test
    void IsBlockedUser_UserIsNotBlocked_ReturnsNull() {
        UUID fromUserId = new UUID(0,0);
        UUID toUserId = new UUID(0,1);

        when(blockedUserRepo.findByFromUserIdAndToUserId(eq(fromUserId), eq(toUserId)))
            .thenReturn(Optional.empty());

        LocalDateTime blockedAt = userService.isBlockedUser(fromUserId, toUserId);

        assertNull(blockedAt);

        verify(blockedUserRepo, times(1))
            .findByFromUserIdAndToUserId(eq(fromUserId), eq(toUserId));
        verifyNoMoreInteractions(userRepo, blockedUserRepo, chatRepo, chatMemberRepo);
    }

    @Test
    void AddBlockedUser_SameId_Throws() {
        UUID id = new UUID(0,0);

        assertThrows(
            IllegalUserInputException.class,
            () -> userService.addBlockedUser(id, id)
        );

        verifyNoInteractions(userRepo, blockedUserRepo, chatRepo, chatMemberRepo);
    }

    @Test
    void AddBlockedUser_UserIsBlocked_Throws() {
        User fromUser = new UserBuilder().withId(new UUID(0,0)).build();
        User toUser = new UserBuilder().withId(new UUID(0,1)).build();
        fromUser.addBlockedUser(toUser);

        when(userRepo.findById(eq(fromUser.getId()))).thenReturn(Optional.of(fromUser));
        when(userRepo.findById(eq(toUser.getId()))).thenReturn(Optional.of(toUser));

        assertThrows(
            IllegalUserInputException.class,
            () -> userService.addBlockedUser(fromUser.getId(), toUser.getId())
        );

        verify(userRepo, times(1)).findById(eq(fromUser.getId()));
        verify(userRepo, times(1)).findById(eq(toUser.getId()));
        verifyNoMoreInteractions(userRepo, blockedUserRepo, chatRepo, chatMemberRepo);
    }

    @Test
    void AddBlockedUser_UserIsNotBlocked_Success() {
        User fromUser = new UserBuilder().withId(new UUID(0,0)).build();
        User toUser = new UserBuilder().withId(new UUID(0,1)).build();

        when(userRepo.findById(eq(fromUser.getId()))).thenReturn(Optional.of(fromUser));
        when(userRepo.findById(eq(toUser.getId()))).thenReturn(Optional.of(toUser));

        BlockedUserDTO blockeduserDTO =
            userService.addBlockedUser(fromUser.getId(), toUser.getId());

        assertNotNull(blockeduserDTO);
        assertEquals(blockeduserDTO.id(), toUser.getId());

        verify(userRepo, times(1)).findById(eq(fromUser.getId()));
        verify(userRepo, times(1)).findById(eq(toUser.getId()));
        verifyNoMoreInteractions(userRepo, blockedUserRepo, chatRepo, chatMemberRepo);
    }

    @Test
    void RemoveBlockedUser_UserIsNotBlocked_Throws() {
        User fromUser = new UserBuilder().withId(new UUID(0,0)).build();
        User toUser = new UserBuilder().withId(new UUID(0,1)).build();

        when(userRepo.findById(eq(fromUser.getId()))).thenReturn(Optional.of(fromUser));
        when(userRepo.findById(eq(toUser.getId()))).thenReturn(Optional.of(toUser));

        assertThrows(
            IllegalUserInputException.class,
            () -> userService.removeBlockedUser(fromUser.getId(), toUser.getId())
        );

        verify(userRepo, times(1)).findById(eq(fromUser.getId()));
        verify(userRepo, times(1)).findById(eq(toUser.getId()));
        verifyNoMoreInteractions(userRepo, blockedUserRepo, chatRepo, chatMemberRepo);
    }

    @Test
    void RemoveBlockedUser_UserIsBlocked_Success() {
        User fromUser = new UserBuilder().withId(new UUID(0,0)).build();
        User toUser = new UserBuilder().withId(new UUID(0,1)).build();
        fromUser.addBlockedUser(toUser);

        when(userRepo.findById(eq(fromUser.getId()))).thenReturn(Optional.of(fromUser));
        when(userRepo.findById(eq(toUser.getId()))).thenReturn(Optional.of(toUser));

        userService.removeBlockedUser(fromUser.getId(), toUser.getId());

        assertFalse(fromUser.isBlockedUser(toUser));

        verify(userRepo, times(1)).findById(eq(fromUser.getId()));
        verify(userRepo, times(1)).findById(eq(toUser.getId()));
        verifyNoMoreInteractions(userRepo, blockedUserRepo, chatRepo, chatMemberRepo);
    }
    // Blocking end --------------------------------------------------------------------------------
}