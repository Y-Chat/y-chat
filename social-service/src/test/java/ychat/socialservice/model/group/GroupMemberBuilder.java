package ychat.socialservice.model.group;

import ychat.socialservice.model.user.User;
import ychat.socialservice.model.user.UserBuilder;

/**
 * Attention! Does not provide a valid relationship to groups, only good for testing the group
 * member functions, as in the real context a group member is not created directly.
 */
public class GroupMemberBuilder {
    private User user = new UserBuilder().build();

    public GroupMemberBuilder withUser(User user) {
        this.user = user;
        return this;
    }

    public GroupMember build() {
        Group group = new GroupBuilder().withInitUser(user).build();
        GroupMember groupMember = new GroupMember(user, group);
        groupMember.setGroupRole(GroupRole.GROUP_ADMIN);
        return groupMember;
    }
}
