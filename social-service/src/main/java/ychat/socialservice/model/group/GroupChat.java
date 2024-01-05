package ychat.socialservice.model.group;

import jakarta.persistence.*;
import ychat.socialservice.model.chat.Chat;
import ychat.socialservice.model.user.User;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "groups")
public class GroupChat extends Chat {
    @Id
    @Column(name = "id")
    private UUID id;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "name", column = @Column(name = "group_name")),
        @AttributeOverride(name = "profileDescription", column = @Column(name = "profile_description")),
    })
    private GroupProfile groupProfile;

    @OneToMany(mappedBy = "groupChat")
    private Set<GroupMember> groupMembers;

    public GroupChat() {} // Required by JPA

    public GroupChat(User initUser, GroupProfile groupProfile) {
        this.id = UUID.randomUUID();
        this.setGroupProfile(groupProfile);
        GroupMember initGroupMember = null; // TODO
        this.groupMembers = new HashSet<>(); //TODO add
    }

    @Override
    public Set<User> getMembers() {
        return null; // TODO
    }

    public UUID getId() {
        return id;
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
        return "Group{" + "id=" + id + ", groupProfile=" + groupProfile + '}';
    }
}