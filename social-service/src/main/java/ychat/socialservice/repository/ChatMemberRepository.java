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
import java.util.UUID;

@Repository
public interface ChatMemberRepository extends JpaRepository<ChatMember, ChatMemberId> {
    Page<ChatMember> findAllByChatId(UUID chatId, Pageable pageable);

    Page<ChatMember> findAllByUserId(UUID userId, Pageable pageable);

    Optional<ChatMember> findByUserIdAndChatId(UUID userId, UUID chatid);
}
