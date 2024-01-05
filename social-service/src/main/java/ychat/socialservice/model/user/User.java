package ychat.socialservice.model.user;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/*
- Email is stored, validated and handled in the Auth Service
 */

@Entity
@Table(name = "users")
public class User {
    @Id
    @Column(name = "id")
    private UUID id;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "firstName", column = @Column(name = "first_name")),
        @AttributeOverride(name = "lastName", column = @Column(name = "last_name")),
        @AttributeOverride(name = "phoneNumber", column = @Column(name = "phone_number")),
        @AttributeOverride(name = "profileDescription", column = @Column(name = "profile_description")),
    })
    private UserProfile userProfile;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "twoFactorAuth", column = @Column(name = "two_factor_auth")),
        @AttributeOverride(name = "notifications", column = @Column(name = "notifications"))
    })
    private UserSettings userSettings;

    @ManyToMany
    @JoinTable(
        name = "blocked",
        joinColumns = @JoinColumn(name = "from_user_id"),
        inverseJoinColumns = @JoinColumn(name = "to_user_id")
    )
    private Set<User> blocked;

    protected User() {} // Required by JPA

    // id needs to be provided by the Auth Service
    public User(UUID id, UserProfile userProfile, UserSettings userSettings) {
        this.id = id;
        this.setUserProfile(userProfile);
        this.setUserSettings(userSettings);
        this.blocked = new HashSet<>();
    }

    public UUID getId() {
        return id;
    }

    public UserProfile getUserProfile() {
        return userProfile;
    }

    public void setUserProfile(UserProfile userProfile) {
        if (userProfile == null)
            throw new NullPointerException("UserProfile given to User was null.");
        if (userProfile.firstName() == null || userProfile.lastName() == null || userProfile.phoneNumber() == null)
            throw new NullPointerException("UserProfile given to User has null in firstName, lastName or phoneNumber.");
        if (userProfile.profileDescription() == null) {
            this.userProfile = new UserProfile(
                    userProfile.firstName(), userProfile.lastName(), userProfile.phoneNumber(),
                    "Hi, I am using Y-Chat, formerly known as WhatsUp."
            );
            return;
        }
        this.userProfile = userProfile;
    }

    public UserSettings getUserSettings() {
        return userSettings;
    }

    public void setUserSettings(UserSettings userSettings) {
        if (userSettings == null) {
            this.userSettings = new UserSettings(false, false);
            return;
        }
        boolean twoFactorAuth = userSettings.twoFactorAuth() != null ? userSettings.twoFactorAuth() : false;
        boolean notifications = userSettings.notifications() != null ? userSettings.notifications() : false;
        this.userSettings = new UserSettings(twoFactorAuth, notifications);
    }

    public Set<User> copyBlocked() {
        return new HashSet<>(this.blocked);
    }

    public boolean hasBlocked(User user) {
        return this.blocked.contains(user);
    }

    public void addBlocked(User user) {
        if (user == null)
            throw new NullPointerException("User given to addBlocked was null.");
        if (this.equals(user))
            throw new IllegalArgumentException("The following user tried to block themselves: " + user);
        this.blocked.add(user);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        User user = (User) o;
        return this.getId().equals(user.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getId());
    }

    @Override
    public String toString() {
        return "User{" + "id=" + id + ", userProfile=" + userProfile + '}';
    }
}