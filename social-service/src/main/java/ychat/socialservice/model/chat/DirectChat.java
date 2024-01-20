package ychat.socialservice.model.chat;

import jakarta.persistence.*;
import lombok.NonNull;
import ychat.socialservice.util.IllegalUserInputException;
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
            throw new IllegalUserInputException(
                "User is not allowed to have a direct chat with themselves: " + fstUser
            );
        }
        this.members = new HashSet<>();
        this.members.add(new DirectChatMember(fstUser, this, sndUser.getId()));
        this.members.add(new DirectChatMember(sndUser, this, fstUser.getId()));
    }

    @Override
    public boolean toDeleteIfUserRemoved(User user) {
        Optional<DirectChatMember> optionalDirectChatMember = getOtherMember(user);
        if (optionalDirectChatMember.isEmpty())
            return true;
        DirectChatMember otherMember = optionalDirectChatMember.get();
        return otherMember.getChatStatus() == ChatStatus.DELETED;
    }

    public Optional<DirectChatMember> getMember(User user) {
        for (DirectChatMember member : members) {
            if (user.equals(member.getUser()))
                return Optional.of(member);
        }
        return Optional.empty();
    }

    public Optional<DirectChatMember> getOtherMember(User user) {
        for (DirectChatMember member : members) {
            if (!user.equals(member.getUser()))
                return Optional.of(member);
        }
        return Optional.empty();
    }

    @Override
    public String toString() {
        return "DirectChat{" + "id=" + getId() + ", members=" + members + '}';
    }
}
