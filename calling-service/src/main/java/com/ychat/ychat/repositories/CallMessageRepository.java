package com.ychat.ychat.repositories;

import com.ychat.ychat.models.Call;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CallMessageRepository extends MongoRepository<Call, UUID> {

    List<Call> findByCalleeId(UUID id, Pageable pageable);

    List<Call> findByCallerId(UUID id, Pageable pageable);
}
