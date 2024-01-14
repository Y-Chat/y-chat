package ychat.socialservice.controller;

import lombok.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ychat.socialservice.model.group.GroupProfile;
import ychat.socialservice.model.group.GroupRole;
import ychat.socialservice.service.GroupService;

import java.util.Set;
import java.util.UUID;

/**
 * This endpoint manages groups, a collection of user with a shared chat. Internally, the group is
 * actually a type of chat which means that a group id also serves as a chat id and can be used at
 * the chats endpoint.
 * <p>
 * The endpoints are structured into managing the group lifecycle, group profile, and
 * group members and their roles.
 * <p>
 * A group cannot be explicitly deleted, it is deleted once all users have left the group. The
 * deletion process includes deleting the chat and the information about the group members.
 */
@RestController
@RequestMapping("/groups")
public class GroupController {
    private final GroupService groupService;
    public GroupController(@NonNull GroupService groupService) {
        this.groupService = groupService;
    }

    // Lifecycle start -----------------------------------------------------------------------------
    /**
     * Creates a group and makes the user its only member and admin.
     *
     * @param creatorUserId the id of the initial group member
     * @return the id of the newly created group
     */
    @PostMapping
    public ResponseEntity<UUID> createGroup(@RequestParam UUID creatorUserId) {
        UUID groupId = groupService.createGroup(creatorUserId);
        return ResponseEntity.status(HttpStatus.CREATED).body(groupId);
    }
    // Lifecycle end -------------------------------------------------------------------------------

    // Group profile start -------------------------------------------------------------------------
    /**
     * Fetches the group profile for a given group.
     *
     * @param groupId the id of the group
     * @return the group profile of this group
     */
    @GetMapping("/{groupId}")
    public ResponseEntity<GroupProfile> getProfile(@PathVariable UUID groupId) {
        GroupProfile groupProfile = groupService.getProfile(groupId);
        return ResponseEntity.ok(groupProfile);
    }

    /**
     * Updates the group profile for a given group.
     *
     * @param groupId the id of the group
     * @param groupProfile the new group profile
     * @return no content
     */
    @PutMapping("/{groupId}")
    public ResponseEntity<Void> updateProfile(@PathVariable UUID groupId,
                                              @RequestBody GroupProfile groupProfile) {
        groupService.updateProfile(groupId, groupProfile);
        return ResponseEntity.ok().build();
    }
    // Group profile end ---------------------------------------------------------------------------

    // Members start -------------------------------------------------------------------------------
    /**
     * Fetches all members of a group.
     *
     * @param groupId the id of the group
     * @return a list of user ids corresponding to the group members
     */
    @GetMapping("/{groupId}/members")
    public ResponseEntity<Set<UUID>> getMembers(@PathVariable UUID groupId) {
        Set<UUID> userIds = groupService.getMembers(groupId);
        return ResponseEntity.ok(userIds);
    }

    /**
     * Adds a new user to the group. One cannot add someone who is already a member of the group.
     *
     * @param groupId the id of the group
     * @param userId the id of the user to be added
     * @return no content
     */
    @PostMapping("/{groupId}/members")
    public ResponseEntity<Void> addMember(@PathVariable UUID groupId,
                                          @RequestParam UUID userId) {
        groupService.addMember(groupId, userId);
        return ResponseEntity.ok().build();
    }

    /**
     * Removes a member from a group. One cannot remove someone who is not a member of the group.
     *
     * @param groupId the id of the group
     * @param userId the id of the member to be removed
     * @return no content
     */
    @DeleteMapping("/{groupId}/members")
    public ResponseEntity<Void> removeMember(@PathVariable UUID groupId,
                                             @RequestParam UUID userId) {
        groupService.removeMember(groupId, userId);
        return ResponseEntity.ok().build();
    }

    /**
     * Fetches the role of a user for a group. One cannot fetch the role of a user not in this
     * group.
     *
     * @param groupId the id of the group
     * @param userId the id of the group member
     * @return the role of the group member for this group
     */
    @GetMapping("/{groupId}/members/{userId}")
    public ResponseEntity<GroupRole> getRole(@PathVariable UUID groupId,
                                             @PathVariable UUID userId) {
        GroupRole groupRole = groupService.getRole(groupId, userId);
        return ResponseEntity.ok(groupRole);
    }

    /**
     * Updates the role of a group member for a given group. One cannot update the role of a user
     * who is not in the group.
     *
     * @param groupId the id of the group
     * @param userId the id of the group member
     * @param groupRole the new role of the user
     * @return no content
     */
    @PutMapping("/{groupId}/members/{userId}")
    public ResponseEntity<Void> updateRole(@PathVariable UUID groupId,
                                           @PathVariable UUID userId,
                                           @RequestBody GroupRole groupRole) {
        groupService.updateRole(groupId, userId, groupRole);
        return ResponseEntity.ok().build();
    }
    // Members end ---------------------------------------------------------------------------------
}
