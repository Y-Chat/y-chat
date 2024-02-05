package ychat.socialservice.controller;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import ychat.socialservice.model.chat.*;
import ychat.socialservice.model.group.Group;
import ychat.socialservice.model.group.GroupBuilder;
import ychat.socialservice.model.user.User;
import ychat.socialservice.model.user.UserBuilder;
import ychat.socialservice.repository.ChatMemberRepository;
import ychat.socialservice.repository.ChatRepository;
import ychat.socialservice.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.Matchers.in;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@AutoConfigureMockMvc
@Transactional
class ChatControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepo;
    @Autowired
    private ChatRepository chatRepo;
    @Autowired
    private ChatMemberRepository chatMemberRepo;

    // State start ---------------------------------------------------------------------------------
    private final String username1 = "username1";
    private final String username2 = "username2";
    private final String username3 = "username3";
    private User user1;
    private User user2;
    private User user3;
    private Group group;
    private DirectChat directChat;

    @BeforeEach
    void setUp() {
        user1 = new UserBuilder().withId(UUID.nameUUIDFromBytes(username1.getBytes())).build();
        user2 = new UserBuilder().withId(UUID.nameUUIDFromBytes(username2.getBytes())).build();
        user3 = new UserBuilder().withId(UUID.nameUUIDFromBytes(username3.getBytes())).build();
        userRepo.save(user1);
        userRepo.save(user2);
        userRepo.save(user3);

        group = new GroupBuilder().withInitUser(user1).build();
        group.addGroupMember(user2);
        directChat = new DirectChatBuilder().withFstUser(user1).withSndUser(user3).build();
        chatRepo.save(group);
        chatRepo.save(directChat);
    }
    // State end -----------------------------------------------------------------------------------

    // Chat start ----------------------------------------------------------------------------------
    @Test
    @WithMockUser(username = username1)
    void GetChat_Ok_ReturnChat() throws Exception {
        mockMvc.perform(get("/chats/{chatId}", directChat.getId())
            .param("userId", user1.getId().toString()))
            .andExpect(status().isOk())
            .andExpect(content().contentType("application/json"))
            .andExpect(jsonPath("$.chatId").value(directChat.getId().toString()))
            .andExpect(jsonPath("$.chatType").value("DIRECT_CHAT"))
            .andExpect(jsonPath("$.groupProfileDTO").value(nullValue()))
            .andExpect(jsonPath("$.userId").value(user3.getId().toString()));
    }

    @Test
    @WithMockUser(username = username1)
    void GetAllChats_Ok_ReturnPage() throws Exception {
        List<String> resultIds = new ArrayList<>();
        resultIds.add(group.getId().toString());
        resultIds.add(directChat.getId().toString());

        mockMvc.perform(get("/chats")
                .param("userId", user1.getId().toString())
                .param("page", "0")
                .param("size", "10")
            )
            .andExpect(status().isOk())
            .andExpect(content().contentType("application/json"))
            .andExpect(jsonPath("$.number").value(0))
            .andExpect(jsonPath("$.size").value(10))
            .andExpect(jsonPath("$.totalElements").value(2))
            .andExpect(jsonPath("$.content").isArray())
            .andExpect(jsonPath("$.content").isNotEmpty())
            .andExpect(jsonPath("$.content[0].chatId").value(in(resultIds)))
            .andExpect(jsonPath("$.content[1].chatId").value(in(resultIds)));
    }

    @Test
    @WithMockUser(username = username1)
    void CreateDirectChat_Created_ReturnChat() throws Exception {
        MvcResult mvcResult = mockMvc.perform(post("/chats/directChats")
            .param("userId", user1.getId().toString())
            .param("otherUserId", user2.getId().toString()))
            .andExpect(status().isCreated())
            .andExpect(content().contentType("application/json"))
            .andExpect(jsonPath("$.chatType").value("DIRECT_CHAT"))
            .andExpect(jsonPath("$.groupProfileDTO").value(nullValue()))
            .andExpect(jsonPath("$.userId").value(user2.getId().toString()))
            .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        UUID chatId = UUID.fromString(JsonPath.read(response, "$.chatId"));
        assertTrue(chatRepo.existsById(chatId));
        assertTrue(chatMemberRepo.existsById(new ChatMemberId(user1.getId(), chatId)));
        assertTrue(chatMemberRepo.existsById(new ChatMemberId(user2.getId(), chatId)));
    }
    // Chat end ------------------------------------------------------------------------------------

    // Members start -------------------------------------------------------------------------------
    @Test
    @WithMockUser(username = username1)
    void GetChatMembers_Ok_ReturnPage() throws Exception {
        List<String> resultIds = new ArrayList<>();
        resultIds.add(user1.getId().toString());
        resultIds.add(user3.getId().toString());

        mockMvc.perform(get("/chats/{chatId}/members", directChat.getId())
            .param("userId", user1.getId().toString())
            .param("page", "0")
            .param("size", "10"))
            .andExpect(status().isOk())
            .andExpect(content().contentType("application/json"))
            .andExpect(jsonPath("$.number").value(0))
            .andExpect(jsonPath("$.size").value(10))
            .andExpect(jsonPath("$.totalElements").value(2))
            .andExpect(jsonPath("$.content").isArray())
            .andExpect(jsonPath("$.content").isNotEmpty())
            .andExpect(jsonPath("$.content[0].userId").value(in(resultIds)))
            .andExpect(jsonPath("$.content[1].userId").value(in(resultIds)));
    }

    @Test
    @WithMockUser(username = username1)
    void GetChatStatus_Ok_ReturnStatus() throws Exception {
        mockMvc.perform(get("/chats/{chatid}/members/{userId}/status",
                directChat.getId(), user1.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType("application/json"))
            .andExpect(jsonPath("$").value("ACTIVE"));
    }

    @Test
    @WithMockUser(username = username1)
    void SetChatStatus_Ok_ReturnStatus() throws Exception {
        mockMvc.perform(patch("/chats/{chatid}/members/{userId}/status",
                directChat.getId(), user1.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content("\"DELETED\""))
            .andExpect(status().isOk())
            .andExpect(content().contentType("application/json"))
            .andExpect(jsonPath("$").value("DELETED"));

        Optional<ChatMember> chatMemberOpt =
            chatMemberRepo.findByUserIdAndChatId(user1.getId(), directChat.getId());
        assertTrue(chatMemberOpt.isPresent());
        assertEquals(chatMemberOpt.get().getChatStatus(), ChatStatus.DELETED);
    }
    // Members end ---------------------------------------------------------------------------------
}