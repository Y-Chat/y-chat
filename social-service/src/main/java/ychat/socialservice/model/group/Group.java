package ychat.socialservice.model.group;

import jakarta.persistence.*;
import lombok.NonNull;
import ychat.socialservice.util.LimitReachedException;
import ychat.socialservice.model.chat.Chat;
import ychat.socialservice.model.user.User;

import java.util.HashSet;
import java.util.Set;

/**
 * A group is collection of users with a common chat. Internally, a group is a type of chat, so one
 * can think of group as group chat. Owns the group member entity.
 */
@Entity
@Table(name = "\"group\"") // Group is a reserved keyword in postgres
public class Group extends Chat {
    private static final int MEMBER_LIMIT = 1000;

    @Embedded
    private GroupProfile groupProfile;

    @OneToMany(
        mappedBy = "chat", cascade = CascadeType.ALL, fetch = FetchType.LAZY,
        orphanRemoval = true
    )
    private Set<GroupMember> groupMembers;

    protected Group() {} // Required by JPA

    public Group(@NonNull User initUser, @NonNull GroupProfile groupProfile) {
        super();
        this.groupProfile = groupProfile;

        GroupMember initGroupMember = new GroupMember(initUser, this);
        initGroupMember.setGroupRole(GroupRole.GROUP_ADMIN);
        this.groupMembers = new HashSet<>();
        this.groupMembers.add(initGroupMember);
    }

    @Override
    public boolean toDeleteIfUserRemoved(User user) {
        return groupMembers.size() == 1 && isGroupMember(user);
    }

    public GroupProfile getGroupProfile() {
        return groupProfile;
    }

    // One can fetch members via the GroupMemberRepository

    public boolean isGroupMember(User user) {
        if (user == null) return false;
        return groupMembers.contains(new GroupMember(user, this));
    }

    public GroupMember addGroupMember(User user) {
        if (user == null) return null;
        if (groupMembers.size() >= Group.MEMBER_LIMIT) {
            throw new LimitReachedException(
                "User reached the block limit of " + Group.MEMBER_LIMIT + ": " + user
            );
        }
        GroupMember groupMember = new GroupMember(user, this);
        groupMembers.add(groupMember);
        return groupMember;
    }

    public void removeGroupMember(User user) {
        if (user == null) return;
        GroupMember groupMember = new GroupMember(user, this);
        groupMembers.remove(groupMember);
    }

    // hashCode and equals work on the id in the superclass

    @Override
    public String toString() {
        return "Group{" + "id=" + getId() + ", groupProfile=" + groupProfile + '}';
    }
}