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
     * Does not work in production at the moment. Since we use automatic DDL generation in
     * production, I think the problem is that it creates a foreign key to chat instead of group
     * which disable the option to cast it to group even though we now that it is one.
     * <p>
     * In general, Hibernate forces the key to be in the superclass to have polymorphic queries.
     * It still works with a predefined schema since we must initialize GroupMember with a Group
     * and cannot change it afterwards.
     */
    public Group getGroup() {
        return (Group) getChat();
    }

    public Group getGroupFixed(GroupRepository groupRepository) {
        var chat = getChat();
        var group = groupRepository.findById(chat.getId());
        if(group.isEmpty()) throw new RuntimeException("Chat of of group member is not a Group. Invariant broken");
        return group.get();
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