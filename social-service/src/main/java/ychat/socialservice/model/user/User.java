package ychat.socialservice.model.user;

import jakarta.persistence.*;
import lombok.NonNull;
import ychat.socialservice.util.LimitReachedException;
import ychat.socialservice.model.util.TimestampEntity;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * The User entity of our domain model. Owns the blocking relation. Does not have a field for the
 * chat members as they are potentially unbounded.
 */
@Entity
@Table(name = "\"user\"") // User is a reserved keyword in postgres
public class User extends TimestampEntity {
    private static final int BLOCK_LIMIT = 1000;

    @Id
    private UUID id;

    @Embedded
    private UserProfile userProfile;

    @Embedded
    private UserSettings userSettings;

    @OneToMany(
        mappedBy = "fromUser", cascade = CascadeType.ALL, fetch = FetchType.LAZY,
        orphanRemoval = true
    )
    private Set<BlockedUser> blockedUsers;

    protected User() {} // Required by JPA

    public User(@NonNull UUID id, @NonNull UserProfile userProfile) {
        this.id = id;
        this.userProfile = userProfile;
        this.userSettings = new UserSettings(true, true);
        blockedUsers = new HashSet<>();
    }

    public UUID getId() {
        return id;
    }

    public UserProfile getUserProfile() {
        return userProfile;
    }

    public UserSettings getUserSettings() {
        return userSettings;
    }

    // One can fetch all blocked users via a paginated request to the BlockedUserRepository

    public boolean isBlockedUser(User user) {
        if (user == null) return false;
        return blockedUsers.contains(new BlockedUser(this, user));
    }

    public BlockedUser addBlockedUser(User user) {
        if (user == null) return null;
        if (blockedUsers.size() >= User.BLOCK_LIMIT) {
            throw new LimitReachedException(
                "User reached the block limit of " + User.BLOCK_LIMIT + ": " + user
            );
        }
        BlockedUser blockedUser = new BlockedUser(this, user);
        blockedUsers.add(blockedUser);
        return blockedUser;
    }

    public void removeBlockedUser(User user) {
        if (user == null) return;
        BlockedUser blockedUser = new BlockedUser(this, user);
        blockedUsers.remove(blockedUser);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        User that = (User) o;
        return this.id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return "User{" + "id=" + id + ", userProfile=" + userProfile + '}';
    }
}