package ychat.socialservice.service;

import com.google.firebase.auth.FirebaseAuthException;
import jakarta.validation.constraints.NotNull;
import lombok.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import ychat.socialservice.model.group.Group;
import ychat.socialservice.model.group.GroupMember;
import ychat.socialservice.model.group.GroupProfile;
import ychat.socialservice.model.user.User;
import ychat.socialservice.model.util.CreateDTO;
import ychat.socialservice.model.util.UpdateDTO;
import ychat.socialservice.repository.GroupMemberRepository;
import ychat.socialservice.service.dto.ChatMemberDTO;
import ychat.socialservice.service.dto.DTOConverter;
import ychat.socialservice.service.dto.GroupProfileDTO;
import ychat.socialservice.repository.GroupRepository;
import ychat.socialservice.service.dto.GroupDTO;
import ychat.socialservice.model.group.GroupRole;
import ychat.socialservice.util.IllegalUserInputException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Performs the business logic on groups and their members. The methods are designed to
 * be simple and readable, not performant. The code will be optimized when we discover
 * bottlenecks.
 */
@Validated
@Service
@Transactional(readOnly = true)
public class GroupService {
    private final UserService userService;
    private final GroupRepository groupRepo;
    private final GroupMemberRepository groupMemberRepo;

    public GroupService(@NonNull UserService userService,
                        @NonNull GroupRepository groupRepo,
                        @NonNull GroupMemberRepository groupMemberRepo) {
        this.userService = userService;
        this.groupRepo = groupRepo;
        this.groupMemberRepo = groupMemberRepo;
    }

    public GroupMember findGroupMemberByIdsOrThrow(UUID userId, UUID groupId) {
        Optional<GroupMember> optionalGroupMember =
            groupMemberRepo.findByUserIdAndChatId(userId, groupId);
        if (optionalGroupMember.isEmpty())
            throw new IllegalUserInputException(userId + " is not a member of " + groupId + ".");
        return optionalGroupMember.get();
    }

    // Group start ---------------------------------------------------------------------------------
    public GroupDTO getGroup(@NotNull UUID groupId, @NotNull UUID requestUserId) {
        GroupMember groupMember = findGroupMemberByIdsOrThrow(requestUserId, groupId);
        Group group = groupMember.getGroupFixed(groupRepo);
        return DTOConverter.convertToDTO(group);
    }

    @Transactional
    public GroupDTO createGroup(
        @NotNull UUID userId,
        @NotNull @Validated(CreateDTO.class) GroupProfileDTO groupProfileDTO
    ) {
        User user = userService.findUserByIdOrThrow(userId);
        GroupProfile groupProfile = DTOConverter.convertToEntity(groupProfileDTO);
        Group group = new Group(user, groupProfile);
        groupRepo.save(group);
        return DTOConverter.convertToDTO(group);
    }
    // Group end -----------------------------------------------------------------------------------

    // Profiles start ------------------------------------------------------------------------------
    public GroupProfileDTO getGroupProfile(@NotNull UUID groupId, @NotNull UUID requestUserId) {
        GroupMember requestGroupMember = findGroupMemberByIdsOrThrow(requestUserId, groupId);
        Group group = requestGroupMember.getGroupFixed(groupRepo);
        return DTOConverter.convertToDTO(group.getGroupProfile());
    }

    @Transactional
    public GroupProfileDTO updateGroupProfile(
        @NotNull UUID groupId,
        @RequestBody @Validated(UpdateDTO.class) GroupProfileDTO groupProfileDTO,
        @NotNull UUID requestUserId
    ) {
        if (groupProfileDTO.removeProfilePictureId() != null
                && groupProfileDTO.profilePictureId() != null) {
            throw new IllegalUserInputException(
                "It is not allowed to set both profilePictureId and removeProfilePicture."
            );
        }
        GroupMember groupMember = findGroupMemberByIdsOrThrow(requestUserId, groupId);
        if (groupMember.getGroupRole() != GroupRole.GROUP_ADMIN) {
            throw new IllegalUserInputException(
                requestUserId + " is not a admin of " + groupId + "."
            );
        }
        Group group = groupMember.getGroupFixed(groupRepo);
        GroupProfile groupProfile = group.getGroupProfile();
        groupProfile.setGroupName(groupProfileDTO.groupName());
        if (groupProfileDTO.removeProfilePictureId() != null
                && groupProfileDTO.removeProfilePictureId())
            groupProfile.removeProfilePictureId();
        else
            groupProfile.setProfilePictureId(groupProfileDTO.profilePictureId());
        groupProfile.setProfileDescription(groupProfileDTO.profileDescription());
        return DTOConverter.convertToDTO(group.getGroupProfile());
    }
    // Profiles end --------------------------------------------------------------------------------

