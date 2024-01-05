package ychat.socialservice.model.chat;

import ychat.socialservice.model.user.User;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DirectChat extends Chat {
    private List<DirectChatMember> members;

    public DirectChat(User fstUser, User sndUser) {
        super();
        if (fstUser == null || sndUser == null)
            throw new NullPointerException("Null fstUser or sndUser was passed to DirectChat.");
        if (fstUser.equals(sndUser)) {
            throw new IllegalArgumentException(
                "The following User tried to create a DirectChat with themselves" + fstUser
            );
        }
        this.members = new ArrayList<>();
        this.members.add(new DirectChatMember(this, fstUser, ChatStatus.ACTIVE));
        this.members.add(new DirectChatMember(this, sndUser, ChatStatus.ACTIVE));
    }

    @Override
    public Set<User> getMembers() {
        HashSet<User> users = new HashSet<>();
        for(DirectChatMember member : this.members) {
            users.add(member.getUser());
        }
        return users;
    }

    @Override
    public String toString() {
        return "DirectChat{" + "id=" + this.getId() + ", members=" + this.getMembers() + '}';
    }
}
