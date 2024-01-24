package ychat.socialservice.service.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Size;
import ychat.socialservice.model.util.CreateDTO;
import ychat.socialservice.model.util.UpdateDTO;

import java.util.UUID;

/**
 * The DTO to interact with user profiles.
 *
 * @param firstName any name to be shown first
 * @param lastName any name to be shown second
 * @param removeProfilePictureId flag to indicate, that the profilePictureId should be set to null
 * @param profilePictureId id to fetch the profile picture
 * @param profileDescription statement to be shown to other users
 */
public record UserProfileDTO (
    @NotNull(groups = {CreateDTO.class})
    @Size(min = 1, max = 32, groups = {CreateDTO.class, UpdateDTO.class})
    String firstName,

    @NotNull(groups = {CreateDTO.class})
    @Size(min = 1, max = 32, groups = {CreateDTO.class, UpdateDTO.class})
    String lastName,

    UUID profilePictureId,

    @Null(groups = {CreateDTO.class})
    Boolean removeProfilePictureId,

    @Size(max = 128, groups = {CreateDTO.class, UpdateDTO.class})
    String profileDescription
) {}
