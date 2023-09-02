package com.github.messageconsumer.service;

import com.github.messageconsumer.dto.CartRmqDto;
import com.github.messageconsumer.entity.Cart;
import com.github.messageconsumer.entity.Product;
import com.github.messageconsumer.repository.CartRepository;
import com.github.messageconsumer.service.util.ValidatCartMethod;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;

@Slf4j
@RequiredArgsConstructor
@Service
public class CartConsumerService {
    private final CartRepository cartRepository;
    private final ValidatCartMethod validatCartMethod;

    @RabbitListener(queues = "post")
    public void receiveCartMessage(CartRmqDto cartRmqDto, Message message, Channel channel) throws IOException {
        try {
            Product validatedProduct = validatCartMethod.validateProduct(cartRmqDto.getProductId());
            cartRepository.save(
                    Cart.builder()
                            .users(cartRmqDto.getUser())
                            .products(validatedProduct)
                            .createdAt(LocalDateTime.now())
                            .isOrdered(false)
                            .quantity(cartRmqDto.getQuantity())
                            .options(cartRmqDto.getOptions())
                            .build()
            );
        } catch (Exception e) {
            log.error("Error processing cart message: " + e.getMessage(), e);

            // 재시도 제한 설정
            int maxRetries = 3; // 최대 재시도 횟수 설정
            Integer retries = (Integer) message.getMessageProperties().getHeader("x-retries");

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
