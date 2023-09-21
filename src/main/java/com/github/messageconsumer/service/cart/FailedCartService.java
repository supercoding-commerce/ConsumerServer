package com.github.messageconsumer.service.cart;


import com.github.messageconsumer.dto.CartRmqDto;
import com.github.messageconsumer.dto.OrderRmqDto;
import com.github.messageconsumer.entity.Cart;
import com.github.messageconsumer.entity.FailedLog;
import com.github.messageconsumer.entity.Order;
import com.github.messageconsumer.entity.Product;
import com.github.messageconsumer.repository.CartRepository;
import com.github.messageconsumer.repository.FailedLogRepository;
import com.github.messageconsumer.repository.ProductRepository;
import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class FailedCartService {
    private final ProductRepository productRepository;
    private final RabbitTemplate rabbitTemplate;
    private final FailedLogRepository failedLogRepository;
    private final CartRepository cartRepository;

    @RabbitListener(queues = "dlqCart", containerFactory = "deadListenerContainer")
    public void getDlqCart(CartRmqDto cartRmqDto, Message message, Channel channel) throws IOException {
        try {
            String messageBody = new String(message.getBody(), "UTF-8");
            // 실패한 메시지를 데이터베이스에 저장
            FailedLog failedLog = new FailedLog();
            failedLog.setMessageBody(messageBody);
            failedLog.setCreatedAt(LocalDateTime.now());
            failedLogRepository.save(failedLog);

            String causes = (String) message.getMessageProperties().getHeaders().getOrDefault("failed-causes", "서버오류");
            Optional<Product> productOptional = productRepository.findById(cartRmqDto.getProductId());
            if(productOptional.isPresent()) {
                cartRepository.save(
                        Cart.builder()
                                .products(productOptional.get())
                                .createdAt(LocalDateTime.now())
                                .cartState(3)
                                .quantity(cartRmqDto.getQuantity())
                                .options(cartRmqDto.getOptions())
                                .failed_causes(causes)
                                .build()
                );
            }

            log.info("Failed Cart message saved to the database.");
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            } catch (IOException e) {
                log.error("Error processing failed cart message: " + e.getMessage(), e);
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, false);
            }

        }
}
