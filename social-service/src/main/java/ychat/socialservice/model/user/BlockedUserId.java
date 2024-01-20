package ychat.socialservice.model.user;

import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

/**
 * Embedded id for the BlockedUser entity. Owned and only accessed through the BlockedUser.
 */
@Embeddable
public class BlockedUserId implements Serializable {
    private UUID fromUserId;
    private UUID toUserId;

    protected BlockedUserId() {}

    public BlockedUserId(UUID fromUserId, UUID toUserId) {
        this.fromUserId = fromUserId;
        this.toUserId = toUserId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BlockedUserId that = (BlockedUserId) o;
        return Objects.equals(this.fromUserId, that.fromUserId)
            && Objects.equals(this.toUserId, that.toUserId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fromUserId, toUserId);
    }

    @Override
    public String toString() {
        return "BlockedUserId{" + "fromUserId=" + fromUserId + ", toUserId=" + toUserId + '}';
    }
}
