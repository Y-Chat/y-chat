package ychat.socialservice.model.chat;

import ychat.socialservice.model.user.User;

public abstract class ChatMember {
    private User user;
    protected ChatStatus chatStatus;

    public ChatMember(User user, ChatStatus chatStatus) {
        if (user == null)
            throw new NullPointerException("Null User was passed to ChatMember.");
        this.user = user;
        this.setChatStatus(chatStatus);
    }

    public User getUser() {
        return this.user;
    }

    public ChatStatus getChatStatus() {
        return this.chatStatus;
    }

    public abstract void setChatStatus(ChatStatus chatStatus);

    public abstract boolean equals(Object o);

    public abstract int hashCode();

    public abstract String toString();

}
