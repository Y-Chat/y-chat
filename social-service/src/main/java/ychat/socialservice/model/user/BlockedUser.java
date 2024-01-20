package ychat.socialservice.model.user;

import jakarta.persistence.*;
import lombok.NonNull;
import ychat.socialservice.model.util.TimestampEntity;

import java.util.Objects;

/**
 * Holds the blocking relation for the User entity. This entity is owned by the User entity, all
 * operations on it happen via cascading on the User entity.
 */
@Entity
@Table(name = "blocked_user")
public class BlockedUser extends TimestampEntity {
    @EmbeddedId
    private BlockedUserId blockedUserId;

    @ManyToOne
    @MapsId("fromUserId")
    @JoinColumn
    private User fromUser;

    @ManyToOne
    @MapsId("toUserId")
    @JoinColumn
    private User toUser;

    protected BlockedUser() {} // Required by JPA

    public BlockedUser(@NonNull User fromUser, @NonNull User toUser) {
        this.blockedUserId = new BlockedUserId(fromUser.getId(), toUser.getId());
        this.fromUser = fromUser;
        this.toUser = toUser;
    }

    public User getFromUser() {
        return fromUser;
    }

    public User getToUser() {
        return toUser;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BlockedUser that = (BlockedUser) o;
        return this.blockedUserId.equals(that.blockedUserId);
    }

    @Override
    public int hashCode() {
        return blockedUserId.hashCode();
    }

    @Override
    public String toString() {
        return "Blocked{" + "fromUser=" + fromUser + ", toUser=" + toUser + '}';
    }
}
