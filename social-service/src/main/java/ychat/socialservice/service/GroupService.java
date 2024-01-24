package ychat.socialservice.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.NonNull;
import org.springframework.stereotype.Service;
import ychat.socialservice.model.group.Group;
import ychat.socialservice.model.group.GroupMember;
import ychat.socialservice.model.group.GroupProfile;
import ychat.socialservice.model.user.User;
import ychat.socialservice.repository.GroupMemberRepository;
import ychat.socialservice.service.dto.ChatMemberDTO;
import ychat.socialservice.service.dto.DTOConverter;
import ychat.socialservice.service.dto.GroupProfileDTO;
import ychat.socialservice.repository.GroupRepository;
import ychat.socialservice.service.dto.GroupDTO;
import ychat.socialservice.model.group.GroupRole;
import ychat.socialservice.util.IllegalUserInputException;

import java.util.Optional;
import java.util.UUID;

/**
 * Performs the business logic on groups and their members. The methods are designed to
 * be simple and readable, not performant. The code will be optimized when we discover
 * bottlenecks.
 */
@Service
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

    public Group findGroupByIdOrThrow(UUID groupId) {
        Optional<Group> optionalGroup = groupRepo.findById(groupId);
        if (optionalGroup.isEmpty())
            throw new EntityNotFoundException("Group does not exist: " + groupId);
        return optionalGroup.get();
    }

    public Optional<GroupMember> findGroupMemberByIdsOrThrow(UUID groupId, UUID userId) {
        Group group = findGroupByIdOrThrow(groupId);
        User user = userService.findUserByIdOrThrow(userId);
        return groupMemberRepo.findByUserAndChat(user, group);
    }

    // Group start ---------------------------------------------------------------------------------
    public GroupDTO getGroup(UUID groupId) {
        Group group = findGroupByIdOrThrow(groupId);
        return DTOConverter.convertToDTO(group);
    }

    public GroupDTO createGroup(UUID userId, GroupProfileDTO groupProfileDTO) {
        User user = userService.findUserByIdOrThrow(userId);
        GroupProfile groupProfile = DTOConverter.convertToEntity(groupProfileDTO);
        Group group = new Group(user, groupProfile);
        groupRepo.save(group);
        return DTOConverter.convertToDTO(group);
    }
    // Group end -----------------------------------------------------------------------------------

    // Profiles start ------------------------------------------------------------------------------
    public GroupProfileDTO getGroupProfile(UUID groupId) {
        Group group = findGroupByIdOrThrow(groupId);
        return DTOConverter.convertToDTO(group.getGroupProfile());
    }

    public GroupProfileDTO updateGroupProfile(UUID groupId, GroupProfileDTO groupProfileDTO) {
        Group group = findGroupByIdOrThrow(groupId);
        GroupProfile groupProfile = group.getGroupProfile();
        groupProfile.setGroupName(groupProfileDTO.groupName());
        if (groupProfileDTO.removeProfilePictureId())
            groupProfile.removeProfilePictureId();
        else
            groupProfile.setProfilePictureId(groupProfileDTO.profilePictureId());
        groupProfile.setProfileDescription(groupProfileDTO.profileDescription());
        groupRepo.save(group);
        return DTOConverter.convertToDTO(group.getGroupProfile());
    }
    // Profiles end --------------------------------------------------------------------------------

    // Members start -------------------------------------------------------------------------------
    public ChatMemberDTO addGroupMember(UUID groupId, UUID userId) {
        Group group = findGroupByIdOrThrow(groupId);
        User user = userService.findUserByIdOrThrow(userId);
        if (group.isGroupMember(user)) {
            throw new IllegalUserInputException(
                group + " already has " + user + " as member."
            );
        }
        GroupMember groupMember = group.addGroupMember(user);
        groupRepo.save(group);
        return DTOConverter.convertToDTO(groupMember);
    }

    public void removeGroupMember(UUID groupId, UUID userId) {
        Group group = findGroupByIdOrThrow(groupId);
        User user = userService.findUserByIdOrThrow(userId);
        if (!group.isGroupMember(user)) {
            throw new IllegalUserInputException(
                group + " does not have " + user + " as member."
            );
        }
        if (group.toDeleteIfUserRemoved(user)) {
            // Deletes chat membership via cascading as well
            groupRepo.delete(group);
        } else {
            group.removeGroupMember(user);
            groupRepo.save(group);
        }
    }

    public GroupRole getGroupRole(UUID groupId, UUID userId) {
        Optional<GroupMember> optionalGroupMember = findGroupMemberByIdsOrThrow(groupId, userId);
        if (optionalGroupMember.isEmpty())
            return GroupRole.NOT_A_MEMBER;
        GroupMember groupMember = optionalGroupMember.get();
        return groupMember.getGroupRole();
    }

    public GroupRole updateGroupRole(UUID groupId, UUID userId, GroupRole groupRole) {
        if (groupRole == GroupRole.NOT_A_MEMBER)
            throw new IllegalUserInputException("NOT_A_MEMBER is not allowed for updateGroupRole.");
        Optional<GroupMember> optionalGroupMember = findGroupMemberByIdsOrThrow(groupId, userId);
        if (optionalGroupMember.isEmpty())
            throw new IllegalUserInputException(userId + " not a member of group " + groupId);
        GroupMember groupMember = optionalGroupMember.get();
        groupMember.setGroupRole(groupRole);
        groupMemberRepo.save(groupMember);
        return groupMember.getGroupRole();
    }
    // Members end ---------------------------------------------------------------------------------
}