    // Members start -------------------------------------------------------------------------------
    @Transactional
    public ChatMemberDTO addGroupMember(@NotNull UUID groupId, @NotNull UUID userId,
                                        @NotNull UUID requestUserId) {
        GroupMember requestGroupMember = findGroupMemberByIdsOrThrow(requestUserId, groupId);
        if (requestGroupMember.getGroupRole() != GroupRole.GROUP_ADMIN)
            throw new IllegalUserInputException(requestUserId + " is not an admin of " + groupId + ".");
        Group group = requestGroupMember.getGroupFixed(groupRepo);
        User user = userService.findUserByIdOrThrow(userId);
        if (group.isMember(user)) {
            throw new IllegalUserInputException(
                group + " already has " + user + " as member."
            );
        }
        GroupMember groupMember = group.addGroupMember(user);
        return DTOConverter.convertToDTO(groupMember);
    }

    @Transactional
    public ChatMemberDTO[] addGroupMembers(@NotNull UUID groupId, @NotNull String[] emails,
                                        @NotNull UUID requestUserId) throws FirebaseAuthException {
        GroupMember requestGroupMember = findGroupMemberByIdsOrThrow(requestUserId, groupId);
        if (requestGroupMember.getGroupRole() != GroupRole.GROUP_ADMIN)
            throw new IllegalUserInputException(requestUserId + " is not an admin of " + groupId + ".");
        Group group = groupRepo.findById(requestGroupMember.getChat().getId()).get();
        List<ChatMemberDTO> groupMembers = new ArrayList<>();
        for(String email: emails) {
            UUID userId = userService.getUserIdByEmail(email);
            if(userId == null) continue;
            User user = userService.findUserByIdOrThrow(userId);
            if (group.isMember(user)) {
               continue;
            }
            groupMembers.add(DTOConverter.convertToDTO(group.addGroupMember(user)));
        }
        return groupMembers.toArray(new ChatMemberDTO[]{});
    }

    @Transactional
    public void removeGroupMember(@NotNull UUID groupId, @NotNull UUID userId,
                                  @NotNull UUID requestUserId) {
        GroupMember requestGroupMember = findGroupMemberByIdsOrThrow(requestUserId, groupId);
        if (requestGroupMember.getGroupRole() != GroupRole.GROUP_ADMIN)
            throw new IllegalUserInputException(userId + " is not a admin of " + groupId + ".");
        Group group = requestGroupMember.getGroupFixed(groupRepo);
        User user = userService.findUserByIdOrThrow(userId);
        if (!group.isMember(user)) {
            throw new IllegalUserInputException(
                group + " does not have " + user + " as member."
            );
        }
        if (group.toDeleteIfUserRemoved(user))
            // Deletes chat membership via cascading as well
            groupRepo.delete(group);
        else
            group.removeMember(user);
    }

    public GroupRole getGroupRole(@NotNull UUID groupId, @NotNull UUID userId,
                                  @NotNull UUID requestUserId) {
        findGroupMemberByIdsOrThrow(requestUserId, groupId);
        Optional<GroupMember> optionalGroupMember =
            groupMemberRepo.findByUserIdAndChatId(userId, groupId);
        if (optionalGroupMember.isEmpty())
            return GroupRole.NOT_A_MEMBER;
        GroupMember groupMember = optionalGroupMember.get();
        return groupMember.getGroupRole();
    }

    @Transactional
    public GroupRole updateGroupRole(@NotNull UUID groupId, @NotNull UUID userId,
                                     @NotNull GroupRole groupRole, @NotNull UUID requestUserId) {
        if (groupRole == GroupRole.NOT_A_MEMBER)
            throw new IllegalUserInputException("NOT_A_MEMBER is not allowed for updateGroupRole.");
        GroupMember requestGroupMember = findGroupMemberByIdsOrThrow(requestUserId, groupId);
        if (requestGroupMember.getGroupRole() != GroupRole.GROUP_ADMIN)
            throw new IllegalUserInputException(userId + " is not a admin of " + groupId + ".");
        GroupMember groupMember = findGroupMemberByIdsOrThrow(userId, groupId);
        if (groupRole != GroupRole.GROUP_ADMIN && groupMember.getGroupFixed(groupRepo).getNumberOfAdmins() == 1
            && userId == requestUserId) {
            throw new IllegalUserInputException(
                requestUserId + " cannot demote yourself, when you are the last admin of "
                + groupId + "."
            );
        }
        groupMember.setGroupRole(groupRole);
        return groupMember.getGroupRole();
    }
    // Members end ---------------------------------------------------------------------------------
}
