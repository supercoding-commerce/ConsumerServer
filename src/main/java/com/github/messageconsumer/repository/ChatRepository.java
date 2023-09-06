package com.github.messageconsumer.repository;

import com.github.messageconsumer.collection.Chat;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ChatRepository extends MongoRepository<Chat, ObjectId> {
    Optional<Chat> findByCustomRoomId(String customRoomId);
}
