package ychat.socialservice.model.user;

import jakarta.persistence.Embeddable;

/*
- All the user information we consider to be public to other users
 */

@Embeddable
public record UserProfile (
    String firstName,
    String lastName,
    String phoneNumber,
    String profileDescription
) {
    public UserProfile() { // Required by JPA
        this(null, null, null, null);
    }
}
