package com.github.messageconsumer.collection;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "chat")
public class Chat {
    @Id // MongoDB ObjectId
    private String chatId;

    private String customRoomId;

    private Long sellerId;

    private Long productId;

    private Long userId;

    private String shopName;

    private String userName;


    private Map<String, Map<String, String>> chats;


    public Map<String, Map<String, String>> getChats() {
        if (chats == null) {
            chats = new HashMap<>(); // 빈 Map을 초기화
        }
        return chats;
    }
}

