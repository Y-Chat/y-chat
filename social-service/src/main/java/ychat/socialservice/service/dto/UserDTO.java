package ychat.socialservice.service.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

/**
 * Object to be returned for user requests.
 *
 * @param id the id of the user
 * @param userProfileDTO the profile of the user
 * @param userSettingsDTO the settings of the user
 */
public record UserDTO (
    @NotNull
    UUID id,

    @NotNull
    UserProfileDTO userProfileDTO,

    @NotNull
    UserSettingsDTO userSettingsDTO
) {}
