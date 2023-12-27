package com.github.messageconsumer.service.chat;

import com.github.messageconsumer.collection.Chat;
import com.github.messageconsumer.dto.ChatRmqDto;
import com.github.messageconsumer.repository.ChatRepository;
import com.github.messageconsumer.web.controller.ChatAlarmController;
import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
@Service
public class PostChatService {
    private final ChatRepository chatRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    //@Transactional
    public void postChat(ChatRmqDto chatRmqDto, Message message, Channel channel) throws IOException {
        try {
            // customRoomId에 해당하는 Chat document 조회
            Chat chat = chatRepository.findByCustomRoomId(chatRmqDto.getCustomRoomId())
                    .orElse(Chat.builder().customRoomId(chatRmqDto.getCustomRoomId()).build()); // 없으면 새로운 Chat document 생성

            //새로운 메시지 인스턴스
            Chat.Message newMessage = Chat.Message.builder()
                    .timestamp(chatRmqDto.getCreatedAt().replace(".", "-"))
                    .sender(chatRmqDto.getSender())
                    .content(chatRmqDto.getContent())
                    .build();

            // 새로운 메시지를 chats 리스트에 추가
            chat.getChats().add(newMessage);
            chatRepository.save(chat);


            //SSE
            applicationEventPublisher.publishEvent(new ChatSseEvent(this, chatRmqDto));

            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);

        } catch (Exception e) {
            log.error("Error processing chat message: " + e.getMessage(), e);
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, false);

        }

    }

}
