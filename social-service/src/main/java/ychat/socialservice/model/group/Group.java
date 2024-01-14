package ychat.socialservice.model.group;

import jakarta.persistence.*;
import ychat.socialservice.model.chat.Chat;
import ychat.socialservice.model.user.User;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

// TODO persistence cascade and fetch

@Entity
@Table(name = "groups")
public class GroupChat extends Chat {
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "name", column = @Column(name = "group_name")),
        @AttributeOverride(
            name = "profileDescription",
            column = @Column(name = "profile_description")
        ),
    })
    private GroupProfile groupProfile;

    // TODO be able to fetch groupMembers and check for membership and how to add etc
    @OneToMany(mappedBy = "groupChat")
    private Set<GroupMember> groupMembers;

    public GroupChat(User initUser, GroupProfile groupProfile) {
        super();
        this.setGroupProfile(groupProfile);
        GroupMember initGroupMember = new GroupMember(
            initUser, this, GroupRole.GROUP_ADMIN
        );
        this.groupMembers = new HashSet<>();
        this.groupMembers.add(initGroupMember);
    }

    public GroupChat() {} // Required by JPA

    @Override
    public Set<UUID> getMemberIds() {
        HashSet<UUID> memberIds = new HashSet<>();
        for (GroupMember groupMember : groupMembers)
            memberIds.add(groupMember.getUser().getId());
        return memberIds;
    }

    public GroupProfile getGroupProfile() {
        return groupProfile;
    }

    public void setGroupProfile(GroupProfile groupProfile) {
        if (groupProfile == null)
            throw new NullPointerException("GroupProfile given to Group was null.");
        if (groupProfile.name() == null)
            throw new NullPointerException("GroupProfile given to Group has null in name.");
        if (groupProfile.profileDescription() == null) {
            this.groupProfile = new GroupProfile(groupProfile.name(), "I am groop.");
            return;
        }
        this.groupProfile = groupProfile;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        GroupChat groupChat = (GroupChat) o;
        return this.getId().equals(groupChat.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getId());
    }

    @Override
    public String toString() {
        return "Group{" + "id=" + this.getId() + ", groupProfile=" + this.getGroupProfile() + '}';
    }
}