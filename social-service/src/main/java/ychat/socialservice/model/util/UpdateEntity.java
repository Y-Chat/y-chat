package ychat.socialservice.model.util;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;

import java.time.LocalDateTime;

@MappedSuperclass
public abstract class CreationUpdateTimeEntity extends CreationTimeEntity {
    @Column(name = "updated")
    private LocalDateTime updated;

    @PrePersist
    protected void onCreate() {
        this.up = LocalDateTime.ofInstant(Instant.now(), ZoneOffset.UTC);
    }
}
