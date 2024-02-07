package ychat.socialservice.model.group;

import jakarta.persistence.*;
import lombok.NonNull;
import ychat.socialservice.model.chat.Chat;
import ychat.socialservice.model.chat.ChatMember;
import ychat.socialservice.model.user.User;
import ychat.socialservice.repository.GroupRepository;

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

    /**
     * I would have prefered to have a Group variable here as well but Hibernate will not allow it
     * for the composite key and thus the polymorphic queries. The data model is not broken because
     * of this because GroupMember can only be initialized with a Group not another type of Chat
     * and the Chat cannot be changed.
     */
    public Group getGroup() {
        return (Group) getChat();
    }

    public GroupRole getGroupRole() {
        return groupRole;
    }

    public void setGroupRole(GroupRole groupRole) {
        if (groupRole == null) return;
        if (groupRole == GroupRole.NOT_A_MEMBER) return;
        if (groupRole != GroupRole.GROUP_ADMIN && getGroup().getNumberOfAdmins() == 1) return;
        this.groupRole = groupRole;
    }

    // equals and hashCode are implemented in the superclass

    @Override
    public String toString() {
        return "GroupMember{" + "user=" + getUser() + ", group=" + getGroup() +
                ", groupRole=" + groupRole + ", chatStatus=" + getChatStatus() + '}';
    }
}