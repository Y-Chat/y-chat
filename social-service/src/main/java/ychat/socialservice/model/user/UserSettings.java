package ychat.socialservice.model.user;

import jakarta.persistence.Embeddable;

/*
- Will not necessarily be implemented as a feature but such a class would most likely become necessary
 */

@Embeddable
public record UserSettings (
    Boolean twoFactorAuth,
    Boolean notifications
) {
    public UserSettings() { // Required by JPA
        this(null, null);
    }
}
