package com.github.messageconsumer.service.chat;

import com.github.messageconsumer.collection.Chat;
import com.github.messageconsumer.dto.CartRmqDto;
import com.github.messageconsumer.dto.ChatRmqDto;
import com.github.messageconsumer.dto.RoomRmqDto;
import com.github.messageconsumer.entity.Cart;
import com.github.messageconsumer.entity.Product;
import com.github.messageconsumer.entity.User;
import com.github.messageconsumer.repository.ChatRepository;
import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class ChatConsumerService {
    private final ChatRepository chatRepository;
    private final PostChatService postChatService;

    @RabbitListener(queues = "postRoom", containerFactory = "rabbitListenerContainerFactory")
    public void postRoom(RoomRmqDto roomRmqDto, Message message, Channel channel) throws IOException {
        try {
            Optional<Chat> chat = chatRepository.findByCustomRoomId(roomRmqDto.getCustomRoomId());
            if (chat.isPresent()) {
                // 이미 존재하는 경우 아무것도 하지 않고 성공 처리
                log.info("Chat already exists for customRoomId: " + roomRmqDto.getCustomRoomId());
            } else {
                // 존재하지 않는 경우 새로운 Chat 객체 생성 및 저장
                Chat newChat = Chat.builder()
                        .customRoomId(roomRmqDto.getCustomRoomId())
                        .sellerId(roomRmqDto.getSellerId())
                        .shopName(roomRmqDto.getShopName())
                        .userId(roomRmqDto.getUserId())
                        .userName(roomRmqDto.getUserName())
                        .productId(roomRmqDto.getProductId())
                        .build();
                chatRepository.save(newChat);
                log.info("New Chat created for customRoomId: " + roomRmqDto.getCustomRoomId());
            }

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

    @RabbitListener(queues = "postChat1", containerFactory = "rabbitListenerContainerFactory")
    public void postChat1(ChatRmqDto chatRmqDto, Message message, Channel channel) throws IOException {
        postChatService.postChat(chatRmqDto, message,  channel);
    }

    @RabbitListener(queues = "postChat2", containerFactory = "rabbitListenerContainerFactory")
    public void postChat2(ChatRmqDto chatRmqDto, Message message, Channel channel) throws IOException {
        postChatService.postChat(chatRmqDto, message,  channel);
    }

    @RabbitListener(queues = "postChat3", containerFactory = "rabbitListenerContainerFactory")
    public void postChat3(ChatRmqDto chatRmqDto, Message message, Channel channel) throws IOException {
        postChatService.postChat(chatRmqDto, message,  channel);
    }
    @RabbitListener(queues = "postChat4", containerFactory = "rabbitListenerContainerFactory")
    public void postChat4(ChatRmqDto chatRmqDto, Message message, Channel channel) throws IOException {
        postChatService.postChat(chatRmqDto, message,  channel);
    }
    @RabbitListener(queues = "postChat5", containerFactory = "rabbitListenerContainerFactory")
    public void postChat5(ChatRmqDto chatRmqDto, Message message, Channel channel) throws IOException {
        postChatService.postChat(chatRmqDto, message,  channel);
    }


}
