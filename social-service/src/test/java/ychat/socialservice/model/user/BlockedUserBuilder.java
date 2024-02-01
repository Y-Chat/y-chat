package ychat.socialservice.model.user;

import java.util.UUID;

public class BlockedUserBuilder {
    private User fromUser = new UserBuilder().withId(new UUID(0,0)).build();
    private User toUser = new UserBuilder().withId(new UUID(0,1)).build();

    public BlockedUserBuilder withFromUser(UserBuilder userBuilder) {
        this.fromUser = userBuilder.build();
        return this;
    }

    public BlockedUserBuilder withToUser(UserBuilder userBuilder) {
        this.toUser = userBuilder.build();
        return this;
    }

    public BlockedUser build() {
        return new BlockedUser(fromUser, toUser);
    }
}
