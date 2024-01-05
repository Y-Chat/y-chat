package ychat.socialservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ychat.socialservice.model.chat.ChatStatus;
import ychat.socialservice.model.chat.ChatType;
import ychat.socialservice.repository.ChatRepository;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class ChatService {
    private ChatRepository chatRepository;

    public ChatService(ChatRepository chatRepository) {
        this.chatRepository = chatRepository;
    }

    public List<ChatType> getChatTypes(List<UUID> chatIds) {

    }

    public void createDirectChat(UUID fstUserId, UUID sndUserId) {
        return;
    }

    public Set<UUID> getChatParticipants(UUID chatId) {
    }

    public ChatStatus getStatus(UUID chatId, UUID userId) {
    }

    public void setStatus(UUID chatId, UUID userId, ChatStatus chatStatus) {
    }

}
