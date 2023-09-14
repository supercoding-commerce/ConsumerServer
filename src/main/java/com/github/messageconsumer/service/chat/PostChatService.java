package com.github.messageconsumer.service.chat;

import com.github.messageconsumer.collection.Chat;
import com.github.messageconsumer.dto.ChatRmqDto;
import com.github.messageconsumer.repository.ChatRepository;
import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class PostChatService {
    private final ChatRepository chatRepository;

    @Transactional
    public void postChat(ChatRmqDto chatRmqDto, Message message, Channel channel) throws IOException {
        try {
            // customRoomId에 해당하는 Chat document 조회
            Chat chat = chatRepository.findByCustomRoomId(chatRmqDto.getCustomRoomId())
                    .orElse(Chat.builder().customRoomId(chatRmqDto.getCustomRoomId()).build()); // 없으면 새로운 Chat document 생성

            Map<String, String> newMessage = new HashMap<>();
            newMessage.put("sender", chatRmqDto.getSender()); // 발신자 설정
            newMessage.put("content", chatRmqDto.getContent()); // 메시지 내용 설정
            //newMessage.put("createdAt", chatRmqDto.getCreatedAt());
//            int messageTag = chatRmqDto.getMessageTag();
//            int maxMessageTag = chat.getChats().keySet().stream()
//                    .mapToInt(Integer::intValue)
//                    .max()
//                    .orElse(0);
//
//            // messageTag를 조정하여 덮어쓰기
//            int newMessageTag = maxMessageTag > messageTag ? maxMessageTag + messageTag : messageTag;
            String sanitizedKey = chatRmqDto.getCreatedAt().replace(".", "-");
            chat.getChats().put(sanitizedKey, newMessage);
            chatRepository.save(chat);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);

        } catch (Exception e) {
            log.error("Error processing chat message: " + e.getMessage(), e);
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, false);

        }
    }
}
