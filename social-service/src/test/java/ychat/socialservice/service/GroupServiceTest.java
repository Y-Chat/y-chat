package ychat.socialservice.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ychat.socialservice.model.group.*;
import ychat.socialservice.model.user.User;
import ychat.socialservice.model.user.UserBuilder;
import ychat.socialservice.repository.GroupMemberRepository;
import ychat.socialservice.repository.GroupRepository;
import ychat.socialservice.service.dto.*;
import ychat.socialservice.util.IllegalUserInputException;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class GroupServiceTest {
    @Mock
    private UserService userService;
    @Mock
    private GroupRepository groupRepo;
    @Mock
    private GroupMemberRepository groupMemberRepo;
    @InjectMocks
    private GroupService groupService;

    @Test
    void FindGroupMemberByIdsOrThrow_NotExists_Throws() {
        UUID userId = new UUID(0,0);
        UUID groupId = new UUID(0,1);

        when(groupMemberRepo.findByUserIdAndChatId(eq(userId), eq(groupId)))
            .thenReturn(Optional.empty());

        assertThrows(
            IllegalUserInputException.class,
            () -> groupService.findGroupMemberByIdsOrThrow(userId, groupId)
        );

        verify(groupMemberRepo, times(1))
            .findByUserIdAndChatId(eq(userId), eq(groupId));
        verifyNoMoreInteractions(userService, groupRepo, groupMemberRepo);
    }

    @Test
    void FindGroupMemberByIdsOrThrow_Exists_ReturnsGroupMember() {
        GroupMember groupMember = new GroupMemberBuilder().build();
        User user = groupMember.getUser();
        Group group = groupMember.getGroup();

        when(groupMemberRepo.findByUserIdAndChatId(eq(user.getId()), eq(group.getId())))
            .thenReturn(Optional.of(groupMember));

        GroupMember retGroupMember =
            groupService.findGroupMemberByIdsOrThrow(user.getId(), group.getId());

        assertEquals(groupMember, retGroupMember);

        verify(groupMemberRepo, times(1))
                .findByUserIdAndChatId(eq(user.getId()), eq(group.getId()));
        verifyNoMoreInteractions(userService, groupRepo, groupMemberRepo);
    }

    // Group start ---------------------------------------------------------------------------------
    @Test
    void GetGroup_Member_ReturnGroup() {
        GroupMember groupMember = new GroupMemberBuilder().build();
        Group group = groupMember.getGroup();
        User user = groupMember.getUser();

        when(groupMemberRepo.findByUserIdAndChatId(eq(user.getId()), eq(group.getId())))
            .thenReturn(Optional.of(groupMember));

        GroupDTO groupDTO = groupService.getGroup(group.getId(), user.getId());

        assertNotNull(groupDTO);
        assertEquals(groupDTO.id(), group.getId());

        verify(groupMemberRepo, times(1))
            .findByUserIdAndChatId(eq(user.getId()), eq(group.getId()));
        verifyNoMoreInteractions(userService, groupRepo, groupMemberRepo);
    }

    @Test
    void CreateGroup_ReturnsGroup() {
        User user = new UserBuilder().build();
        GroupProfile groupProfile = new GroupProfileBuilder().build();
        GroupProfileDTO groupProfileDTO = DTOConverter.convertToDTO(groupProfile);

        when(userService.findUserByIdOrThrow((eq(user.getId())))).thenReturn(user);

        GroupDTO groupDTO = groupService.createGroup(user.getId(), groupProfileDTO);

        assertNotNull(groupDTO);
        assertEquals(groupDTO.groupProfileDTO().groupName(), groupProfileDTO.groupName());

        verify(userService, times(1))
            .findUserByIdOrThrow(eq(user.getId()));
        verify(groupRepo, times(1))
            .save(any(Group.class));
        verifyNoMoreInteractions(userService, groupRepo, groupMemberRepo);
    }
    // Group end -----------------------------------------------------------------------------------

    // Profile start -------------------------------------------------------------------------------
    @Test
    void GetGroupProfile_Member_ReturnsGroupProfile() {
        GroupMember groupMember = new GroupMemberBuilder().build();
        User user = groupMember.getUser();
        Group group = groupMember.getGroup();

        when(groupMemberRepo.findByUserIdAndChatId(eq(user.getId()), eq(group.getId())))
            .thenReturn(Optional.of(groupMember));

        GroupProfileDTO groupProfileDTO = groupService.getGroupProfile(group.getId(), user.getId());

        assertNotNull(groupProfileDTO);
        assertEquals(groupProfileDTO.groupName(), group.getGroupProfile().getGroupName());

        verify(groupMemberRepo, times(1))
            .findByUserIdAndChatId(eq(user.getId()), eq(group.getId()));
        verifyNoMoreInteractions(userService, groupRepo, groupMemberRepo);
    }

    @Test
    void GetGroupProfile_NotMember_Throws() {
        UUID userId = new UUID(0,0);
        UUID groupId = new UUID(0,1);

        when(groupMemberRepo.findByUserIdAndChatId(eq(userId), eq(groupId)))
            .thenReturn(Optional.empty());

        assertThrows(
            IllegalUserInputException.class,
            () -> groupService.getGroupProfile(groupId, userId)
        );

        verify(groupMemberRepo, times(1))
            .findByUserIdAndChatId(eq(userId), eq(groupId));
        verifyNoMoreInteractions(userService, groupRepo, groupMemberRepo);
    }

    @Test
    void UpdateGroupProfile_InvalidPartialUpdate_Throws() {
        UUID userId = new UUID(0,0);
        UUID groupId = new UUID(0,1);
        GroupProfileDTO groupProfileDTO = new GroupProfileDTO(
            null, "newId",
            true, null
        );

        assertThrows(
            IllegalUserInputException.class,
            () -> groupService.updateGroupProfile(groupId, groupProfileDTO, userId)
        );

        verifyNoMoreInteractions(userService, groupRepo, groupMemberRepo);
    }

    @Test
    void UpdateGroupProfile_ValidPartialUpdate_ReturnsGroupProfile() {
        GroupMember groupMember = new GroupMemberBuilder().build();
        User user = groupMember.getUser();
        Group group = groupMember.getGroup();
        GroupProfile groupProfile = group.getGroupProfile();
        groupProfile.setGroupName("group_name");
        groupProfile.setProfilePictureId("oldid");
        GroupProfileDTO groupProfileDTO = new GroupProfileDTO(
            "group_name_changed", "updatedid",
            null, null
        );

        when(groupMemberRepo.findByUserIdAndChatId(eq(user.getId()), eq(group.getId())))
                .thenReturn(Optional.of(groupMember));

        GroupProfileDTO retGroupProfileDTO =
            groupService.updateGroupProfile(group.getId(), groupProfileDTO, user.getId());

        assertNotNull(retGroupProfileDTO);
        assertEquals(retGroupProfileDTO.groupName(), groupProfile.getGroupName());
        assertEquals(retGroupProfileDTO.profilePictureId(), groupProfileDTO.profilePictureId());

        verify(groupMemberRepo, times(1))
                .findByUserIdAndChatId(eq(user.getId()), eq(group.getId()));
        verifyNoMoreInteractions(userService, groupRepo, groupMemberRepo);
    }
    // Profile end ---------------------------------------------------------------------------------

    // Members start -------------------------------------------------------------------------------
    @Test
    void AddGroupMember_NotAdmin_Throws() {
        User user = new UserBuilder().withId(new UUID(0, 1)).build();
        User requestUser = new UserBuilder().withId(new UUID(0, 2)).build();
        Group group = new GroupBuilder().withInitUser(user).build();
        GroupMember groupMember = group.addGroupMember(requestUser);

        when(groupMemberRepo.findByUserIdAndChatId(eq(requestUser.getId()), eq(group.getId())))
            .thenReturn(Optional.of(groupMember));

        assertThrows(
            IllegalUserInputException.class,
            () -> groupService.addGroupMember(group.getId(), user.getId(), requestUser.getId())
        );

        verify(groupMemberRepo, times(1))
            .findByUserIdAndChatId(eq(requestUser.getId()), eq(group.getId()));
        verifyNoMoreInteractions(userService, groupRepo, groupMemberRepo);
    }

    @Test
    void AddGroupMember_AlreadyMember_Throws() {
        User requestUser = new UserBuilder().withId(new UUID(0, 1)).build();
        User user = new UserBuilder().withId(new UUID(0, 2)).build();
        GroupMember groupMember = new GroupMemberBuilder().withUser(requestUser).build();
        groupMember.setGroupRole(GroupRole.GROUP_ADMIN);
        Group group = groupMember.getGroup();
        group.addGroupMember(user);

        when(groupMemberRepo.findByUserIdAndChatId(eq(requestUser.getId()), eq(group.getId())))
            .thenReturn(Optional.of(groupMember));
        when(userService.findUserByIdOrThrow((eq(user.getId())))).thenReturn(user);

        assertThrows(
            IllegalUserInputException.class,
            () -> groupService.addGroupMember(group.getId(), user.getId(), requestUser.getId())
        );

        verify(groupMemberRepo, times(1))
                .findByUserIdAndChatId(eq(requestUser.getId()), eq(group.getId()));
        verify(userService, times(1))
            .findUserByIdOrThrow(eq(user.getId()));
        verifyNoMoreInteractions(userService, groupRepo, groupMemberRepo);
    }

    @Test
    void AddGroupMember_NotAlreadyMember_ReturnChatMember() {
        User requestUser = new UserBuilder().withId(new UUID(0, 1)).build();
        User user = new UserBuilder().withId(new UUID(0, 2)).build();
        GroupMember groupMember = new GroupMemberBuilder().withUser(requestUser).build();
        groupMember.setGroupRole(GroupRole.GROUP_ADMIN);
        Group group = groupMember.getGroup();

        when(groupMemberRepo.findByUserIdAndChatId(eq(requestUser.getId()), eq(group.getId())))
                .thenReturn(Optional.of(groupMember));
        when(userService.findUserByIdOrThrow((eq(user.getId())))).thenReturn(user);

        ChatMemberDTO chatMemberDTO =
            groupService.addGroupMember(group.getId(), user.getId(), requestUser.getId());

        assertNotNull(chatMemberDTO);
        assertEquals(chatMemberDTO.userId(), user.getId());
        assertEquals(chatMemberDTO.groupRole(), GroupRole.GROUP_MEMBER);

        verify(groupMemberRepo, times(1))
                .findByUserIdAndChatId(eq(requestUser.getId()), eq(group.getId()));
        verify(userService, times(1))
                .findUserByIdOrThrow(eq(user.getId()));
        verifyNoMoreInteractions(userService, groupRepo, groupMemberRepo);
    }

    @Test
    void RemoveGroupMember_NotAdmin_Throws() {
        User requestUser = new UserBuilder().withId(new UUID(0, 1)).build();
        User user = new UserBuilder().withId(new UUID(0, 2)).build();
        Group group = new GroupBuilder().withInitUser(user).build();
        GroupMember groupMember = group.addGroupMember(requestUser);

        when(groupMemberRepo.findByUserIdAndChatId(eq(requestUser.getId()), eq(group.getId())))
            .thenReturn(Optional.of(groupMember));

        assertThrows(
            IllegalUserInputException.class,
            () -> groupService.removeGroupMember(group.getId(), user.getId(), requestUser.getId())
        );

        verify(groupMemberRepo, times(1))
            .findByUserIdAndChatId(eq(requestUser.getId()), eq(group.getId()));
        verifyNoMoreInteractions(userService, groupRepo, groupMemberRepo);
    }

    @Test
    void RemoveGroupMember_NotGroupMember_Throws() {
        User requestUser = new UserBuilder().withId(new UUID(0, 1)).build();
        User user = new UserBuilder().withId(new UUID(0, 2)).build();
        GroupMember groupMember = new GroupMemberBuilder().withUser(requestUser).build();
        groupMember.setGroupRole(GroupRole.GROUP_ADMIN);
        Group group = groupMember.getGroup();

        when(groupMemberRepo.findByUserIdAndChatId(eq(requestUser.getId()), eq(group.getId())))
                .thenReturn(Optional.of(groupMember));
        when(userService.findUserByIdOrThrow((eq(user.getId())))).thenReturn(user);

        assertThrows(
            IllegalUserInputException.class,
            () -> groupService.removeGroupMember(group.getId(), user.getId(), requestUser.getId())
        );

        verify(groupMemberRepo, times(1))
                .findByUserIdAndChatId(eq(requestUser.getId()), eq(group.getId()));
        verify(userService, times(1))
                .findUserByIdOrThrow(eq(user.getId()));
        verifyNoMoreInteractions(userService, groupRepo, groupMemberRepo);
    }

    @Test
    void RemoveGroupMember_MultipleGroupMemberAndLastAdmin_RemoveAndPromote() {
        User requestUser = new UserBuilder().withId(new UUID(0, 1)).build();
        User user = new UserBuilder().withId(new UUID(0, 2)).build();
        GroupMember groupMember = new GroupMemberBuilder().withUser(requestUser).build();
        groupMember.setGroupRole(GroupRole.GROUP_ADMIN);
        Group group = groupMember.getGroup();
        group.addGroupMember(user);

        when(groupMemberRepo.findByUserIdAndChatId(eq(requestUser.getId()), eq(group.getId())))
            .thenReturn(Optional.of(groupMember));
        when(userService.findUserByIdOrThrow((eq(requestUser.getId())))).thenReturn(requestUser);

        groupService.removeGroupMember(group.getId(), requestUser.getId(), requestUser.getId());

        assertFalse(group.isMember(requestUser));
        assertTrue(group.isMember(user));
        assertEquals(group.getNumberOfAdmins(), 1);

        verify(groupMemberRepo, times(1))
            .findByUserIdAndChatId(eq(requestUser.getId()), eq(group.getId()));
        verify(userService, times(1))
            .findUserByIdOrThrow(eq(requestUser.getId()));
        verifyNoMoreInteractions(userService, groupRepo, groupMemberRepo);
    }

    @Test
    void RemoveGroupMember_SingleGroupMember_DeleteGroup() {
        User user = new UserBuilder().withId(new UUID(0, 1)).build();
        GroupMember groupMember = new GroupMemberBuilder().withUser(user).build();
        groupMember.setGroupRole(GroupRole.GROUP_ADMIN);
        Group group = groupMember.getGroup();

        when(groupMemberRepo.findByUserIdAndChatId(eq(user.getId()), eq(group.getId())))
            .thenReturn(Optional.of(groupMember));
        when(userService.findUserByIdOrThrow((eq(user.getId())))).thenReturn(user);

        groupService.removeGroupMember(group.getId(), user.getId(), user.getId());

        verify(groupMemberRepo, times(1))
            .findByUserIdAndChatId(eq(user.getId()), eq(group.getId()));
        verify(userService, times(1))
            .findUserByIdOrThrow(eq(user.getId()));
        verify(groupRepo, times(1))
            .delete(eq(group));
        verifyNoMoreInteractions(userService, groupRepo, groupMemberRepo);
    }

    @Test
    void GetGroupRole_RequestNotMember_Throws() {
        UUID requestUserId = new UUID(0,0);
        UUID userId = new UUID(0,1);
        UUID groupId = new UUID(0,2);

        when(groupMemberRepo.findByUserIdAndChatId(eq(requestUserId), eq(groupId)))
                .thenReturn(Optional.empty());

        assertThrows(
            IllegalUserInputException.class,
            () -> groupService.getGroupRole(groupId, userId, requestUserId)
        );

        verify(groupMemberRepo, times(1))
            .findByUserIdAndChatId(eq(requestUserId), eq(groupId));
        verifyNoMoreInteractions(userService, groupRepo, groupMemberRepo);
    }

    @Test
    void GetGroupRole_UserNotMember_ReturnsNotAMember() {
        User requestUser = new UserBuilder().withId(new UUID(0, 1)).build();
        User user = new UserBuilder().withId(new UUID(0, 2)).build();
        GroupMember requestGroupMember = new GroupMemberBuilder().withUser(requestUser).build();
        Group group = requestGroupMember.getGroup();

        when(groupMemberRepo.findByUserIdAndChatId(eq(requestUser.getId()), eq(group.getId())))
            .thenReturn(Optional.of(requestGroupMember));
        when(groupMemberRepo.findByUserIdAndChatId(eq(user.getId()), eq(group.getId())))
            .thenReturn(Optional.empty());

        GroupRole groupRole =
            groupService.getGroupRole(group.getId(), user.getId(), requestUser.getId());

        assertNotNull(groupRole);
        assertEquals(groupRole, GroupRole.NOT_A_MEMBER);

        verify(groupMemberRepo, times(1))
            .findByUserIdAndChatId(eq(requestUser.getId()), eq(group.getId()));
        verify(groupMemberRepo, times(1))
            .findByUserIdAndChatId(eq(user.getId()), eq(group.getId()));
        verifyNoMoreInteractions(userService, groupRepo, groupMemberRepo);
    }

    @Test
    void GetGroupRole_BothMember_ReturnsGroupRole() {
        User requestUser = new UserBuilder().withId(new UUID(0, 1)).build();
        User user = new UserBuilder().withId(new UUID(0, 2)).build();
        GroupMember requestGroupMember = new GroupMemberBuilder().withUser(requestUser).build();
        Group group = requestGroupMember.getGroup();
        GroupMember groupMember = group.addGroupMember(user);

        when(groupMemberRepo.findByUserIdAndChatId(eq(requestUser.getId()), eq(group.getId())))
            .thenReturn(Optional.of(requestGroupMember));
        when(groupMemberRepo.findByUserIdAndChatId(eq(user.getId()), eq(group.getId())))
            .thenReturn(Optional.of(groupMember));

        GroupRole groupRole =
            groupService.getGroupRole(group.getId(), user.getId(), requestUser.getId());

        assertNotNull(groupRole);
        assertEquals(groupRole, groupMember.getGroupRole());

        verify(groupMemberRepo, times(1))
            .findByUserIdAndChatId(eq(requestUser.getId()), eq(group.getId()));
        verify(groupMemberRepo, times(1))
            .findByUserIdAndChatId(eq(user.getId()), eq(group.getId()));
        verifyNoMoreInteractions(userService, groupRepo, groupMemberRepo);
    }

    @Test
    void UpdateGroupRole_ToNotAMember_Throws() {
        UUID groupId = new UUID(0,0);
        UUID userId = new UUID(0,1);
        UUID requestUserId = new UUID(0,2);

        assertThrows(
            IllegalUserInputException.class,
            () -> groupService.updateGroupRole(
                groupId, userId, GroupRole.NOT_A_MEMBER, requestUserId
            )
        );

        verifyNoMoreInteractions(userService, groupRepo, groupMemberRepo);
    }

    @Test
    void UpdateGroupRole_NotAdmin_Throws() {
        User requestUser = new UserBuilder().withId(new UUID(0, 1)).build();
        User user = new UserBuilder().withId(new UUID(0, 2)).build();
        Group group = new GroupBuilder().withInitUser(user).build();
        GroupMember groupMember = group.addGroupMember(requestUser);

        when(groupMemberRepo.findByUserIdAndChatId(eq(requestUser.getId()), eq(group.getId())))
            .thenReturn(Optional.of(groupMember));

        assertThrows(
            IllegalUserInputException.class,
            () -> groupService.updateGroupRole(
                group.getId(), user.getId(), GroupRole.GROUP_ADMIN, requestUser.getId()
            )
        );

        verify(groupMemberRepo, times(1))
            .findByUserIdAndChatId(eq(requestUser.getId()), eq(group.getId()));
        verifyNoMoreInteractions(userService, groupRepo, groupMemberRepo);
    }

    @Test
    void UpdateGroupRole_Admin_ReturnsGroupRole() {
        User requestUser = new UserBuilder().withId(new UUID(0, 1)).build();
        User user = new UserBuilder().withId(new UUID(0, 2)).build();
        GroupMember requestGroupMember = new GroupMemberBuilder().withUser(requestUser).build();
        Group group = requestGroupMember.getGroup();
        GroupMember groupMember = group.addGroupMember(user);
        groupMember.setGroupRole(GroupRole.GROUP_MEMBER);

        when(groupMemberRepo.findByUserIdAndChatId(eq(requestUser.getId()), eq(group.getId())))
            .thenReturn(Optional.of(requestGroupMember));
        when(groupMemberRepo.findByUserIdAndChatId(eq(user.getId()), eq(group.getId())))
            .thenReturn(Optional.of(groupMember));

        GroupRole groupRole = groupService.updateGroupRole(
            group.getId(), user.getId(), GroupRole.GROUP_ADMIN, requestUser.getId()
        );

        assertNotNull(groupRole);
        assertEquals(groupRole, GroupRole.GROUP_ADMIN);

        verify(groupMemberRepo, times(1))
            .findByUserIdAndChatId(eq(requestUser.getId()), eq(group.getId()));
        verify(groupMemberRepo, times(1))
            .findByUserIdAndChatId(eq(user.getId()), eq(group.getId()));
        verifyNoMoreInteractions(userService, groupRepo, groupMemberRepo);
    }

    @Test
    void UpdateGroupRole_LastAdmin_Throws() {
        User user = new UserBuilder().build();
        GroupMember groupMember = new GroupMemberBuilder().withUser(user).build();
        Group group = groupMember.getGroup();

        when(groupMemberRepo.findByUserIdAndChatId(eq(user.getId()), eq(group.getId())))
            .thenReturn(Optional.of(groupMember));

        assertThrows(
            IllegalUserInputException.class,
            () -> groupService.updateGroupRole(
                group.getId(), user.getId(), GroupRole.GROUP_MEMBER, user.getId()
            )
        );

        verify(groupMemberRepo, times(2))
            .findByUserIdAndChatId(eq(user.getId()), eq(group.getId()));
        verifyNoMoreInteractions(userService, groupRepo, groupMemberRepo);
    }
    // Members end ---------------------------------------------------------------------------------
}