package com.github.messageconsumer.service.chat;

import com.github.messageconsumer.collection.Chat;
import com.github.messageconsumer.dto.CartRmqDto;
import com.github.messageconsumer.dto.ChatRmqDto;
import com.github.messageconsumer.dto.RoomRmqDto;
import com.github.messageconsumer.entity.Cart;
import com.github.messageconsumer.entity.Product;
import com.github.messageconsumer.entity.User;
import com.github.messageconsumer.repository.ChatRepository;
import com.github.messageconsumer.service.order.exception.OrderException;
import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @RabbitListener(
            bindings = @QueueBinding(
                    exchange = @Exchange(value = "exchange"),
                    value = @Queue(value = "postRoom",
                            arguments = @Argument(name="x-dead-letter-exchange", value = "dlqExchange"))
            ), ackMode = "MANUAL", containerFactory = "rabbitListenerContainerFactory")
    public void postRoom(RoomRmqDto roomRmqDto, Message message, Channel channel) throws IOException {
        try {
            Optional<Chat> chatOptional = chatRepository.findByCustomRoomId(roomRmqDto.getCustomRoomId());
            if (chatOptional.isPresent()) {
                Chat chat = chatOptional.get();
                // Chat 객체 업데이트
                chat.setSellerId(roomRmqDto.getSellerId());
                chat.setShopName(roomRmqDto.getShopName());
                chat.setUserId(roomRmqDto.getUserId());
                chat.setUserName(roomRmqDto.getUserName());
                chat.setProductId(roomRmqDto.getProductId());

                chatRepository.save(chat); // Chat 업데이트 후 저장
                channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            } else {
                System.out.println("postRoom222222222");
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
                channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            }

        } catch (OrderException e) {
            log.error("Error processing chat message: " + e.getMessage(), e);
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, false);

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
