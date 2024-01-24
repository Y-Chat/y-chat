package com.ychat.ychat.repositories;

import com.ychat.ychat.models.UserFirebaseTokenMapping;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserFirebaseTokenMappingRepository extends MongoRepository<UserFirebaseTokenMapping, UUID> {
}
