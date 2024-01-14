package ychat.socialservice.model.user;

import jakarta.persistence.*;
import ychat.socialservice.model.util.CreationTimestampEntity;

import java.util.Objects;

@Entity
@Table(name = "blocked")
public class Blocked extends CreationTimestampEntity {
    @EmbeddedId
    @AttributeOverrides({
        @AttributeOverride(name = "fromId", column = @Column(name = "from_id")),
        @AttributeOverride(name = "toId", column = @Column(name = "to_id")),
    })
    private BlockedId blockedId;

    @ManyToOne
    @MapsId("fromId")
    @JoinColumn(name = "from_id")
    private User fromUser;

    @ManyToOne
    @MapsId("toId")
    @JoinColumn(name = "to_id")
    private User toUser;

    public Blocked() {} // Required by JPA

    public Blocked(User fromUser, User toUser) {
        super();
        this.blockedId = new BlockedId(fromUser.getId(), toUser.getId());
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
        if (o == null || this.getClass() != o.getClass()) return false;
        Blocked blocked = (Blocked) o;
        return this.blockedId.equals(blocked.blockedId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.blockedId);
    }

    @Override
    public String toString() {
        return "Blocked{" + "fromUser=" + fromUser + ", toUser=" + toUser + '}';
    }
}
