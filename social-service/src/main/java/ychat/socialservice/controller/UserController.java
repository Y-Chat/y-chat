package ychat.socialservice.controller;

import lombok.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ychat.socialservice.model.user.UserProfile;
import ychat.socialservice.model.user.UserSettings;
import ychat.socialservice.service.UserService;

import java.util.Set;
import java.util.UUID;

/**
 * This endpoint manages the user as he exists in the Y-Chat domain, specifically their profiles,
 * settings, and who they have blocked.
 * <p>
 * Importantly, the lifetime of a user is owned by the Auth service. The user email and phone number
 * are transported via the JWT.
 */
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    public UserController(@NonNull UserService userService) {
        this.userService = userService;
    }

    // Lifecycle start -----------------------------------------------------------------------------
    /**
     * Create a user with an initial profile.
     *
     * @param userId the id associated with the user from the Auth service
     * @param userProfile the public information of a user
     * @return no content
     */
    @PostMapping
    public ResponseEntity<Void> createUser(@RequestBody UUID userId,
                                           @RequestBody UserProfile userProfile) {
        userService.createUser(userId, userProfile);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * Deletes the user from the Social service including all blocked relations and all group/chat
     * memberships.
     *
     * @param userId the id of the user
     * @return no content
     */
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID userId) {
        userService.deleteUser(userId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
    // Lifecycle end -------------------------------------------------------------------------------

    // Profiles start ------------------------------------------------------------------------------
    /**
     * Fetches the profile for a given user.
     *
     * @param userId the id of the user
     * @return userProfile the public information of a user
     */
    @GetMapping("/{userId}")
    public ResponseEntity<UserProfile> getUserProfile(@PathVariable UUID userId) {
        UserProfile userProfile = userService.getUserProfile(userId);
        return ResponseEntity.ok(userProfile);
    }

    /**
     * Updates the profile for a given user.
     *
     * @param userId the id of the user
     * @param userProfile the public information of a user
     * @return no content
     */
    @PutMapping("/{userId}")
    public ResponseEntity<Void> updateUserProfile(@PathVariable UUID userId,
                                                  @RequestBody UserProfile userProfile) {
        userService.updateUserProfile(userId, userProfile);
        return new ResponseEntity<>(HttpStatus.OK);
    }
    // Profiles end --------------------------------------------------------------------------------

    // Settings start ------------------------------------------------------------------------------
    /**
     * Fetch the settings for a given user.
     * @param userId the id of the user
     * @return the settings for the user
     */
    @GetMapping("/{userId}/settings")
    public ResponseEntity<UserSettings> getUserSettings(@PathVariable UUID userId) {
        UserSettings userSettings = userService.getUserSettings(userId);
        return ResponseEntity.ok(userSettings);
    }

    /**
     * Update the settings for a given user.
     *
     * @param userId the id of the user
     * @param userSettings the new settings for the user
     * @return no content
     */
    @PutMapping("/{userId}/settings")
    public ResponseEntity<Void> updateUserSettings(@PathVariable UUID userId,
                                                   @RequestBody UserSettings userSettings) {
        userService.updateUserSettings(userId, userSettings);
        return new ResponseEntity<>(HttpStatus.OK);
    }
    // Settings end --------------------------------------------------------------------------------

    // Blocking start ------------------------------------------------------------------------------
    /**
     * Get the blocklist for a user. Not paginated as there is a limit on the number of users you
     * can block.
     *
     * @param userId the id of the blocker
     * @return a list of ids belonging to the blocked users
     */
    @GetMapping("/{userId}/blockedUsers")
    public ResponseEntity<Set<UUID>> getBlockedUsers(@PathVariable UUID userId) {
        Set<UUID> blockedUserIds = userService.getBlockedUsers(userId);
        return ResponseEntity.ok(blockedUserIds);
    }

    /**
     * Check whether a given user has another user blocked.
     *
     * @param userId the id of the blocker
     * @param checkUserId the id of the blockee
     * @return a boolean indicating whether the user is blocked
     */
    @GetMapping("/{userId}/blockedUsers/{checkUserId}")
    public ResponseEntity<Boolean> checkBlockedUser(@PathVariable UUID userId,
                                                    @PathVariable UUID checkUserId) {
        boolean blocked = userService.checkBlockedUser(userId, checkUserId);
        return ResponseEntity.ok(blocked);
    }
    /**
     * Add another user to the list of blocked users. A user cannot block themselves. There is a
     * limit on the number of users a user can block.
     *
     * @param userId the id of the blocker
     * @param blockUserId the id of the blockee
     * @return no content
     */
    @PostMapping("/{userId}/blockedUsers")
    public ResponseEntity<Void> addBlockedUser(
        @PathVariable UUID userId, @RequestParam UUID blockUserId
    ) {
        userService.addBlockedUser(userId, blockUserId);
        return ResponseEntity.ok().build();
    }

    /**
     * Removes another user from the blocklist.
     *
     * @param userId the id of the blocker
     * @param unblockUserId the id of the blockee
     * @return no content
     */
    @DeleteMapping("/{userId}/blockedUsers")
    public ResponseEntity<Void> removeBlockedUser(@PathVariable UUID userId,
                                                  @RequestParam UUID unblockUserId) {
        userService.removeBlockedUser(userId, unblockUserId);
        return ResponseEntity.ok().build();
    }
    // Blocking end --------------------------------------------------------------------------------
}
