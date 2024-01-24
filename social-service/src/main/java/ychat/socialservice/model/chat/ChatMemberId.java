package ychat.socialservice.model.chat;

import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

/**
 * Embedded id for the ChatMember entity. Owned and only accessed by ChatMember.
 */
@Embeddable
public class ChatMemberId implements Serializable {
    private UUID userId;
    private UUID chatId;

    protected ChatMemberId() {} // Required by JPA

    public ChatMemberId(UUID userId, UUID chatId) {
        this.userId = userId;
        this.chatId = chatId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChatMemberId that = (ChatMemberId) o;
        return Objects.equals(this.userId, that.userId)
            && Objects.equals(this.chatId, that.chatId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, chatId);
    }

    @Override
    public String toString() {
        return "ChatMemberId{" + "userId=" + userId + ", chatId=" + chatId + '}';
    }
}
