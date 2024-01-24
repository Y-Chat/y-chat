package ychat.socialservice.service.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Object to be returned for blocked user requests.
 *
 * @param id the id of the blocked user
 * @param userProfileDTO the profile of the blocked user
 * @param blockedAt the timestamp when the user was blocked
 */
public record BlockedUserDTO (
    @NotNull
    UUID id,

    @NotNull
    UserProfileDTO userProfileDTO,

    @NotNull
    LocalDateTime blockedAt
) {}
