package ychat.socialservice.model.user;

import jakarta.persistence.MappedSuperclass;

import java.util.UUID;

/**
 * Common functionality for both user and group profiles
 */
@MappedSuperclass
public abstract class Profile {
    private UUID profilePictureId;

    private String profileDescription;

    protected Profile() {} // Required by JPA

    public Profile(String profileDescription) {
        if (profileDescription == null)
            defaultProfileDescription();
        else
            this.profileDescription = profileDescription;
    }

    protected abstract void defaultProfileDescription();

    public UUID getProfilePictureId() {
        return profilePictureId;
    }

    public void setProfilePictureId(UUID profilePictureId) {
        if (profilePictureId == null) return;
        this.profilePictureId = profilePictureId;
    }

    public void removeProfilePictureId() {
        profilePictureId = null;
    }

    public String getProfileDescription() {
        return profileDescription;
    }

    public void setProfileDescription(String profileDescription) {
        if (profileDescription == null) return;
        this.profileDescription = profileDescription;
    }

    @Override
    public abstract boolean equals(Object o);

    @Override
    public abstract int hashCode();

    @Override
    public abstract String toString();
}
