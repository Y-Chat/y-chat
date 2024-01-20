package ychat.socialservice.model.group;

import jakarta.persistence.*;
import lombok.NonNull;
import ychat.socialservice.model.chat.ChatMember;
import ychat.socialservice.model.user.User;

/**
 * Models the many-to-many relationship between users and groups. This entity is owned by the group.
 */
@Entity
@Table(name = "group_member")
public class GroupMember extends ChatMember {
    @Enumerated(EnumType.STRING)
    private GroupRole groupRole;

    protected GroupMember() {} // Required by JPA

    public GroupMember(User user, @NonNull Group group) {
        super(user, group);
        this.groupRole = GroupRole.GROUP_MEMBER;
    }

    public Group getGroup() {
        return (Group) getChat();
    }

    public GroupRole getGroupRole() {
        return groupRole;
    }

    public void setGroupRole(GroupRole groupRole) {
        if (groupRole == null) return;
        this.groupRole = groupRole;
    }


    // equals and hashCode are implemented in the superclass

    @Override
    public String toString() {
        return "GroupMember{" + "user=" + getUser() + ", group=" + getChat() +
                ", groupRole=" + groupRole + ", chatStatus=" + getChatStatus() + '}';
    }
}