package ychat.socialservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ychat.socialservice.model.chat.ChatMemberId;
import ychat.socialservice.model.chat.DirectChatMember;
import ychat.socialservice.model.user.User;

@Repository
public interface DirectChatMemberRepository extends JpaRepository<DirectChatMember, ChatMemberId> {
    // TODO not sure if it really works => test
    @Query(
        "SELECT COUNT(m) > 0 " +
        "FROM DirectChatMember m " +
        "WHERE (m.user = :user OR m.otherUserId = :otherUser) " +
        "GROUP BY m.chat " +
        "HAVING COUNT(m.chat) > 1"
    )
    boolean existsBetweenTwoUsers(@Param("user") User user, @Param("otherUser") User otherUser);
}