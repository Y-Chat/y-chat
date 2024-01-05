package ychat.socialservice.model.group;

import jakarta.persistence.*;
import ychat.socialservice.model.user.User;

@Entity
@Table(name = "group_members")
public class GroupMember {
    @Id
    @ManyToOne
    @JoinColumn(name = "group_chat_id")
    private GroupChat groupChat;

    @Id
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    private GroupRole groupRole;
    private boolean chatArchived;

    public GroupMember() {}
}
