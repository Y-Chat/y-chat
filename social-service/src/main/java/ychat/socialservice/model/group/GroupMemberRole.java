package ychat.socialservice.model.group;

import java.util.UUID;

/*
- Serves as a DTO
 */

public record GroupMemberRole (
    UUID id,
    GroupRole role
) {}
