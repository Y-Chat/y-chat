package ychat.socialservice.model.util;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PreUpdate;

import java.time.LocalDateTime;

// TODO only have timestampentity
@MappedSuperclass
public abstract class UpdateEntity extends CreateEntity {
    @Column(name = "updated")
    private LocalDateTime updated;

    @PreUpdate
    protected void onUpdate() {
        this.updated = LocalDateTime.now();
    }

    public LocalDateTime getUpdated() {
        return updated;
    }
}
