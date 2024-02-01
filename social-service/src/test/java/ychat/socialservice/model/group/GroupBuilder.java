package ychat.socialservice.model.group;

import ychat.socialservice.model.user.User;
import ychat.socialservice.model.user.UserBuilder;

public class GroupBuilder {
    private User initUser = new UserBuilder().build();
    private GroupProfile groupProfile = new GroupProfileBuilder().build();

    public GroupBuilder withInitUser(User initUser) {
        this.initUser = initUser;
        return this;
    }

    public GroupBuilder with(GroupProfileBuilder groupProfileBuilder) {
        this.groupProfile = groupProfileBuilder.build();
        return this;
    }

    public Group build() {
        return new Group(initUser, groupProfile);
    }
}
