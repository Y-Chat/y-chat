package ychat.socialservice.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ychat.socialservice.model.group.GroupRole;
import ychat.socialservice.service.GroupService;

import java.util.Set;
import java.util.UUID;

/*
- Has not delete group endpoint, a group is deleted once all users are removed
- TODO check error handling
- TODO add access control
 */

@RestController
@RequestMapping("/groups")
public class GroupController {
    private final GroupService groupService;
    public GroupController(GroupService groupService) {
        this.groupService = groupService;
    }

    @PostMapping
    public ResponseEntity<UUID> createGroup(@RequestBody UUID adminUserId,
                                            @RequestBody Set<UUID> memberUserIds) {
        UUID groupId = groupService.createGroup(adminUserId, memberUserIds);
        return ResponseEntity.status(HttpStatus.CREATED).body(groupId);
    }

    // Users start --------------------------------------------------------------------------------
    @PatchMapping("/{groupId}/add-users")
    public ResponseEntity<Void> addUsers(@PathVariable UUID groupId,
                                         @RequestBody Set<UUID> userIds) {
        groupService.addUsers(groupId, userIds);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{groupId}/remove-users")
    public ResponseEntity<Void> removeUsers(@PathVariable UUID groupId,
                                            @RequestBody Set<UUID> userIds) {
        groupService.removeUsers(groupId, userIds);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{groupId}/users")
    public ResponseEntity<Set<UUID>> getUsers(@PathVariable UUID groupId) {
        Set<UUID> userIds = groupService.getUsers(groupId);
        return ResponseEntity.ok(userIds);
    }
    // Users end ----------------------------------------------------------------------------------

    // Roles start --------------------------------------------------------------------------------
    @PatchMapping("/{groupId}/roles")
    public ResponseEntity<Void> updateRoles(@PathVariable UUID groupId,
                                            @RequestBody Set<GroupRole> groupRoles) {
        groupService.updateRoles(groupId, groupRoles);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{groupId}/roles")
    public ResponseEntity<Set<GroupRole>> getRoles(@PathVariable UUID groupId) {
        Set<GroupRole> groupRoles = groupService.getRoles(groupId);
        return ResponseEntity.ok(groupRoles);
    }
    // Roles end -----------------------------------------------------------------------------------

    // Info start ----------------------------------------------------------------------------------
    @PatchMapping("/{groupId}/info")
    public ResponseEntity<Void> updateInfo(@PathVariable UUID groupId,
                                           @RequestBody GroupInfo groupInfo) {
        groupService.updateInfo(groupId, groupInfo);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{groupId}/info")
    public ResponseEntity<GroupInfo> getInfo(@PathVariable UUID groupId) {
        GroupInfo groupInfo = groupService.getInfo(groupId);
        return ResponseEntity.ok(groupInfo);
    }
    // Info End ------------------------------------------------------------------------------------
}
