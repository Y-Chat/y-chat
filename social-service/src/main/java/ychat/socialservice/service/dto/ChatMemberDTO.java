package ychat.socialservice.service.dto;

import jakarta.validation.constraints.NotNull;
import ychat.socialservice.model.group.GroupRole;

import java.util.UUID;

/**
 * Object to be returned for chat member requests. Schema changes depending on whether it is a
 * direct or group chat.
 *
 * @param userId the id of the member
 * @param userProfileDTO the profile of the member
 * @param groupRole if it is a group chat, also the role of the member
 */
public record ChatMemberDTO (
    @NotNull
    UUID userId,

    @NotNull
    UserProfileDTO userProfileDTO,

    GroupRole groupRole
) {
    // Direct chat member constructor
    public ChatMemberDTO(UUID userId, UserProfileDTO userProfileDTO) {
        this(userId, userProfileDTO, null);
    }

    // Group chat member constructor
    public ChatMemberDTO(UUID userId, UserProfileDTO userProfileDTO, GroupRole groupRole) {
        this.userId = userId;
        this.userProfileDTO = userProfileDTO;
        this.groupRole = groupRole;
    }
}
