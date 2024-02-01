package ychat.socialservice.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ychat.socialservice.model.chat.*;
import ychat.socialservice.model.group.Group;
import ychat.socialservice.model.group.GroupMember;
import ychat.socialservice.model.group.GroupMemberBuilder;
import ychat.socialservice.model.user.User;
import ychat.socialservice.model.user.UserBuilder;
import ychat.socialservice.repository.ChatMemberRepository;
import ychat.socialservice.repository.DirectChatMemberRepository;
import ychat.socialservice.repository.DirectChatRepository;
import ychat.socialservice.service.dto.ChatDTO;
import ychat.socialservice.util.IllegalUserInputException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatServiceTest {
    @Mock
    private UserService userService;
    @Mock
    private ChatMemberRepository chatMemberRepo;
    @Mock
    private DirectChatRepository directChatRepo;
    @Mock
    private DirectChatMemberRepository directChatMemberRepo;

    @InjectMocks
    private ChatService chatService;

    @Test
    void FindChatMemberByIdsOrThrow_NotExists_Throws() {
        UUID userId = new UUID(0,0);
        UUID chatId = new UUID(0,1);

        when(chatMemberRepo.findByUserIdAndChatId(eq(userId), eq(chatId)))
            .thenReturn(Optional.empty());

        assertThrows(
            IllegalUserInputException.class,
            () -> chatService.findChatMemberByIdsOrThrow(userId, chatId)
        );

        verify(chatMemberRepo, times(1))
            .findByUserIdAndChatId(eq(userId), eq(chatId));
        verifyNoMoreInteractions(userService, chatMemberRepo, directChatRepo, directChatMemberRepo);
    }

    @Test
    void FindChatMemberByIdsOrThrow_Exists_ReturnsChatMember() {
        User fstUser = new UserBuilder().withId(new UUID(0,0)).build();
        User sndUser = new UserBuilder().withId(new UUID(0,1)).build();
        DirectChatMember directChatMember =
            new DirectChatMemberBuilder().withFstuser(fstUser).withSndUser(sndUser).build();
        Chat chat = directChatMember.getChat();

        when(chatMemberRepo.findByUserIdAndChatId(eq(fstUser.getId()), eq(chat.getId())))
            .thenReturn(Optional.of(directChatMember));

        ChatMember retChatMember =
            chatService.findChatMemberByIdsOrThrow(fstUser.getId(), chat.getId());

        assertNotNull(retChatMember);
        assertEquals(retChatMember, directChatMember);

        verify(chatMemberRepo, times(1))
            .findByUserIdAndChatId(eq(fstUser.getId()), eq(chat.getId()));
        verifyNoMoreInteractions(userService, chatMemberRepo, directChatRepo, directChatMemberRepo);
    }

    // Chats start ---------------------------------------------------------------------------------
    @Test
    void GetChat_IsDirectChatMember_ReturnChat() {
        User fstUser = new UserBuilder().withId(new UUID(0,0)).build();
        User sndUser = new UserBuilder().withId(new UUID(0,1)).build();
        DirectChatMember directChatMember =
            new DirectChatMemberBuilder().withFstuser(fstUser).withSndUser(sndUser).build();
        Chat chat = directChatMember.getChat();

        when(chatMemberRepo.findByUserIdAndChatId(eq(sndUser.getId()), eq(chat.getId())))
            .thenReturn(Optional.of(directChatMember));

        ChatDTO chatDTO = chatService.getChat(chat.getId(), sndUser.getId());

        assertNotNull(chatDTO);
        assertEquals(chatDTO.chatId(), chat.getId());

        verify(chatMemberRepo, times(1))
            .findByUserIdAndChatId(eq(sndUser.getId()), eq(chat.getId()));
        verifyNoMoreInteractions(userService, chatMemberRepo, directChatRepo, directChatMemberRepo);
    }

    @Test
    void GetChats_PageSizeTooLarge_Throws() {
        UUID id = new UUID(0,0);
        int tooLargePageSize = ChatService.MAX_CHAT_PAGE_SIZE + 1;
        Pageable pageable = PageRequest.of(0, tooLargePageSize);

        assertThrows(
            IllegalUserInputException.class,
            () -> chatService.getAllChats(id, pageable)
        );

        verifyNoInteractions(userService, chatMemberRepo, directChatRepo, directChatMemberRepo);
    }

    @Test
    void GetChats_Unpaged_Throws() {
        UUID id = UUID.randomUUID();
        Pageable pageable = Pageable.unpaged();

        assertThrows(
            IllegalUserInputException.class,
            () -> chatService.getAllChats(id, pageable)
        );

        verifyNoInteractions(userService, chatMemberRepo, directChatRepo, directChatMemberRepo);
    }

    @Test
    void GetAllChats_ReturnsChats() {
        User fstUser = new UserBuilder().withId(new UUID(0,0)).build();
        User sndUser = new UserBuilder().withId(new UUID(0,1)).build();
        GroupMember groupMember = new GroupMemberBuilder().withUser(fstUser).build();
        DirectChatMember directChatMember =
            new DirectChatMemberBuilder().withFstuser(fstUser).withSndUser(sndUser).build();
        List<ChatMember> chatMembers = new ArrayList<>();
        chatMembers.add(groupMember);
        chatMembers.add(directChatMember);
        Pageable pageable = PageRequest.of(0, ChatService.MAX_CHAT_PAGE_SIZE);

        when(userService.findUserByIdOrThrow(eq(fstUser.getId())))
            .thenReturn(fstUser);
        when(chatMemberRepo.findAllByUserId(eq(fstUser.getId()), eq(pageable)))
            .thenReturn(new PageImpl<>(chatMembers));

        Page<ChatDTO> chatPage = chatService.getAllChats(fstUser.getId(), pageable);

        assertNotNull(chatPage);
        List<ChatDTO> chatDTOs = chatPage.getContent();
        assertEquals(2, chatDTOs.size());
        for(ChatDTO chatDTO : chatDTOs){
            assertTrue(chatDTO.chatId().equals(groupMember.getGroup().getId())
                || chatDTO.chatId().equals(directChatMember.getChat().getId()));
        }

        verify(userService, times(1))
            .findUserByIdOrThrow(eq(fstUser.getId()));
        verify(chatMemberRepo, times(1))
            .findAllByUserId(eq(fstUser.getId()), eq(pageable));
        verifyNoMoreInteractions(userService, chatMemberRepo, directChatRepo, directChatMemberRepo);
    }

    @Test
    void CreateDirectChat_SameUser_Throws() {
        UUID userId = new UUID(0,0);

        assertThrows(
            IllegalUserInputException.class,
            () -> chatService.createDirectChat(userId, userId)
        );

        verifyNoInteractions(userService, chatMemberRepo, directChatRepo, directChatMemberRepo);
    }

    @Test
    void CreateDirectChat_ExistsAlready_Throws() {
        UUID fstUserId = new UUID(0,0);
        UUID sndUserId = new UUID(0,1);

        when(directChatMemberRepo.existsBetweenTwoUsers(eq(fstUserId), eq(sndUserId)))
            .thenReturn(true);

        assertThrows(
            IllegalUserInputException.class,
            () -> chatService.createDirectChat(fstUserId, sndUserId)
        );

        verify(directChatMemberRepo, times(1))
            .existsBetweenTwoUsers(eq(fstUserId), eq(sndUserId));
        verifyNoMoreInteractions(userService, chatMemberRepo, directChatRepo, directChatMemberRepo);
    }

    @Test
    void CreateDirectChat_NotExistsAlready_ReturnChat() {
        User fstUser = new UserBuilder().withId(new UUID(0,0)).build();
        User sndUser = new UserBuilder().withId(new UUID(0,1)).build();

        when(userService.findUserByIdOrThrow(eq(fstUser.getId())))
            .thenReturn(fstUser);
        when(userService.findUserByIdOrThrow(eq(sndUser.getId())))
            .thenReturn(sndUser);
        when(directChatMemberRepo.existsBetweenTwoUsers(eq(fstUser.getId()), eq(sndUser.getId())))
            .thenReturn(false);

        ChatDTO chatDTO = chatService.createDirectChat(fstUser.getId(), sndUser.getId());

        assertNotNull(chatDTO);
        assertEquals(chatDTO.userId(), sndUser.getId());
        assertEquals(chatDTO.userProfileDTO().firstName(), sndUser.getUserProfile().getFirstName());

        verify(userService, times(1))
            .findUserByIdOrThrow(eq(fstUser.getId()));
        verify(userService, times(1))
            .findUserByIdOrThrow(eq(fstUser.getId()));
        verify(directChatMemberRepo, times(1))
            .existsBetweenTwoUsers(eq(fstUser.getId()), eq(sndUser.getId()));
        verify(directChatRepo, times(1))
            .save(any(DirectChat.class));
        verifyNoMoreInteractions(userService, chatMemberRepo, directChatRepo, directChatMemberRepo);
    }
    // Chats end -----------------------------------------------------------------------------------

    // Members start -------------------------------------------------------------------------------
    @Test
    void GetChatMembers_Unpaged_Throws() {
        UUID chatId = new UUID(0,0);
        UUID userId = new UUID(0,1);
        Pageable pageable = Pageable.unpaged();

        assertThrows(
            IllegalUserInputException.class,
            () -> chatService.getChatMembers(chatId, userId, pageable)
        );

        verifyNoInteractions(userService, chatMemberRepo, directChatRepo, directChatMemberRepo);
    }

    @Test
    void GetChatMembers_PageSizeTooLarge_Throws() {
        UUID chatId = new UUID(0,0);
        UUID userId = new UUID(0,1);
        int tooLargePageSize = ChatService.MAX_CHAT_MEMBER_PAGE_SIZE + 1;
        Pageable pageable = PageRequest.of(0, tooLargePageSize);

        assertThrows(
            IllegalUserInputException.class,
            () -> chatService.getChatMembers(chatId, userId, pageable)
        );

        verifyNoInteractions(userService, chatMemberRepo, directChatRepo, directChatMemberRepo);
    }

    @Test
    void GetChatMembers_NotDirectChatMember_Throws() {
        UUID userId = new UUID(0,0);
        UUID chatId = new UUID(0,1);
        Pageable pageable = PageRequest.of(0, ChatService.MAX_CHAT_MEMBER_PAGE_SIZE);

        when(chatMemberRepo.findByUserIdAndChatId(eq(userId), eq(chatId)))
            .thenReturn(Optional.empty());

        assertThrows(
            IllegalUserInputException.class,
            () -> chatService.getChatMembers(chatId, userId, pageable)
        );

        verify(chatMemberRepo, times(1))
            .findByUserIdAndChatId(eq(userId), eq(chatId));
        verifyNoMoreInteractions(userService, chatMemberRepo, directChatRepo, directChatMemberRepo);
    }

    @Test
    void GetChatMembers_DirectChatMember_ReturnChatMembers() {
        User fstUser = new UserBuilder().withId(new UUID(0,0)).build();
        User sndUser = new UserBuilder().withId(new UUID(0,1)).build();
        DirectChat directChat =
            new DirectChatBuilder().withFstUser(fstUser).withSndUser(sndUser).build();
        ChatMember fstMember = directChat.getMember(fstUser);
        ChatMember sndMember = directChat.getMember(sndUser);
        List<ChatMember> chatMembers = new ArrayList<>();
        chatMembers.add(fstMember);
        chatMembers.add(sndMember);
        Pageable pageable = PageRequest.of(0, ChatService.MAX_CHAT_MEMBER_PAGE_SIZE);

        when(chatMemberRepo.findAllByChatId(eq(directChat.getId()), eq(pageable)))
            .thenReturn(new PageImpl<>(chatMembers));

        Page<ChatMember> chatMemberPage =
            chatMemberRepo.findAllByChatId(directChat.getId(), pageable);

        assertNotNull(chatMemberPage);
        List<ChatMember> retChatMembers = chatMemberPage.getContent();
        assertEquals(2, retChatMembers.size());
        for(ChatMember chatMember : retChatMembers)
            assertTrue(chatMember.equals(fstMember) || chatMember.equals(sndMember));

        verify(chatMemberRepo, times(1))
            .findAllByChatId(eq(directChat.getId()), eq(pageable));
        verifyNoMoreInteractions(userService, chatMemberRepo, directChatRepo, directChatMemberRepo);
    }

    @Test
    void GetChatStatus_IsNotGroupMember_ReturnsNotAMember() {
        GroupMember groupMember = new GroupMemberBuilder().build();
        User user = groupMember.getUser();
        Group group = groupMember.getGroup();

        when(chatMemberRepo.findByUserIdAndChatId(eq(user.getId()), eq(group.getId())))
            .thenReturn(Optional.empty());

        ChatStatus chatStatus = chatService.getChatStatus(group.getId(), user.getId());

        assertNotNull(chatStatus);
        assertEquals(chatStatus, ChatStatus.NOT_A_MEMBER);

        verify(chatMemberRepo, times(1))
            .findByUserIdAndChatId(eq(user.getId()), eq(group.getId()));
        verifyNoMoreInteractions(userService, chatMemberRepo, directChatRepo, directChatMemberRepo);
    }

    @Test
    void GetChatStatus_IsGroupMember_ReturnsStatus() {
        GroupMember groupMember = new GroupMemberBuilder().build();
        User user = groupMember.getUser();
        Group group = groupMember.getGroup();

        when(chatMemberRepo.findByUserIdAndChatId(eq(user.getId()), eq(group.getId())))
            .thenReturn(Optional.of(groupMember));

        ChatStatus chatStatus = chatService.getChatStatus(group.getId(), user.getId());

        assertNotNull(chatStatus);
        assertEquals(chatStatus, groupMember.getChatStatus());

        verify(chatMemberRepo, times(1))
            .findByUserIdAndChatId(eq(user.getId()), eq(group.getId()));
        verifyNoMoreInteractions(userService, chatMemberRepo, directChatRepo, directChatMemberRepo);
    }

    @Test
    void SetChatStatus_ToNotAMember_Throws() {
        UUID chatId = new UUID(0,0);
        UUID userId = new UUID(0,1);

        assertThrows(
            IllegalUserInputException.class,
            () -> chatService.setChatStatus(chatId, userId, ChatStatus.NOT_A_MEMBER)
        );

        verifyNoInteractions(userService, chatMemberRepo, directChatRepo, directChatMemberRepo);
    }

    @Test
    void SetChatStatus_IsNotDirectChatMember_Throws() {
        DirectChatMember directChatMember = new DirectChatMemberBuilder().build();
        DirectChat directChat = (DirectChat) directChatMember.getChat();
        User user = directChatMember.getUser();

        when(chatMemberRepo.findByUserIdAndChatId(eq(user.getId()), eq(directChat.getId())))
            .thenReturn(Optional.empty());

        assertThrows(
            IllegalUserInputException.class,
            () -> chatService.setChatStatus(
                directChat.getId(), user.getId(),ChatStatus.ACTIVE
            )
        );

        verify(chatMemberRepo, times(1))
            .findByUserIdAndChatId(eq(user.getId()), eq(directChat.getId()));
        verifyNoMoreInteractions(userService, chatMemberRepo, directChatRepo, directChatMemberRepo);
    }

    @Test
    void SetChatStatus_ToDeletedGroup_Throws() {
        GroupMember groupMember = new GroupMemberBuilder().build();
        User user = groupMember.getUser();
        Group group = groupMember.getGroup();

        when(chatMemberRepo.findByUserIdAndChatId(eq(user.getId()), eq(group.getId())))
            .thenReturn(Optional.of(groupMember));

        assertThrows(
            IllegalUserInputException.class,
            () -> chatService.setChatStatus(group.getId(), user.getId(), ChatStatus.DELETED)
        );

        verify(chatMemberRepo, times(1))
            .findByUserIdAndChatId(eq(user.getId()), eq(group.getId()));
        verifyNoMoreInteractions(userService, chatMemberRepo, directChatRepo, directChatMemberRepo);
    }

    @Test
    void SetChatStatus_ToDeletedDirectedChat_Delete_ReturnsDeleted() {
        DirectChatMember directChatMember = new DirectChatMemberBuilder().build();
        DirectChat directChat = (DirectChat) directChatMember.getChat();
        User user = directChatMember.getUser();
        User otherUser = new UserBuilder().withId(directChatMember.getOtherUserId()).build();
        directChat.removeMember(otherUser);

        when(chatMemberRepo.findByUserIdAndChatId(eq(user.getId()), eq(directChat.getId())))
            .thenReturn(Optional.of(directChatMember));

        ChatStatus chatStatus = chatService.setChatStatus(
            directChat.getId(), user.getId(), ChatStatus.DELETED
        );

        assertNotNull(chatStatus);
        assertEquals(chatStatus, ChatStatus.DELETED);

        verify(chatMemberRepo, times(1))
            .findByUserIdAndChatId(eq(user.getId()), eq(directChat.getId()));
        verify(directChatRepo, times(1))
            .delete(eq(directChat));
        verifyNoMoreInteractions(userService, chatMemberRepo, directChatRepo, directChatMemberRepo);
    }

    @Test
    void SetChatStatus_ToDeletedDirectedChat_NotDelete_ReturnsDeleted() {
        DirectChatMember directChatMember = new DirectChatMemberBuilder().build();
        DirectChat directChat = (DirectChat) directChatMember.getChat();
        User user = directChatMember.getUser();

        when(chatMemberRepo.findByUserIdAndChatId(eq(user.getId()), eq(directChat.getId())))
            .thenReturn(Optional.of(directChatMember));

        ChatStatus chatStatus = chatService.setChatStatus(
            directChat.getId(), user.getId(), ChatStatus.DELETED
        );

        assertNotNull(chatStatus);
        assertEquals(chatStatus, ChatStatus.DELETED);

        verify(chatMemberRepo, times(1))
            .findByUserIdAndChatId(eq(user.getId()), eq(directChat.getId()));
        verifyNoMoreInteractions(userService, chatMemberRepo, directChatRepo, directChatMemberRepo);
    }
    // Members end -------------------------------------------------------------------------------
}