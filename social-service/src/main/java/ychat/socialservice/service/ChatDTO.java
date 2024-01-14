package ychat.socialservice.model.chat;

import java.util.Set;
import java.util.UUID;

public record ChatDTO(
    ChatType chatType,
    Set<UUID> userIds
) {
    public enum ChatType {GROUP_CHAT, DIRECT_CHAT}
}
