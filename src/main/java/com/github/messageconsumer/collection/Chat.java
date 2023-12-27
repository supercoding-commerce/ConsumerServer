package com.github.messageconsumer.collection;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

    private List<Message> chats;

    //private Map<String, Map<String, String>> chats;


    public List<Message> getChats() {
        if (chats == null) {
            chats = new ArrayList<>(); // 빈 리스트를 초기화
        }
        return chats;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Message {
        private String timestamp; // 메시지의 타임스탬프
        private String sender;    // 메시지의 보낸 사람
        private String content;   // 메시지 내용
    }
}

