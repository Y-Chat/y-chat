package ychat.socialservice.service.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

/**
 * Object to be returned for chat requests. Schema changes depending on whether it is a direct or
 * group chat and whether the other user of a direct chat still exists.
 *
 * @param chatId the id of the cat
 * @param chatType the type of the chat, i.e. direct or group chat
 * @param groupProfileDTO if it is a group chat, the profile of the group
 * @param userId if it is a direct chat, the id of the other user
 * @param userProfileDTO if it is a direct chat and the other user still exists, the profile of the
 *                       other user
 */
public record ChatDTO (
    @NotNull
    UUID chatId,

    @NotNull
    ChatType chatType,

    GroupProfileDTO groupProfileDTO,

    UUID userId,

    UserProfileDTO userProfileDTO
) {
    // Constructor for group chats
    public ChatDTO(UUID chatId, GroupProfileDTO groupProfileDTO) {
        this(chatId, ChatType.GROUP_CHAT, groupProfileDTO, null, null);
    }

    // Constructor for direct chats
    public ChatDTO(UUID chatId, UUID userId, UserProfileDTO userProfileDTO) {
        this(chatId, ChatType.DIRECT_CHAT, null, userId, userProfileDTO);
    }
}
