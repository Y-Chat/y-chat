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
import ychat.socialservice.model.group.Group;
import ychat.socialservice.model.group.GroupBuilder;
import ychat.socialservice.model.group.GroupMember;
import ychat.socialservice.model.group.GroupRole;
import ychat.socialservice.model.user.User;
import ychat.socialservice.model.user.UserBuilder;
import ychat.socialservice.repository.GroupMemberRepository;
import ychat.socialservice.repository.GroupRepository;
import ychat.socialservice.repository.UserRepository;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@AutoConfigureMockMvc
@Transactional
class GroupControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepo;
    @Autowired
    private GroupRepository groupRepo;
    @Autowired
    private GroupMemberRepository groupMemberRepo;

    // State start ---------------------------------------------------------------------------------
    private final String username1 = "username1";
    private final String username2 = "username2";
    private final String username3 = "username3";
    private User user1;
    private User user2;
    private User user3;
    private Group group;

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
        groupRepo.save(group);
    }
    // State end -----------------------------------------------------------------------------------

    // Group start ---------------------------------------------------------------------------------
    @Test
    @WithMockUser(username = username1)
    void GetGroup_Ok_ReturnGroup() throws Exception {
        mockMvc.perform(get("/groups/{groupId}", group.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType("application/json"))
            .andExpect(jsonPath("$.id").value(group.getId().toString()))
            .andExpect(jsonPath("$.groupProfileDTO.groupName")
                .value(group.getGroupProfile().getGroupName()));
    }

    @Test
    @WithMockUser(username = username1)
    void CreateGroup_Created_ReturnGroup() throws Exception {
        MvcResult mvcResult = mockMvc.perform(post("/groups")
            .param("userId", user1.getId().toString())
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"groupName\": \"groopgroop\"}"))
            .andExpect(status().isCreated())
            .andExpect(content().contentType("application/json"))
            .andExpect(jsonPath("$.groupProfileDTO.groupName")
                .value("groopgroop"))
            .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        UUID groupId = UUID.fromString(JsonPath.read(response, "$.id"));
        assertTrue(groupRepo.existsById(groupId));
        assertTrue(groupMemberRepo.existsById(new ChatMemberId(user1.getId(), groupId)));
    }
    // Group end -----------------------------------------------------------------------------------

    // Profile start -------------------------------------------------------------------------------
    @Test
    @WithMockUser(username = username1)
    void GetGroupProfile_Ok_ReturnGroupProfile() throws Exception {
        mockMvc.perform(get("/groups/{groupId}/profile", group.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType("application/json"))
            .andExpect(jsonPath("$.groupName")
                .value(group.getGroupProfile().getGroupName()));
    }

    @Test
    @WithMockUser(username = username1)
    void UpdateGroupProfile_Ok_ReturnGroupProfile() throws Exception {
        String newProfileDescription = "hihihi";
        String oldGroupName = group.getGroupProfile().getGroupName();

        mockMvc.perform(patch("/groups/{groupId}/profile", group.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"profileDescription\": \"" + newProfileDescription + "\"}"))
            .andExpect(status().isOk())
            .andExpect(content().contentType("application/json"))
            .andExpect(jsonPath("$.groupName").value(oldGroupName))
            .andExpect(jsonPath("$.profileDescription").value(newProfileDescription));

        Optional<Group> retGroupOpt = groupRepo.findById(group.getId());
        assertTrue(retGroupOpt.isPresent());
        Group retGroup = retGroupOpt.get();
        assertEquals(retGroup.getGroupProfile().getGroupName(), oldGroupName);
        assertEquals(retGroup.getGroupProfile().getProfileDescription(), newProfileDescription);
    }
    // Profile end ---------------------------------------------------------------------------------

    // Members start -------------------------------------------------------------------------------
    @Test
    @WithMockUser(username = username1)
    void AddGroupMember_Ok_ReturnGroupMember() throws Exception {
        mockMvc.perform(post("/groups/{groupId}/member", group.getId())
            .param("userId", user3.getId().toString()))
            .andExpect(status().isOk())
            .andExpect(content().contentType("application/json"))
            .andExpect(jsonPath("$.userId").value(user3.getId().toString()))
            .andExpect(jsonPath("$.groupRole").value("GROUP_MEMBER"));

        assertTrue(groupMemberRepo.existsById(new ChatMemberId(user3.getId(), group.getId())));
    }

    @Test
    @WithMockUser(username = username1)
    void RemoveGroupMember_NoContent_PromotesOtherUser() throws Exception {
        mockMvc.perform(delete("/groups/{groupId}/members", group.getId())
            .param("userId", user1.getId().toString()))
            .andExpect(status().isNoContent());

        assertFalse(groupMemberRepo.existsById(new ChatMemberId(user1.getId(), group.getId())));
        Optional<GroupMember> groupMemberOpt =
            groupMemberRepo.findByUserIdAndChatId(user2.getId(), group.getId());
        assertTrue(groupMemberOpt.isPresent());
        assertEquals(groupMemberOpt.get().getGroupRole(), GroupRole.GROUP_ADMIN);
    }

    @Test
    @WithMockUser(username = username1)
    void GetGroupRole_Ok_ReturnGroupRole() throws Exception {
        mockMvc.perform(
            get("/groups/{groupId}/members/{userId}/role", group.getId(), user2.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType("application/json"))
            .andExpect(jsonPath("$").value("GROUP_MEMBER"));
    }

    @Test
    @WithMockUser(username = username1)
    void UpdateGroupRole_Ok_ReturnGroupRole() throws Exception {
        mockMvc.perform(
            patch("/groups/{groupId}/members/{userId}/role", group.getId(), user2.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content("\"GROUP_ADMIN\""))
            .andExpect(status().isOk())
            .andExpect(content().contentType("application/json"))
            .andExpect(jsonPath("$").value("GROUP_ADMIN"));

        Optional<GroupMember> groupMemberOpt =
            groupMemberRepo.findByUserIdAndChatId(user2.getId(), group.getId());
        assertTrue(groupMemberOpt.isPresent());
        assertEquals(groupMemberOpt.get().getGroupRole(), GroupRole.GROUP_ADMIN);
    }
    // Members end ---------------------------------------------------------------------------------
}