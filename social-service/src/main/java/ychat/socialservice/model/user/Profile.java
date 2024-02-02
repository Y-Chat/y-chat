package ychat.socialservice.model.user;

import jakarta.persistence.MappedSuperclass;

import java.util.UUID;

/**
 * Common functionality for both user and group profiles
 */
@MappedSuperclass
public abstract class Profile {
    private String profilePictureId;

    private String profileDescription;

    public Profile() {
        defaultProfileDescription();
    }

    protected abstract void defaultProfileDescription();

    public String getProfilePictureId() {
        return profilePictureId;
    }

    public void setProfilePictureId(String profilePictureId) {
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
