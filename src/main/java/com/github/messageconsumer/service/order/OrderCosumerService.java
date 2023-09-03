package com.github.messageconsumer.service.order;

import com.github.messageconsumer.dto.OrderRmqDto;
import com.github.messageconsumer.entity.Cart;
import com.github.messageconsumer.entity.Order;
import com.github.messageconsumer.entity.Product;
import com.github.messageconsumer.entity.User;
import com.github.messageconsumer.repository.CartRepository;
import com.github.messageconsumer.repository.OrderRepository;
import com.github.messageconsumer.service.order.util.ValidateOrderMethod;
import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
@Slf4j
@RequiredArgsConstructor
@Service
public class OrderCosumerService {
    private final ValidateOrderMethod validateOrderMethod;
    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    @RabbitListener(queues = "postOrder")
    public void postOrderQueue(OrderRmqDto orderRmqDto, Message message, Channel channel) throws IOException {
        try {
            Product validatedProduct = validateOrderMethod.validateProduct(orderRmqDto.getProductId());
            User validatedUser = validateOrderMethod.validateUser(orderRmqDto.getUserId());
            Cart validatedCart = null;

            if (orderRmqDto.getCartId() != null) {
                validatedCart = validateOrderMethod.validateCart(orderRmqDto.getUserId(), orderRmqDto.getCartId());
                //장바구니에서 주문한 경우, 장바구니 주문상태 변화
                if(validatedCart != null){
                    validatedCart.setIsOrdered(true);
                    cartRepository.save(validatedCart);
                }
            }

            orderRepository.save(
                    Order.builder()
                            .users(validatedUser)
                            .products(validatedProduct)
                            .carts(validatedCart)
                            .createdAt(LocalDateTime.now())
                            .orderState(orderRmqDto.getOrderState())
                            .total_price(orderRmqDto.getTotal_price())
                            .quantity(orderRmqDto.getQuantity())
                            .options(orderRmqDto.getOptions())
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
