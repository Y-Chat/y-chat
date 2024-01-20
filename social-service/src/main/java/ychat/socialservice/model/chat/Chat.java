package ychat.socialservice.model.chat;

import jakarta.persistence.*;
import ychat.socialservice.model.user.User;
import ychat.socialservice.model.util.TimestampEntity;

import java.util.UUID;

/**
 * Allows the uniform handling of group and direct chats. It is its own table to allow polymorphic
 * queries while helping with consistency.
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Chat extends TimestampEntity {
    @Id
    private UUID id;

    public Chat() {
        id = UUID.randomUUID();
    }

    public abstract boolean toDeleteIfUserRemoved(User user);

    public UUID getId() {
        return id;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        Chat that = (Chat) o;
        return this.id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public abstract String toString();
}