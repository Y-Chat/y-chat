package ychat.socialservice.model.chat;

import jakarta.persistence.*;
import lombok.NonNull;
import ychat.socialservice.model.user.User;
import ychat.socialservice.model.util.TimestampEntity;

/**
 * Abstraction over group and direct chat membership to allow for efficient handling of common
 * functionality. It is not mapped to its on table as the subclasses are already many-to-many
 * connection tables themselves.
 */
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class ChatMember extends TimestampEntity {
    @EmbeddedId
    protected ChatMemberId chatMemberId;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn
    private User user;

    @ManyToOne
    @MapsId("chatId")
    @JoinColumn
    private Chat chat;

    @Enumerated(EnumType.STRING)
    private ChatStatus chatStatus;

    protected ChatMember() {} // Required by JPA

    public ChatMember(@NonNull User user, @NonNull Chat chat) {
        this.chatMemberId = new ChatMemberId(user.getId(), chat.getId());
        this.user = user;
        this.chat = chat;
        this.chatStatus = ChatStatus.ACTIVE;
    }

    public User getUser() {
        return user;
    }

    public Chat getChat() {
        return chat;
    }

    public ChatStatus getChatStatus() {
        return chatStatus;
    }

    public void setChatStatus(ChatStatus chatStatus) {
        if (chatStatus == null ) return;
        this.chatStatus = chatStatus;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        ChatMember that = (ChatMember) o;
        return this.chatMemberId.equals(that.chatMemberId);
    }

    @Override
    public int hashCode() {
        return chatMemberId.hashCode();
    }

    @Override
    public abstract String toString();

}