package ychat.socialservice.service.dto;

/**
 * The DTO to interact with user settings.
 *
 * @param readReceipts whether read receipts are shown
 * @param lastSeen whether the last seen information is shown
 */
public record UserSettingsDTO (
    Boolean readReceipts,

    Boolean lastSeen
) {}
