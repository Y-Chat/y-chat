package ychat.socialservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ychat.socialservice.model.chat.ChatMemberId;
import ychat.socialservice.model.group.GroupMember;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface GroupMemberRepository extends JpaRepository<GroupMember, ChatMemberId> {
    Optional<GroupMember> findByUserIdAndChatId(UUID userId, UUID chatID);
}