package ychat.socialservice.model.user;

import java.io.Serializable;
import java.util.UUID;

public record BlockedId (UUID fromId, UUID toId) implements Serializable {}
