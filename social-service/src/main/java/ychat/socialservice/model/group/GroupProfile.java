package ychat.socialservice.model.group;

import jakarta.persistence.Embeddable;
import ychat.socialservice.model.user.Profile;

import java.util.Objects;

@Embeddable
public class GroupProfile extends Profile {
    private String groupName;

    protected GroupProfile() {} // Required by JPA

    public GroupProfile(String groupName) {
        super();
        this.groupName = groupName;
    }

    @Override
    protected void defaultProfileDescription() {
        setProfileDescription("I am groop.");
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        if (groupName == null) return;
        this.groupName = groupName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GroupProfile that = (GroupProfile) o;
        return Objects.equals(this.groupName, that.groupName)
            && Objects.equals(this.getProfilePictureId(), that.getProfilePictureId())
            && Objects.equals(this.getProfileDescription(), that.getProfileDescription());
    }

    @Override
    public int hashCode() {
        return Objects.hash(groupName, getProfilePictureId(), getProfileDescription());
    }

    @Override
    public String toString() {
        return "GroupProfile{" + "groupName='" + groupName + '\'' +
                ", profilePictureId=" + getProfilePictureId() +
                ", profileDescription='" + getProfileDescription() + '\'' +
                '}';
    }
}
