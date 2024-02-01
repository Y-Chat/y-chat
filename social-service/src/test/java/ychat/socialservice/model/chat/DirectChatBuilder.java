package ychat.socialservice.model.chat;

import ychat.socialservice.model.user.User;
import ychat.socialservice.model.user.UserBuilder;

import java.util.UUID;

public class DirectChatBuilder {
    private User fstUser = new UserBuilder().withId(new UUID(0,0)).build();
    private User sndUser = new UserBuilder().withId(new UUID(0,1)).build();

    public DirectChatBuilder withFstUser(User fstUser) {
        this.fstUser = fstUser;
        return this;
    }

    public DirectChatBuilder withSndUser(User sndUser) {
        this.sndUser = sndUser;
        return this;
    }

    public DirectChat build() {
        return new DirectChat(fstUser, sndUser);
    }
}
