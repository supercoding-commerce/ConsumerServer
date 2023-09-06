package com.github.messageconsumer.service.chat;

import com.github.messageconsumer.collection.Chat;
import com.github.messageconsumer.dto.ChatRmqDto;
import com.github.messageconsumer.repository.ChatRepository;
import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class PostChatService {
    private final ChatRepository chatRepository;

    public void postChat(ChatRmqDto chatRmqDto, Message message, Channel channel) throws IOException {
        try {
            // customRoomId에 해당하는 Chat document 조회
            Chat chat = chatRepository.findByCustomRoomId(chatRmqDto.getCustomRoomId())
                    .orElse(Chat.builder().customRoomId(chatRmqDto.getCustomRoomId()).build()); // 없으면 새로운 Chat document 생성

            Map<String, String> newMessage = new HashMap<>();
            newMessage.put("sender", chatRmqDto.getSender()); // 발신자 설정
            newMessage.put("content", chatRmqDto.getContent()); // 메시지 내용 설정
            int messageTag = chatRmqDto.getMessageTag();
            int maxMessageTag = chat.getChats().keySet().stream()
                    .mapToInt(Integer::intValue)
                    .max()
                    .orElse(0);
            if(maxMessageTag > messageTag ){
                messageTag = maxMessageTag + 1;
            }
            chat.getChats().put(messageTag, newMessage);
            chatRepository.save(chat);


        } catch (Exception e) {
            log.error("Error processing cart message: " + e.getMessage(), e);

            // 재시도 제한 설정
            int maxRetries = 3; // 최대 재시도 횟수 설정
            Integer retries = (Integer) message.getMessageProperties().getHeaders().getOrDefault("x-retries", 0);

            if (retries < maxRetries) {
                // 재시도 횟수 증가
                retries++;
                message.getMessageProperties().setHeader("x-retries", retries);

                // 일정 시간 후 재시도
                long delayMillis = 5000; // 5초 대기
                message.getMessageProperties().setExpiration(String.valueOf(delayMillis));
                channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
            } else {
                // 최대 재시도 횟수를 초과하면 메시지를 버립니다.
                channel.basicReject(message.getMessageProperties().getDeliveryTag(), false);
                log.error("Max retries exceeded. Discarding message.");
            }
        }
    }
}
