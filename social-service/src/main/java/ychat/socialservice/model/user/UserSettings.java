package ychat.socialservice.model.user;

import jakarta.persistence.Embeddable;

import java.util.Objects;

/**
 * Stores the settings for a user. Will not necessarily be implemented as a feature but such a
 * record would most likely become necessary eventually.
 */
@Embeddable
public class UserSettings {
    private boolean readReceipts;
    private boolean lastSeen;

    public UserSettings() {
        this.readReceipts = true;
        this.lastSeen = true;
    }

    public boolean isReadReceipts() {
        return readReceipts;
    }

    public void setReadReceipts(Boolean readReceipts) {
        if (readReceipts == null) return;
        this.readReceipts = readReceipts;
    }

    public boolean isLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(Boolean lastSeen) {
        if (lastSeen == null) return;
        this.lastSeen = lastSeen;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        UserSettings that = (UserSettings) o;
        return this.readReceipts == that.readReceipts && this.lastSeen == that.lastSeen;
    }

    @Override
    public int hashCode() {
        return Objects.hash(readReceipts, lastSeen);
    }

    @Override
    public String toString() {
        return "UserSettings{" + "readReceipts=" + readReceipts + ", lastSeen=" + lastSeen + '}';
    }
}
