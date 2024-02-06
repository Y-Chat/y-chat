package com.ychat.ychat.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Document
public class UserFirebaseTokenMapping {

    @Id
    private UUID userId;

    private Set<FirebaseToken> fcmTokens;

    public UserFirebaseTokenMapping(UUID userId, Set<FirebaseToken> fcmTokens) {
        this.userId = userId;
        this.fcmTokens = fcmTokens;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public Set<FirebaseToken> getFcmTokens() {
        return fcmTokens;
    }

    public void setFcmTokens(Set<FirebaseToken> fcmTokens) {
        this.fcmTokens = fcmTokens;
    }

    public static class FirebaseToken {
        private String token;
        private LocalDateTime timestamp;

        public FirebaseToken(String token, LocalDateTime timestamp) {
            this.token = token;
            this.timestamp = timestamp;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public LocalDateTime getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(LocalDateTime timestamp) {
            this.timestamp = timestamp;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            FirebaseToken that = (FirebaseToken) o;
            return Objects.equals(token, that.token);
        }

        @Override
        public int hashCode() {
            return Objects.hash(token);
        }
    }
}
