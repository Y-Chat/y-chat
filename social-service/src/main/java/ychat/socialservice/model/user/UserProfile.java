package ychat.socialservice.model.user;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.util.Objects;

/**
 * All the user information we consider to be public to other users.
 */
@Embeddable
public class UserProfile extends Profile {
    private String firstName;

    private String lastName;

    protected UserProfile() {} // Required by JPA

    public UserProfile(String firstName, String lastName) {
        super();
        this.firstName = firstName;
        this.lastName = lastName;
    }

    @Override
    protected void defaultProfileDescription() {
        setProfileDescription("Hi, I am using YChat.");
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        if (firstName == null) return;
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        if (lastName == null) return;
        this.lastName = lastName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserProfile that = (UserProfile) o;
        return Objects.equals(this.firstName, that.firstName)
            && Objects.equals(this.lastName, that.lastName)
            && Objects.equals(this.getProfilePictureId(), that.getProfilePictureId())
            && Objects.equals(this.getProfileDescription(), that.getProfileDescription());
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstName, lastName, getProfilePictureId(), getProfileDescription());
    }

    @Override
    public String toString() {
        return "UserProfile{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", profilePictureId=" + getProfilePictureId() +
                ", profileDescription='" + getProfileDescription() + '\'' +
                '}';
    }
}