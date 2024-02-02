package ychat.socialservice.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import ychat.socialservice.model.chat.DirectChat;
import ychat.socialservice.model.chat.DirectChatBuilder;
import ychat.socialservice.model.group.Group;
import ychat.socialservice.model.group.GroupBuilder;
import ychat.socialservice.model.user.User;
import ychat.socialservice.model.user.UserBuilder;
import ychat.socialservice.repository.ChatRepository;
import ychat.socialservice.repository.UserRepository;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@AutoConfigureMockMvc
@Transactional
class InternalControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepo;
    @Autowired
    private ChatRepository chatRepo;

    // State start ---------------------------------------------------------------------------------
    private User user1;
    private User user2;
    private Group group;
    private DirectChat directChat;

    @BeforeEach
    void setUp() {
        user1 = new UserBuilder().withId(new UUID(0,1)).build();
        user2 = new UserBuilder().withId(new UUID(0,2)).build();
        userRepo.save(user1);
        userRepo.save(user2);

        user1.addBlockedUser(user2);
        userRepo.save(user1);

        group = new GroupBuilder().withInitUser(user1).build();
        group.addGroupMember(user2);
        directChat = new DirectChatBuilder().withFstUser(user1).withSndUser(user2).build();
        chatRepo.save(group);
        chatRepo.save(directChat);
    }
    // State end -----------------------------------------------------------------------------------

    @Test
    void ShouldReceive_GroupAndBlocked_ReturnTrue() throws Exception {
        mockMvc.perform(get("/internal/{userId}/shouldReceive/{chatId}",
                user1.getId(), group.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType("application/json"))
            .andExpect(jsonPath("$").value(true));
    }

    @Test
    void ShouldReceive_DirectChatAndBlocked_ReturnFalse() throws Exception {
        mockMvc.perform(get("/internal/{userId}/shouldReceive/{chatId}",
                user1.getId(), directChat.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType("application/json"))
            .andExpect(jsonPath("$").value(false));
    }
}