package ychat.socialservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ychat.socialservice.service.dto.ChatMemberDTO;
import ychat.socialservice.service.dto.GroupProfileDTO;
import ychat.socialservice.model.util.CreateDTO;
import ychat.socialservice.model.util.UpdateDTO;
import ychat.socialservice.service.dto.GroupDTO;
import ychat.socialservice.model.group.GroupRole;
import ychat.socialservice.service.GroupService;
import ychat.socialservice.util.IllegalUserInputException;

import java.util.UUID;

@RestController
@RequestMapping("/groups")
@ResponseStatus(HttpStatus.OK)
@Validated
@Tag(
    name = "Groups Endpoint",
    description = "Manage groups, a collection of users with a chat. In reality, a group is also " +
                  "a type of chat, thus the group id can be used at the Chats endpoint. A group " +
                  "cannot be explicitly deleted, it is deleted once all users have left the " +
                  "group. A deleted user automatically leaves the group."
)
public class GroupController {
    private final GroupService groupService;
    public GroupController(@NonNull GroupService groupService) {
        this.groupService = groupService;
    }

    // Group start -----------------------------------------------------------------------------
    @GetMapping("/{groupId}")
    @Operation(
        summary = "Fetch the information about a group.",
        description = "Returns the group id and group profile."
    )
    public GroupDTO getGroup(@PathVariable @NotNull UUID groupId) {
        return groupService.getGroup(groupId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
        summary = "Create a group with an initial user.",
        description = "The initial user is the only member and admin of the newly created group. " +
                      "The name field of group profile is required. If no description is " +
                      "provided a default description will be set. Picture id is optional. " +
                      "Returns the same structure as getGroup."
    )
    public GroupDTO createGroup(
        @RequestParam @NotNull UUID userId,
        @RequestBody @Validated(CreateDTO.class) GroupProfileDTO groupProfileDTO
    ) {
        return groupService.createGroup(userId, groupProfileDTO);
    }
    // Group end -----------------------------------------------------------------------------------

    // Profile start -------------------------------------------------------------------------------
    @GetMapping("/{groupId}/profile")
    @Operation(
        summary = "Fetch the group profile for a given group.",
        description = "All returned fields are populated. RemoveProfilePictureId is null."
    )
    public GroupProfileDTO getGroupProfile(@PathVariable @NotNull UUID groupId) {
        return groupService.getGroupProfile(groupId);
    }

    @PatchMapping("/{groupId}/profile")
    @Operation(
        summary = "Update the group profile for a given group.",
        description = "All given fields are updated. To remove the profilePictureId, set the " +
                      "field to null and removeProfilePictureId to true."
    )
    public GroupProfileDTO updateGroupProfile(
        @PathVariable @NotNull UUID groupId,
        @RequestBody @Validated(UpdateDTO.class) GroupProfileDTO groupProfileDTO
    ) {
        if (groupProfileDTO.removeProfilePictureId() && groupProfileDTO.profilePictureId() != null)
            throw new IllegalUserInputException(
                "It is not allowed to set both profilePictureId and removeProfilePicture."
            );
        return groupService.updateGroupProfile(groupId, groupProfileDTO);
    }
    // Profile start -------------------------------------------------------------------------------

    // Members start -------------------------------------------------------------------------------

    // All members can be fetched in the chat endpoint

    @PostMapping("/{groupId}/members")
    @Operation(
        summary = "Add a new user to the group.",
        description = "The new user is added as a GROUP_MEMBER. One cannot add someone who is " +
                      "already part of the group. Returns the same object as getChatMember."
    )
    public ChatMemberDTO addGroupMember(@PathVariable @NotNull UUID groupId,
                                        @RequestParam @NotNull UUID userId) {
        return groupService.addGroupMember(groupId, userId);
    }

    @DeleteMapping("/{groupId}/members")
    @Operation(
        summary = "Remove a member from a group.",
        description = "One cannot remove someone who is not a member of the group."
    )
    public void removeGroupMember(@PathVariable @NotNull UUID groupId,
                                  @RequestParam @NotNull UUID userId) {
        groupService.removeGroupMember(groupId, userId);
    }

    @GetMapping("/{groupId}/members/{userId}/role")
    @Operation(
        summary = "Fetch the role of a user for a group.",
        description = "If the user is not part of the group, NOT_A_MEMBER is returned."
    )
    public GroupRole getGroupRole(@PathVariable @NotNull UUID groupId,
                                  @PathVariable @NotNull UUID userId) {
        return groupService.getGroupRole(groupId, userId);
    }

    @PatchMapping("/{groupId}/members/{userId}/role")
    @Operation(
        summary = "Update the role of a group member for a group.",
        description = "One cannot update the role of a user who is not in the group. This " +
                      "endpoint cannot be used to remove someone from a group. NOT_A_MEMBER is " +
                      "not a valid value for this endpoint."
    )
    public GroupRole updateGroupRole(@PathVariable @NotNull UUID groupId,
                                @PathVariable @NotNull UUID userId,
                                @RequestBody @NotNull GroupRole groupRole) {
        return groupService.updateGroupRole(groupId, userId, groupRole);
    }
    // Members end ---------------------------------------------------------------------------------
}
