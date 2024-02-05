package ychat.socialservice.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ychat.socialservice.model.user.BlockedUser;
import ychat.socialservice.model.user.BlockedUserId;
import ychat.socialservice.model.user.User;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface BlockedUserRepository extends JpaRepository<BlockedUser, BlockedUserId> {
    Page<BlockedUser> findAllByFromUserId(UUID fromUserId, Pageable pageable);

    Optional<BlockedUser> findByFromUserIdAndToUserId(UUID fromUserId, UUID toUserId);
}
