package ychat.socialservice.service;

import org.springframework.stereotype.Service;
import ychat.socialservice.model.group.GroupRole;

import java.util.Set;
import java.util.UUID;

@Service
public class GroupService {
    public UUID createGroup(UUID adminUserId, Set<UUID> userIds) {
        return UUID.randomUUID();
    }

    public GroupInfo getInfo(UUID groupId) {
        return null;
    }

    public Set<UUID> getUsers(UUID groupId) {
        return null;
    }

    public void addUsers(UUID groupId, Set<UUID> userIds) {
    }

    public void removeUsers(UUID groupId, Set<UUID> userIds) {
    }

    public void updateInfo(UUID groupId, GroupInfo groupInfo) {
    }

    public void updateRoles(UUID groupId, Set<GroupRole> groupRoles) {
    }

    public Set<GroupRole> getRoles(UUID groupId) {
        return null;
    }
}
