package ychat.socialservice.service.dto;

import ychat.socialservice.model.chat.*;
import ychat.socialservice.model.group.Group;
import ychat.socialservice.model.group.GroupMember;
import ychat.socialservice.model.group.GroupProfile;
import ychat.socialservice.model.user.BlockedUser;
import ychat.socialservice.model.user.User;
import ychat.socialservice.model.user.UserProfile;
import ychat.socialservice.model.user.UserSettings;

public class DTOConverter {
    // User start ----------------------------------------------------------------------------------
    public static UserProfileDTO convertToDTO(UserProfile userProfile) {
        return new UserProfileDTO(
            userProfile.getFirstName(), userProfile.getLastName(),
            userProfile.getProfilePictureId(), null,
            userProfile.getProfileDescription()
        );
    }

    public static UserProfile convertToEntity(UserProfileDTO userProfileDTO) {
        UserProfile userProfile = new UserProfile(
            userProfileDTO.firstName(),
            userProfileDTO.lastName()
        );
        userProfile.setProfileDescription(userProfile.getProfileDescription());
        userProfile.setProfilePictureId(userProfileDTO.profilePictureId());
        return userProfile;
    }

    public static UserSettingsDTO convertToDTO(UserSettings userSettings) {
        return new UserSettingsDTO(userSettings.isReadReceipts(), userSettings.isLastSeen());
    }

    public static UserDTO convertToDTO(User user) {
        return new UserDTO(
            user.getId(), convertToDTO(user.getUserProfile()),
            convertToDTO(user.getUserSettings())
        );
    }

    public static BlockedUserDTO convertToDTO(BlockedUser blockedUser) {
        return new BlockedUserDTO(
            blockedUser.getToUser().getId(),
            DTOConverter.convertToDTO(blockedUser.getToUser().getUserProfile()),
            blockedUser.getCreated()
        );
    }
    // User end ------------------------------------------------------------------------------------

    // Group start ---------------------------------------------------------------------------------
    public static GroupProfileDTO convertToDTO(GroupProfile groupProfile) {
        return new GroupProfileDTO(
            groupProfile.getGroupName(), groupProfile.getProfilePictureId(),
            null, groupProfile.getProfileDescription()
        );
    }

    public static GroupProfile convertToEntity(GroupProfileDTO groupProfileDTO) {
        GroupProfile groupProfile = new GroupProfile(groupProfileDTO.groupName());
        groupProfile.setProfileDescription(groupProfileDTO.profileDescription());
        groupProfile.setProfilePictureId(groupProfileDTO.profilePictureId());
        return groupProfile;
    }

    public static GroupDTO convertToDTO(Group group) {
        return new GroupDTO(group.getId(), convertToDTO(group.getGroupProfile()));
    }
    // Group end -----------------------------------------------------------------------------------

    // Chat start ----------------------------------------------------------------------------------
    public static ChatDTO convertToDTO(DirectChat directChat, User user) {
        DirectChatMember otherMember = directChat.getOtherMember(user);
        if (otherMember == null) {
            DirectChatMember member = directChat.getMember(user);
            return new ChatDTO(
                directChat.getId(),
                member.getOtherUserId(),
                null
            );
        } else {
            User otherUser = otherMember.getUser();
            return new ChatDTO(
                directChat.getId(),
                otherUser.getId(),
                convertToDTO(otherUser.getUserProfile())
            );
        }
    }

    public static ChatDTO convertToChatDTO(Group group) {
        return new ChatDTO(
            group.getId(),
            convertToDTO(group.getGroupProfile())
        );
    }

    public static ChatDTO convertToDTO(Chat chat, User user) {
        return chat.getClass() == DirectChat.class
                ? convertToDTO((DirectChat) chat, user) : convertToChatDTO((Group) chat);
    }

    public static ChatMemberDTO convertToDTO(DirectChatMember directChatMember) {
        User user = directChatMember.getUser();
        return new ChatMemberDTO(
            user.getId(),
            convertToDTO(user.getUserProfile())
        );
    }

    public static ChatMemberDTO convertToDTO(GroupMember groupMember) {
        User user = groupMember.getUser();
        return new ChatMemberDTO(
            user.getId(),
            convertToDTO(user.getUserProfile()),
            groupMember.getGroupRole()
        );
    }

    public static ChatMemberDTO convertToDTO(ChatMember chatMember) {
        return chatMember.getClass() == DirectChatMember.class
                ? convertToDTO((DirectChatMember) chatMember)
                : convertToDTO((GroupMember) chatMember);
    }
    // Chat end ------------------------------------------------------------------------------------
}
