package ychat.socialservice.model.util;

import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;

import java.time.LocalDateTime;

/**
 * Adds created and modified timestamps to all subclasses.
 */
@MappedSuperclass
public abstract class TimestampEntity {
    private LocalDateTime created;

    private LocalDateTime modified;

    public LocalDateTime getCreated() {
        return created;
    }

    public LocalDateTime getModified() {
        return modified;
    }

    @PrePersist
    protected void onCreate() {
        this.created = LocalDateTime.now();
        this.modified = this.created;
    }

    @PreUpdate
    protected void onUpdate() {
        this.modified = LocalDateTime.now();
    }

    @Override
    public abstract boolean equals(Object o);

    @Override
    public abstract int hashCode();

    @Override
    public abstract String toString();
}
