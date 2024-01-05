package ychat.socialservice.service;

import org.springframework.stereotype.Service;
import ychat.socialservice.exceptions.EntityAlreadyExistsException;
import ychat.socialservice.model.user.User;
import ychat.socialservice.repository.UserRepository;
import lombok.NonNull;

import java.util.Set;
import java.util.UUID;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void createUser(@NonNull UUID userId, @NonNull UserInfo userInfo) {
        if (userRepository.existsById(userId))
            throw new EntityAlreadyExistsException("User " + userId.toString() + " exists already.");
        User user = new User(userId, )
    }

    public UserInfo getInfo(UUID userId) {
        return null;
    }


    public Set<UUID> getChats(UUID userId) {
        return null;
    }

    public Set<UUID> getGroups(UUID userId) {
        return null;
    }

    public void updateInfo(UUID userId, UserInfo userInfo) {
    }

    public void addBlocked(UUID userId, Set<UUID> blockedIds) {
    }

    public void removeBlocked(UUID userId, Set<UUID> unblockedIds) {
    }

    public Set<UUID> getBlocked(UUID userId) {
    }

    public PublicUserInfo[] getPublicUserInfo(UUID[] userIds) {
        return null;
    }
}
