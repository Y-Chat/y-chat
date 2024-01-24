package ychat.socialservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ychat.socialservice.model.chat.Chat;
import ychat.socialservice.model.chat.ChatMemberId;
import ychat.socialservice.model.group.GroupMember;
import ychat.socialservice.model.user.User;

import java.util.Optional;

@Repository
public interface GroupMemberRepository extends JpaRepository<GroupMember, ChatMemberId> {
    Optional<GroupMember> findByUserAndChat(User user, Chat chat);
}