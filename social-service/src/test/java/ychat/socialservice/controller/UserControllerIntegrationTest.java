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
import ychat.socialservice.model.chat.ChatMemberId;
import ychat.socialservice.model.chat.DirectChat;
import ychat.socialservice.model.chat.DirectChatBuilder;
import ychat.socialservice.model.group.GroupBuilder;
import ychat.socialservice.model.group.Group;
import ychat.socialservice.model.user.BlockedUser;
import ychat.socialservice.model.user.BlockedUserId;
import ychat.socialservice.model.user.User;
import ychat.socialservice.model.user.UserBuilder;
import ychat.socialservice.repository.BlockedUserRepository;
import ychat.socialservice.repository.ChatMemberRepository;
import ychat.socialservice.repository.ChatRepository;
import ychat.socialservice.repository.UserRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@AutoConfigureMockMvc
@Transactional
class UserControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepo;
    @Autowired
    private BlockedUserRepository blockedUserRepo;
    @Autowired
    private ChatRepository chatRepo;
    @Autowired
    private ChatMemberRepository chatMemberRepo;

    // State start ---------------------------------------------------------------------------------
    private final String username1 = "username1";
    private final String username2 = "username2";
    private final String username3 = "username3";
    private final String username4 = "username4";
    private final String userId4 = UUID.nameUUIDFromBytes(username4.getBytes()).toString();
    private User user1;
    private User user2;
    private User user3;
    private BlockedUser user1BlockedUser2;
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

        user1BlockedUser2 = user1.addBlockedUser(user2);
        userRepo.save(user1);

        group = new GroupBuilder().withInitUser(user1).build();
        directChat = new DirectChatBuilder().withFstUser(user1).withSndUser(user3).build();
        chatRepo.save(group);
        chatRepo.save(directChat);
    }
    // State end -----------------------------------------------------------------------------------

    // Security start ------------------------------------------------------------------------------
    @Test
    void GetUser_NoToken_Unauthorized() throws Exception {
        mockMvc.perform(get("/users/{userId}", user1.getId()))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = username2)
    void GetUser_WrongUser_Forbidden() throws Exception {
        mockMvc.perform(get("/users/{userId}", user1.getId()))
            .andExpect(status().isForbidden());
    }
    // Security end --------------------------------------------------------------------------------

    // User start ----------------------------------------------------------------------------------
    @Test
    @WithMockUser(username = username4)
    void CreateUser_Created_ReturnUser() throws Exception {
        mockMvc.perform(post("/users")
            .param("userId", userId4)
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"firstName\": \"hi\", \"lastName\": \"hihi\"}"))
            .andExpect(status().isCreated())
            .andExpect(content().contentType("application/json"))
            .andExpect(jsonPath("$.id").value(userId4))
            .andExpect(jsonPath("$.userProfileDTO.firstName").value("hi"))
            .andExpect(jsonPath("$.userProfileDTO.lastName").value("hihi"));
    }

    @Test
    @WithMockUser(username = username1)
    void GetUser_OK_ReturnsUser() throws Exception {
        mockMvc.perform(get("/users/{userId}", user1.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType("application/json"))
            .andExpect(jsonPath("$.id").value(user1.getId().toString()))
            .andExpect(jsonPath("$.userProfileDTO.firstName")
                .value(user1.getUserProfile().getFirstName()));
    }

    @Test
    @WithMockUser(username = username1)
    void DeleteUser_NoContent() throws Exception {
        mockMvc.perform(delete("/users/{userId}", user1.getId()))
            .andExpect(status().isNoContent());

        assertFalse(userRepo.existsById(user1.getId()));
        assertFalse(chatRepo.existsById(group.getId()));
        assertTrue(chatMemberRepo.existsById(new ChatMemberId(user3.getId(), directChat.getId())));
    }
    // User end ------------------------------------------------------------------------------------

    // Profile and settings start ------------------------------------------------------------------
    @Test
    @WithMockUser(username = username1)
    void GetUserProfile_Ok_ReturnUserProfile() throws Exception {
        mockMvc.perform(get("/users/{userId}/profile", user1.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType("application/json"))
            .andExpect(jsonPath("$.firstName")
                .value(user1.getUserProfile().getFirstName()));
    }

    @Test
    @WithMockUser(username = username1)
    void UpdateUserProfile_Ok_ReturnUserProfile() throws Exception {
        String newFirstName = "testtest";
        assertNotEquals(newFirstName, user1.getUserProfile().getFirstName());

        mockMvc.perform(patch("/users/{userId}/profile", user1.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"firstName\": \"" + newFirstName + "\"}"))
            .andExpect(status().isOk())
            .andExpect(content().contentType("application/json"))
            .andExpect(jsonPath("$.firstName")
                .value(newFirstName))
            .andExpect(jsonPath("$.lastName")
                .value(user1.getUserProfile().getLastName()));
    }

    @Test
    @WithMockUser(username = username1)
    void GetUserSettings_Ok_ReturnUserSettings() throws Exception {
        mockMvc.perform(get("/users/{userId}/settings", user1.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType("application/json"))
            .andExpect(jsonPath("$.lastSeen")
                .value(user1.getUserSettings().isLastSeen()));
    }

    @Test
    @WithMockUser(username = username1)
    void UpdateUserSettings_Ok_ReturnUserSettings() throws Exception {
        boolean newLastSeen = !user1.getUserSettings().isLastSeen();
        mockMvc.perform(patch("/users/{userId}/settings", user1.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"lastSeen\": \"" + newLastSeen + "\"}"))
            .andExpect(status().isOk())
            .andExpect(content().contentType("application/json"))
            .andExpect(jsonPath("$.lastSeen")
                .value(newLastSeen))
            .andExpect(jsonPath("$.readReceipts")
                .value(user1.getUserSettings().isReadReceipts()));
    }
    // Profile and settings end --------------------------------------------------------------------

    // Blocking start ------------------------------------------------------------------------------
    @Test
    @WithMockUser(username = username1)
    void GetBlockedUsers_Ok_Page() throws Exception{
        assertTrue(blockedUserRepo.existsById(new BlockedUserId(user1.getId(), user2.getId())));
        assertFalse(blockedUserRepo.existsById(new BlockedUserId(user1.getId(), user3.getId())));

        mockMvc.perform(get("/users/{userId}/blockedUsers", user1.getId())
                .param("page", "0")
                .param("size", "10")
            )
            .andExpect(status().isOk())
            .andExpect(content().contentType("application/json"))
            .andExpect(jsonPath("$.number").value(0))
            .andExpect(jsonPath("$.size").value(10))
            .andExpect(jsonPath("$.totalElements").value(1))
            .andExpect(jsonPath("$.content").isArray())
            .andExpect(jsonPath("$.content").isNotEmpty())
            .andExpect(jsonPath("$.content[0].id").value(user2.getId().toString()));

        assertTrue(blockedUserRepo.existsById(new BlockedUserId(user1.getId(), user2.getId())));
        assertFalse(blockedUserRepo.existsById(new BlockedUserId(user1.getId(), user3.getId())));
    }

    @Test
    @WithMockUser(username = username1)
    void IsBlockedUser_NotBlocked_Ok_EmptyResponse() throws Exception {
        assertFalse(blockedUserRepo.existsById(new BlockedUserId(user1.getId(), user3.getId())));

        mockMvc.perform(get("/users/{userId}/blockedUsers/{isBlockedId}",
                user1.getId(), user3.getId()))
            .andExpect(status().isOk())
            .andExpect(content().string(""));

        assertFalse(blockedUserRepo.existsById(new BlockedUserId(user1.getId(), user3.getId())));
    }

    @Test
    @WithMockUser(username = username1)
    void IsBlockedUser_Blocked_Ok_Timestamp() throws Exception{
        assertTrue(blockedUserRepo.existsById(new BlockedUserId(user1.getId(), user2.getId())));

        mockMvc.perform(get("/users/{userId}/blockedUsers/{isBlockedId}",
                user1.getId(), user2.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType("application/json"))
            .andExpect(jsonPath("$")
                .value(user1BlockedUser2.getCreated().format(DateTimeFormatter.ISO_DATE_TIME)));

        assertTrue(blockedUserRepo.existsById(new BlockedUserId(user1.getId(), user2.getId())));
    }

    @Test
    @WithMockUser(username = username1)
    void AddBlockedUser_NotBlocked_Ok_BlockMember() throws Exception {
        assertFalse(blockedUserRepo.existsById(new BlockedUserId(user1.getId(), user3.getId())));

        LocalDateTime before = LocalDateTime.now();

        MvcResult mvcResult = mockMvc.perform(
                post("/users/{userId}/blockedUsers", user1.getId())
                .param("blockUserId", user3.getId().toString())
            )
            .andExpect(status().isOk())
            .andExpect(content().contentType("application/json"))
            .andExpect(jsonPath("$.id").value(user3.getId().toString()))
            .andExpect(jsonPath("$.userProfileDTO.firstName")
                .value(user3.getUserProfile().getFirstName()))
            .andReturn();
        String response = mvcResult.getResponse().getContentAsString();
        LocalDateTime blockedAt = LocalDateTime.parse(
            JsonPath.read(response, "$.blockedAt"),
            DateTimeFormatter.ISO_DATE_TIME
        );

        assertTrue(before.isBefore(blockedAt));
        assertTrue(blockedUserRepo.existsById(new BlockedUserId(user1.getId(), user3.getId())));
    }

    @Test
    @WithMockUser(username = username1)
    void RemoveBlockedUser_Blocked_NoContent() throws Exception {
        assertTrue(blockedUserRepo.existsById(new BlockedUserId(user1.getId(), user2.getId())));

        mockMvc.perform(delete("/users/{userId}/blockedUsers", user1.getId())
            .param("unblockUserId", user2.getId().toString()))
            .andExpect(status().isNoContent());

        assertFalse(blockedUserRepo.existsById(new BlockedUserId(user1.getId(), user2.getId())));
    }
    // Blocking end --------------------------------------------------------------------------------
}