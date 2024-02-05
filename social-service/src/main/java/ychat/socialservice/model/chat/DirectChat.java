package ychat.socialservice.model.chat;

import jakarta.persistence.*;
import lombok.NonNull;
import ychat.socialservice.model.user.User;

import java.util.*;

/**
 * The connection of a user to a direct chat. Owns the DirectChatMember entity. It is important to
 * mention that a direct chat can also consist of just a single member, as would be the case when
 * one user is deleted and the other still needs access to their messages.
 */
@Entity
@Table(name = "direct_chat")
public class DirectChat extends Chat {
    @OneToMany(
        mappedBy = "chat", cascade = CascadeType.ALL, fetch = FetchType.LAZY,
        orphanRemoval = true
    )
    private Set<DirectChatMember> members;

    protected DirectChat() {} // Required by JPA

    public DirectChat(@NonNull User fstUser, @NonNull User sndUser) {
        super();
        if (fstUser.equals(sndUser)) {
            throw new IllegalArgumentException(
                "User is not allowed to have a direct chat with themselves: " + fstUser
            );
        }
        this.members = new HashSet<>();
        this.members.add(new DirectChatMember(fstUser, this, sndUser.getId()));
        this.members.add(new DirectChatMember(sndUser, this, fstUser.getId()));
    }

    @Override
    public boolean toDeleteIfUserRemoved(User user) {
        DirectChatMember otherMember = getOtherMember(user);
        if (otherMember == null) return true;
        return otherMember.getChatStatus() == ChatStatus.DELETED;
    }

    @Override
    public boolean isMember(User user) {
        if (user == null) return false;
        DirectChatMember directChatMember = new DirectChatMember(
            user, this, new UUID(0,0)
        );
        return members.contains(directChatMember);
    }

    @Override
    public void removeMember(User user) {
        if (user == null) return;
        if (members.size() == 1) return;
        DirectChatMember directChatMember = new DirectChatMember(
            user, this, new UUID(0,0)
        );
        members.remove(directChatMember);
    }

    @Override
    public DirectChatMember getMember(User user) {
        for (DirectChatMember member : members) {
            if (user.equals(member.getUser()))
                return member;
        }
        return null;
    }

    public DirectChatMember getOtherMember(User user) {
        for (DirectChatMember member : members) {
            if (!user.equals(member.getUser()))
                return member;
        }
        return null;
    }

    @Override
    public String toString() {
        return "DirectChat{" + "id=" + getId() + '}';
    }
}
