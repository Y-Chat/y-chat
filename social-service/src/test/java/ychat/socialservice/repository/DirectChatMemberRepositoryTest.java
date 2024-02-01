package ychat.socialservice.repository;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ychat.socialservice.model.chat.*;
import ychat.socialservice.model.user.UserBuilder;
import ychat.socialservice.model.user.User;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@AutoConfigureTestDatabase(replace= AutoConfigureTestDatabase.Replace.NONE)
class DirectChatMemberRepositoryTest {
    @Autowired
    TestEntityManager entityManager;
    @Autowired
    private DirectChatMemberRepository memberRepo;

    DirectChatMember member1;
    DirectChatMember member2;

    @BeforeEach
    @Transactional
    void setUp() {
        User user1 = new UserBuilder().withId(new UUID(0,0)).build();
        User user2 = new UserBuilder().withId(new UUID(0,1)).build();
        User user3 = new UserBuilder().withId(new UUID(0,2)).build();
        entityManager.persist(user1);
        entityManager.persist(user2);
        entityManager.persist(user3);

        DirectChat directChat1 =
            new DirectChatBuilder().withFstUser(user1).withSndUser(user2).build();
        DirectChat directChat2 =
            new DirectChatBuilder().withFstUser(user1).withSndUser(user3).build();
        entityManager.persist(directChat1);
        entityManager.persist(directChat2);
        entityManager.flush();

        entityManager.detach(directChat1);
        member1 = entityManager.find(
            DirectChatMember.class,
            new ChatMemberId(user1.getId(), directChat1.getId())
        );
        member2 = entityManager.find(
            DirectChatMember.class,
            new ChatMemberId(user2.getId(), directChat1.getId())
        );
    }

    @Test
    void ExistsBetweenTwoUsers_BothEntriesExist_True() {
        assertTrue(memberRepo.existsBetweenTwoUsers(
            member1.getUser().getId(),
            member2.getUser().getId())
        );
    }

    @Test
    void ExistsBetweenTwoUsers_OneEntryExist_True() {
        entityManager.remove(member1);
        entityManager.flush();
        assertTrue(memberRepo.existsBetweenTwoUsers(
            member1.getUser().getId(),
            member2.getUser().getId())
        );
    }

    @Test
    void ExistsBetweenTwoUsers_EntriesDeleted_False() {
        entityManager.remove(member1);
        entityManager.remove(member2);
        entityManager.flush();
        assertFalse(memberRepo.existsBetweenTwoUsers(
            member1.getUser().getId(),
            member2.getUser().getId())
        );
    }
}