package ychat.socialservice.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ychat.socialservice.model.chat.Chat;
import ychat.socialservice.model.chat.ChatMember;
import ychat.socialservice.model.chat.ChatMemberId;
import ychat.socialservice.model.user.User;

import java.util.Optional;

@Repository
public interface ChatMemberRepository extends JpaRepository<ChatMember, ChatMemberId> {
    Page<ChatMember> findAllByChat(Chat chat, Pageable pageable);

    Page<ChatMember> findAllByUser(User user, Pageable pageable);

    Optional<ChatMember> findByUserAndChat(User user, Chat chat);
}
