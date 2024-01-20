package ychat.socialservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ychat.socialservice.service.dto.UserProfileDTO;
import ychat.socialservice.service.dto.UserSettingsDTO;
import ychat.socialservice.model.util.CreateDTO;
import ychat.socialservice.model.util.UpdateDTO;
import ychat.socialservice.service.dto.BlockedUserDTO;
import ychat.socialservice.service.UserService;
import ychat.socialservice.service.dto.UserDTO;
import ychat.socialservice.util.IllegalUserInputException;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@ResponseStatus(HttpStatus.OK)
@RequestMapping("/users")
@Validated
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
                      "other fields are required. RemoveProfilePicture must be null."
    )
    public UserDTO createUser(
        @RequestParam @NotNull UUID userId,
        @RequestBody @Validated(CreateDTO.class) UserProfileDTO userProfileDTO
    ) {
        return userService.createUser(userId, userProfileDTO);
    }

    @GetMapping("/{userId}")
    @Operation(
        summary = "Fetch the general information about a user.",
        description = "Returns the user id, profile, and settings."
    )
    public UserDTO getUser(@PathVariable @NotNull UUID userId) {
        return userService.getUser(userId);
    }

    @DeleteMapping("/{userId}")
    @Operation(
        summary = "Delete a user from the Social service.",
        description = "The deletion process includes all blocked relations and all chat " +
                      "memberships."
    )
    public void deleteUser(@PathVariable @NotNull UUID userId) {
        userService.deleteUser(userId);
    }
    // User end ------------------------------------------------------------------------------------

    // Profiles and settings start -----------------------------------------------------------------
    @GetMapping("/{userId}/profile")
    @Operation(
        summary = "Fetch the profile of a user.",
        description = "All returned fields are populated. RemoveProfilePictureId is null."
    )
    public UserProfileDTO getUserProfile(@PathVariable @NotNull UUID userId) {
        return userService.getUserProfile(userId);
    }

    @PatchMapping("/{userId}/profile")
    @Operation(
        summary = "Update the profile of a user.",
        description = "All given fields are updated. To remove the profilePictureId, set the " +
                      "field to null and removeProfilePictureId to true."
    )
    public void updateUserProfile(
        @PathVariable @NotNull UUID userId,
        @RequestBody @Validated(UpdateDTO.class) UserProfileDTO userProfileDTO
    ) {
        if (userProfileDTO.removeProfilePictureId() && userProfileDTO.profilePictureId() != null) {
            throw new IllegalUserInputException(
                "It is not allowed to set both profilePictureId and removeProfilePicture."
            );
        }
        userService.updateUserProfile(userId, userProfileDTO);
    }

    @GetMapping("/{userId}/settings")
    @Operation(
        summary = "Fetch the settings for a user.",
        description = "All returned fields are populated."
    )
    public UserSettingsDTO getUserSettings(@PathVariable @NotNull UUID userId) {
        return userService.getUserSettings(userId);
    }

    @PatchMapping("/{userId}/settings")
    @Operation(
        summary = "Update the settings for a user.",
        description = "All given fields are updated."
    )
    public void updateUserSettings(
        @PathVariable @NotNull UUID userId,
        @RequestBody @Validated(UpdateDTO.class) UserSettingsDTO userSettingsDTO) {
        userService.updateUserSettings(userId, userSettingsDTO);
    }
    // Profiles and settings end -------------------------------------------------------------------

    // Blocking start ------------------------------------------------------------------------------
    @GetMapping("/{userId}/blockedUsers")
    @Operation(
        summary = "Fetch the blocklist for a user.",
        description = "Returns a page of ids, profiles, and timestamps when the user has been " +
                      "blocked. Page size is not allowed to exceed 1000."
    )
    public Page<BlockedUserDTO> getBlockedUsers(@PathVariable @NotNull UUID userId,
                                                @NotNull Pageable pageable) {
        if (pageable.getPageSize() > UserService.MAX_BLOCKED_USER_PAGE_SIZE) {
            throw new IllegalUserInputException(
                "Page size for Blocked Users is not allowed to be larger than " +
                UserService.MAX_BLOCKED_USER_PAGE_SIZE + "."
            );
        }
        return userService.getBlockedUsers(userId, pageable);
    }

    @GetMapping("/{userId}/blockedUsers/{isBlockedId}")
    @Operation(
        summary = "Check whether a given user has another user blocked.",
        description = "Returns null, if the user is not blocked. Returns a timestamp " +
                      "corresponding to the block time, if the user is blocked."
    )
    public LocalDateTime isBlockedUser(@PathVariable @NotNull UUID userId,
                                       @PathVariable @NotNull UUID isBlockedId) {
        return userService.isBlockedUser(userId, isBlockedId);
    }

    @PostMapping("/{userId}/blockedUsers")
    @Operation(
        summary = "Add a user to the blocklist.",
        description = "A user cannot block themselves. There is a limit on the number of users a " +
                      "user can block. A user cannot block a user that they have already blocked."
    )
    public void addBlockedUser(@PathVariable @NotNull UUID userId,
                               @RequestParam @NotNull UUID blockUserId) {
        if (userId == blockUserId)
            throw new IllegalUserInputException("User cannot block themselves: " + userId);
        userService.addBlockedUser(userId, blockUserId);
    }

    @DeleteMapping("/{userId}/blockedUsers")
    @Operation(
        summary = "Remove a user from the blocklist.",
        description = "One cannot remove a user which has not been blocked."
    )
    public void removeBlockedUser(@PathVariable @NotNull UUID userId,
                                  @RequestParam @NotNull UUID unblockUserId) {
        userService.removeBlockedUser(userId, unblockUserId);
    }
    // Blocking end --------------------------------------------------------------------------------
}
