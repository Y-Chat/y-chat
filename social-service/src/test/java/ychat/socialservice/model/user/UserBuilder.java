package ychat.socialservice.model.user;

import java.util.UUID;

public class UserBuilder {
    private UUID id = UUID.fromString("00000000-0000-0000-0000-000000000000");
    private UserProfile userProfile = new UserProfileBuilder().build();

    public UserBuilder withId(UUID id) {
        this.id = id;
        return this;
    }

    public UserBuilder with(UserProfileBuilder userProfileBuilder) {
        this.userProfile = userProfileBuilder.build();
        return this;
    }

    public User build() {
        return new User(id, userProfile);
    }
}
