package ychat.socialservice.service.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

/**
 * Object to be returned for group requests.
 *
 * @param id the id of the group
 * @param groupProfileDTO the profile of the group
 */
public record GroupDTO (
    @NotNull
    UUID id,

    @NotNull
    GroupProfileDTO groupProfileDTO
) {}
