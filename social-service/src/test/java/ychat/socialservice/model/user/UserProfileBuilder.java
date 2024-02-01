package ychat.socialservice.model.user;

public class UserProfileBuilder {
    private String firstName = "Alice";
    private String lastName = "Bob";

    public UserProfileBuilder withFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public UserProfileBuilder withLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public UserProfile build() {
        return new UserProfile(firstName, lastName);
    }
}
