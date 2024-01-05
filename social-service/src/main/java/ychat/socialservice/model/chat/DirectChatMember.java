package ychat.socialservice.model.chat;

import ychat.socialservice.model.user.User;

import java.util.Objects;

public class DirectChatMember extends ChatMember {
    private DirectChat directChat;

    public DirectChatMember(DirectChat directChat, User user, ChatStatus chatStatus) {
        super(user, chatStatus);
        if (directChat == null)
            throw new NullPointerException("Null DirectChat was passed to DirectChatMember.");
        this.directChat = directChat;
    }

    public DirectChat getDirectChat() {
        return this.directChat;
    }

    @Override
    public void setChatStatus(ChatStatus chatStatus) {
        if (chatStatus == null)
            throw new NullPointerException("Null ChatStatus was passed to DirectChatMember.");
        this.chatStatus = chatStatus;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        DirectChatMember member = (DirectChatMember) o;
        return this.getDirectChat().equals(member.getDirectChat())
            && this.getUser().equals(member.getUser());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getDirectChat(), this.getUser());
    }

    @Override
    public String toString() {
        return "DirectChatMember{" + "directChat=" + this.getDirectChat()
                + ", user=" + this.getUser() + '}';
    }
}
