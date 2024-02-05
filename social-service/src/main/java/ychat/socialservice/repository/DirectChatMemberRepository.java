package ychat.socialservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ychat.socialservice.model.chat.ChatMemberId;
import ychat.socialservice.model.chat.DirectChatMember;
import ychat.socialservice.model.user.User;

import java.util.UUID;

@Repository
public interface DirectChatMemberRepository extends JpaRepository<DirectChatMember, ChatMemberId> {
    @Query(
        "SELECT CASE WHEN EXISTS ( " +
            "SELECT 1 " +
            "FROM DirectChatMember m " +
            "WHERE ((m.user.id = :userId AND m.otherUserId = :otherUserId) " +
                "OR (m.user.id = :otherUserId AND m.otherUserId = :userId)) " +
            "GROUP BY m.chat " +
            "HAVING COUNT(m.chat) >= 1 " +
        ") THEN true ELSE false END"
    )
    boolean existsBetweenTwoUsers(@Param("userId") UUID userId,
                                  @Param("otherUserId") UUID otherUserId);
}