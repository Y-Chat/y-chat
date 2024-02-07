package ychat.socialservice.controller;

import com.google.firebase.auth.FirebaseAuthException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ychat.socialservice.SecurityConfig;
import ychat.socialservice.model.user.User;
import ychat.socialservice.service.dto.UserProfileDTO;
import ychat.socialservice.service.dto.UserSettingsDTO;
import ychat.socialservice.service.dto.BlockedUserDTO;
import ychat.socialservice.service.UserService;
import ychat.socialservice.service.dto.UserDTO;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@ResponseStatus(HttpStatus.OK)
@RequestMapping("/users")
@Tag(
    name = "Users Endpoint",
    description = "Manage the user as they exist in the Y-Chat domain. The lifetime of a user is " +
                  "owned by the service providing authentication. The user email is transported " +
                  "via the JWT."
)
public class UserController {
    private final UserService userService;
    public UserController(@NonNull UserService userService) {
        this.userService = userService;
    }

    // User start ----------------------------------------------------------------------------------
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
        summary = "Create a user with an initial profile.",
        description = "One cannot create a user which exists already. If the profile description " +
                      "is empty, a default value is used. ProfilePictureId is optional. The " +
                      "other fields are required. RemoveProfilePicture must be null. The email " +
                      "must not exist in the system yet."
    )
    public UserDTO createUser(@RequestParam UUID userId,
                              @RequestBody UserProfileDTO userProfileDTO) {
        SecurityConfig.verifyUserAccess(userId);
        return userService.createUser(userId, userProfileDTO);
    }

    @GetMapping("/{userId}")
    @Operation(
        summary = "Fetch the general information about a user.",
        description = "Returns the user id, profile, and settings."
    )
    public UserDTO getUser(@PathVariable UUID userId) {
        SecurityConfig.verifyUserAccess(userId);
        return userService.getUser(userId);
    }

    @GetMapping("/byEmail")
    @Operation(
        summary = "Get the user id for an email.",
        description = "Returns the user id if it exists."
    )
    public UUID getUserIdByEmail(@RequestParam String email) throws FirebaseAuthException {
        User user = userService.getUserIdByEmail(email);
        return user.getId();
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
        summary = "Delete a user from the Social service.",
        description = "The deletion process includes all blocked relations and all chat " +
                      "memberships."
    )
    public void deleteUser(@PathVariable UUID userId) {
        SecurityConfig.verifyUserAccess(userId);
        userService.deleteUser(userId);
    }
    // User end ------------------------------------------------------------------------------------

    // Profiles and settings start -----------------------------------------------------------------
    @GetMapping("/{userId}/profile")
    @Operation(
        summary = "Fetch the profile of a user.",
        description = "All returned fields are populated. RemoveProfilePictureId is null."
    )
    public UserProfileDTO getUserProfile(@PathVariable UUID userId) {
        return userService.getUserProfile(userId);
    }

    @PatchMapping("/{userId}/profile")
    @Operation(
        summary = "Update the profile of a user.",
        description = "All given fields are updated. To remove the profilePictureId, set the " +
                      "field to null and removeProfilePictureId to true. Returns the same object " +
                      "as getUserProfile."
    )
    public UserProfileDTO updateUserProfile(@PathVariable UUID userId,
                                            @RequestBody UserProfileDTO userProfileDTO
    ) {
        SecurityConfig.verifyUserAccess(userId);
        return userService.updateUserProfile(userId, userProfileDTO);
    }

    @GetMapping("/{userId}/settings")
    @Operation(
        summary = "Fetch the settings for a user.",
        description = "All returned fields are populated."
    )
    public UserSettingsDTO getUserSettings(@PathVariable UUID userId) {
        SecurityConfig.verifyUserAccess(userId);
        return userService.getUserSettings(userId);
    }

    @PatchMapping("/{userId}/settings")
    @Operation(
        summary = "Update the settings for a user.",
        description = "All given fields are updated. Returns the same object as getUserSettings."
    )
    public UserSettingsDTO updateUserSettings(@PathVariable UUID userId,
                                              @RequestBody UserSettingsDTO userSettingsDTO) {
        SecurityConfig.verifyUserAccess(userId);
        return userService.updateUserSettings(userId, userSettingsDTO);
    }
    // Profiles and settings end -------------------------------------------------------------------

    // Blocking start ------------------------------------------------------------------------------
    @GetMapping("/{userId}/blockedUsers")
    @Operation(
        summary = "Fetch the blocklist for a user.",
        description = "Returns a page of ids, profiles, and timestamps when the user has been " +
                      "blocked. Page size is not allowed to exceed " +
                      UserService.MAX_BLOCKED_USER_PAGE_SIZE + "."
    )
    public Page<BlockedUserDTO> getBlockedUsers(@PathVariable UUID userId, Pageable pageable) {
        SecurityConfig.verifyUserAccess(userId);
        return userService.getBlockedUsers(userId, pageable);
    }

    @GetMapping("/{userId}/blockedUsers/{isBlockedId}")
    @Operation(
        summary = "Check whether a given user has another user blocked.",
        description = "Returns null, if the user is not blocked. Returns a timestamp " +
                      "corresponding to the block time, if the user is blocked."
    )
    public LocalDateTime isBlockedUser(@PathVariable UUID userId,
                                       @PathVariable UUID isBlockedId) {
        SecurityConfig.verifyUserAccess(userId);
        return userService.isBlockedUser(userId, isBlockedId);
    }

    @PostMapping("/{userId}/blockedUsers")
    @Operation(
        summary = "Add a user to the blocklist.",
        description = "A user cannot block themselves. There is a limit on the number of users a " +
                      "user can block. A user cannot block a user that they have already " +
                      "blocked. Returns the same object as getBlockedUsers."
    )
    public BlockedUserDTO addBlockedUser(@PathVariable UUID userId,
                                         @RequestParam UUID blockUserId) {
        SecurityConfig.verifyUserAccess(userId);
        return userService.addBlockedUser(userId, blockUserId);
    }

    @DeleteMapping("/{userId}/blockedUsers")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
        summary = "Remove a user from the blocklist.",
        description = "One cannot remove a user which has not been blocked."
    )
    public void removeBlockedUser(@PathVariable UUID userId,
                                  @RequestParam UUID unblockUserId) {
        SecurityConfig.verifyUserAccess(userId);
        userService.removeBlockedUser(userId, unblockUserId);
    }
    // Blocking end --------------------------------------------------------------------------------
}
