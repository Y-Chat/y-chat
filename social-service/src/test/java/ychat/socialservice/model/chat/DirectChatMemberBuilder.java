package ychat.socialservice.model.chat;

import ychat.socialservice.model.group.GroupBuilder;
import ychat.socialservice.model.user.User;
import ychat.socialservice.model.user.UserBuilder;

import java.util.UUID;

/**
 * Attention! Does not provide a valid relationship to chats, only good for testing the chat member
 * functions, as in the real context a chat member is not created directly.
 */
public class DirectChatMemberBuilder {
    private User fstUser = new UserBuilder().withId(new UUID(0,0)).build();
    private User sndUser = new UserBuilder().withId(new UUID(0,1)).build();

    public DirectChatMemberBuilder withFstuser(User fstUser) {
        this.fstUser = fstUser;
        return this;
    }

    public DirectChatMemberBuilder withSndUser(User sndUser) {
        this.sndUser = sndUser;
        return this;
    }

    public DirectChatMember build() {
        DirectChat directChat =
            new DirectChatBuilder().withFstUser(fstUser).withSndUser(sndUser).build();
        return new DirectChatMember(fstUser, directChat, sndUser.getId());
    }
}
