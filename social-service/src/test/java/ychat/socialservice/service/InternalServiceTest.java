package ychat.socialservice.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ychat.socialservice.model.chat.Chat;
import ychat.socialservice.model.chat.DirectChatMember;
import ychat.socialservice.model.chat.DirectChatMemberBuilder;
import ychat.socialservice.model.group.Group;
import ychat.socialservice.model.group.GroupBuilder;
import ychat.socialservice.model.group.GroupMember;
import ychat.socialservice.model.user.User;
import ychat.socialservice.model.user.UserBuilder;
import ychat.socialservice.repository.ChatMemberRepository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class InternalServiceTest {
    @Mock
    private UserService userService;
    @Mock
    private ChatMemberRepository chatMemberRepo;
    @InjectMocks
    private InternalService internalService;

    @Test
    void ShouldReceive_GroupAndBlocked_ReturnTrue() {
        User user = new UserBuilder().withId(new UUID(0,1)).build();
        User otherUser = new UserBuilder().withId(new UUID(0,2)).build();
        user.addBlockedUser(otherUser);
        Group group = new GroupBuilder().withInitUser(otherUser).build();
        GroupMember groupMember = group.addGroupMember(user);

        when(chatMemberRepo.findByUserIdAndChatId(eq(user.getId()), eq(group.getId())))
            .thenReturn(Optional.of(groupMember));

        assertTrue(internalService.shouldReceive(user.getId(), group.getId()));

        verify(chatMemberRepo, times(1))
            .findByUserIdAndChatId(eq(user.getId()), eq(group.getId()));
        verifyNoMoreInteractions(userService, chatMemberRepo);
    }

    @Test
    void ShouldReceive_DirectChatAndBlocked_ReturnFalse() {
        User user = new UserBuilder().withId(new UUID(0,1)).build();
        User otherUser = new UserBuilder().withId(new UUID(0,2)).build();
        user.addBlockedUser(otherUser);
        DirectChatMember directChatMember =
            new DirectChatMemberBuilder().withFstuser(user).withSndUser(otherUser).build();
        Chat chat = directChatMember.getChat();

        when(chatMemberRepo.findByUserIdAndChatId(eq(user.getId()), eq(chat.getId())))
            .thenReturn(Optional.of(directChatMember));
        when(userService.isBlockedUser(eq(user.getId()), eq(otherUser.getId())))
            .thenReturn(LocalDateTime.now());

        assertFalse(internalService.shouldReceive(user.getId(), chat.getId()));

        verify(chatMemberRepo, times(1))
                .findByUserIdAndChatId(eq(user.getId()), eq(chat.getId()));
        verify(userService, times(1))
                .isBlockedUser(eq(user.getId()), eq(otherUser.getId()));
        verifyNoMoreInteractions(userService, chatMemberRepo);
    }

    @Test
    void ShouldReceive_NotPartOfChat_ReturnFalse() {
        User user = new UserBuilder().withId(new UUID(0,1)).build();
        Group group = new GroupBuilder().build();

        when(chatMemberRepo.findByUserIdAndChatId(eq(user.getId()), eq(group.getId())))
            .thenReturn(Optional.empty());

        assertFalse(internalService.shouldReceive(user.getId(), group.getId()));

        verify(chatMemberRepo, times(1))
            .findByUserIdAndChatId(eq(user.getId()), eq(group.getId()));
        verifyNoMoreInteractions(userService, chatMemberRepo);
    }
}