package ychat.socialservice.service.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Size;
import ychat.socialservice.model.util.CreateDTO;
import ychat.socialservice.model.util.UpdateDTO;

import java.util.UUID;

/**
 * The DTO to interact with group profiles.
 *
 * @param groupName the name of the group, i.e. how it is visualized when viewing the chats
 * @param profilePictureId id to fetch the profile picture
 * @param removeProfilePictureId flag to indicate, that the profilePictureId should be set to null
 * @param profileDescription the description of what the group is about
 */
public record GroupProfileDTO (
        @NotNull(groups = {CreateDTO.class})
        @Size(min = 1, max = 32, groups = {CreateDTO.class, UpdateDTO.class})
        String groupName,

        String profilePictureId,

        @Null(groups = {CreateDTO.class})
        Boolean removeProfilePictureId,

        @Size(max = 128, groups = {CreateDTO.class, UpdateDTO.class})
        String profileDescription
) {}