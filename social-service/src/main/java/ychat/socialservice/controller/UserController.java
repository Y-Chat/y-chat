package ychat.socialservice.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ychat.socialservice.service.UserService;

import java.util.Set;
import java.util.UUID;

/*
- UserId is not managed by this service but by the Auth Service
- TODO check error handling
- TODO add access control
- TODO have partial and full update
- TODO deletion of user
 */

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<Void> createUser(@RequestBody UUID userId,
                                           @RequestBody UserInfo userInfo) {
        userService.createUser(userId, userInfo);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("public-info")
    public ResponseEntity<PublicUserInfo[]> getPublicInfo(@RequestBody UUID[] userIds) {
        PublicUserInfo[] publicUserInfos = userService.getPublicUserInfo(userIds);
        return ResponseEntity.ok(publicUserInfos);
    }

    // Info start ----------------------------------------------------------------------------------
    @PatchMapping("/{userId}/info")
    public ResponseEntity<Void> updateInfo(@PathVariable UUID userId,
                                           @RequestBody UserInfo userInfo) {
        userService.updateInfo(userId, userInfo);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{userId}/info")
    public ResponseEntity<UserInfo> getInfo(@RequestBody UUID userId) {
        UserInfo userInfos = userService.getInfo(userId);
        return ResponseEntity.ok(userInfos);
    }
    // Info end ------------------------------------------------------------------------------------

    // Blocking start ------------------------------------------------------------------------------
    @PatchMapping("/{userId}/add-blocked")
    public ResponseEntity<Void> addBlocked(@PathVariable UUID userId,
                                           @RequestBody Set<UUID> blockedIds) {
        userService.addBlocked(userId, blockedIds);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{userId}/remove-blocked")
    public ResponseEntity<Void> removeBlocked(@PathVariable UUID userId,
                                              @RequestBody Set<UUID> unblockedIds) {
        userService.removeBlocked(userId, unblockedIds);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{userId}/blocked")
    public ResponseEntity<Set<UUID>> getBlocked(@PathVariable UUID userId) {
        Set<UUID> blockedIds = userService.getBlocked(userId);
        return ResponseEntity.ok(blockedIds);
    }
    // Blocking end --------------------------------------------------------------------------------

    // Chats and groups start ----------------------------------------------------------------------
    @GetMapping("/{userId}/chats")
    public ResponseEntity<Set<UUID>> getChats(@PathVariable UUID userId) {
        Set<UUID> chatIds = userService.getChats(userId);
        return ResponseEntity.ok(chatIds);
    }

    @GetMapping("/{userId}/groups")
    public ResponseEntity<Set<UUID>> getGroups(@PathVariable UUID userId) {
        Set<UUID> groupIds = userService.getGroups(userId);
        return ResponseEntity.ok(groupIds);
    }
    // Chats and groups end ------------------------------------------------------------------------
}
