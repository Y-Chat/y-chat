package ychat.socialservice.model.group;

import jakarta.persistence.Embeddable;

@Embeddable
public record GroupProfile(
        String name,
        String profileDescription
) {
    public GroupProfile() { // Required by JPA
        this(null, null);
    }
}