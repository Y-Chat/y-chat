package ychat.socialservice.model.chat;

import ychat.socialservice.model.user.User;

import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public abstract class Chat {
    private UUID id;

    public Chat() {
        this.id = UUID.randomUUID();
    }

    public abstract Set<User> getMembers();

    public UUID getId() {
        return this.id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        Chat chat = (Chat) o;
        return this.getId().equals(chat.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getId());
    }

    public abstract String toString();
}