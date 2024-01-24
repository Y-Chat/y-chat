package ychat.socialservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ychat.socialservice.model.chat.DirectChat;

import java.util.UUID;

@Repository
public interface DirectChatRepository extends JpaRepository<DirectChat, UUID> {}
