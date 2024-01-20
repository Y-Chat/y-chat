package ychat.socialservice.model.chat;

import jakarta.persistence.*;
import lombok.NonNull;
import ychat.socialservice.model.user.User;

import java.util.UUID;

/**
 * Models the many-to-many relationship between users and direct chats. This entity is owned by the
 * direct chat.
 */
@Entity
@Table(name = "direct_chat_member")
public class DirectChatMember extends ChatMember {
    private UUID otherUserId;

    protected DirectChatMember() {} // Required by JPA

    public DirectChatMember(User user, DirectChat directChat, @NonNull UUID otherUserId) {
        super(user, directChat);
        this.otherUserId = otherUserId;
    }

    public UUID getOtherUserId() {
        return otherUserId;
    }

    private void setOtherUserId(UUID otherUserId) {
        this.otherUserId = otherUserId;
    } // Required by JPA

    // equals and hashCode defined in superclass

    @Override
    public String toString() {
        return "DirectChatMember{" + "user=" + getUser() + "directChat=" + getChat()
                + ", chatStatus=" + getChatStatus() + '}';
    }
}
