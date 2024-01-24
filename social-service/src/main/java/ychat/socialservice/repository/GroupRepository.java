package ychat.socialservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ychat.socialservice.model.group.Group;

import java.util.UUID;

@Repository
public interface GroupRepository extends JpaRepository<Group, UUID> {}
