package com.github.messageconsumer.service.order;

import com.github.messageconsumer.dto.OrderRmqDto;
import com.github.messageconsumer.entity.Cart;
import com.github.messageconsumer.entity.Order;
import com.github.messageconsumer.entity.Product;
import com.github.messageconsumer.entity.User;
import com.github.messageconsumer.repository.CartRepository;
import com.github.messageconsumer.repository.OrderRepository;
import com.github.messageconsumer.service.order.exception.OrderException;
import com.github.messageconsumer.service.order.util.ValidateOrderMethod;
import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Slf4j
@RequiredArgsConstructor
@Service
public class OrderConsumerService {
    private final ValidateOrderMethod validateOrderMethod;
    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;

    @Transactional
    @RabbitListener(
            bindings = @QueueBinding(
                    exchange = @Exchange(value = "exchange"),
                    value = @Queue(value = "postOrder",
                    arguments = @Argument(name="x-dead-letter-exchange", value = "dlqExchange"))
            ), ackMode = "MANUAL", containerFactory = "rabbitListenerContainerFactory")
    public void postOrder(OrderRmqDto orderRmqDto, Message message, Channel channel ) throws IOException {
        try {
            Product validatedProduct = validateOrderMethod.validateProduct(orderRmqDto.getProductId());
            User validatedUser = validateOrderMethod.validateUser(orderRmqDto.getUserId());
            boolean isStockValid = validateOrderMethod.validateStock(orderRmqDto.getQuantity(), validatedProduct);
            if (!isStockValid) {
                // 재고 부족 시 Nack 처리
                log.debug("재고가 부족합니다");
                message.getMessageProperties().setHeader("failed_causes", "재고 부족");
                channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, false);
                return;
            }

            if(orderRmqDto.getCartId() != null) {
                log.info("ordered cartId: " + orderRmqDto.getCartId());
                log.info("ordered userId: " + orderRmqDto.getUserId());
                Cart validatedCart = validateOrderMethod.validateCart(orderRmqDto.getCartId(), orderRmqDto.getUserId());
                //장바구니에서 주문한 경우, 장바구니 주문상태 변화
                validatedCart.setCartState(1);
                cartRepository.save(validatedCart);

                orderRepository.save(
                        Order.builder()
                                .users(validatedUser)
                                .products(validatedProduct)
                                .carts(validatedCart)
                                .sellers(validatedProduct.getSeller())
                                .createdAt(getKoreanTime())
                                .orderState(orderRmqDto.getOrderState())
                                .orderTag(orderRmqDto.getOrderTag())
                                .totalPrice(orderRmqDto.getTotal_price())
                                .quantity(orderRmqDto.getQuantity())
                                .options(orderRmqDto.getOptions())
                                .build()
                );
            }else {
                orderRepository.save(
                        Order.builder()
                                .users(validatedUser)
                                .products(validatedProduct)
                                .sellers(validatedProduct.getSeller())
                                .createdAt(getKoreanTime())
                                .orderState(orderRmqDto.getOrderState())
                                .orderTag(orderRmqDto.getOrderTag())
                                .totalPrice(orderRmqDto.getTotal_price())
                                .quantity(orderRmqDto.getQuantity())
                                .options(orderRmqDto.getOptions())
                                .build()
                );

            }
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);

        } catch (IOException | RuntimeException e) {
            log.debug("postOrder exception: " + e.getMessage(), e);
            message.getMessageProperties().setHeader("failed_causes", "관리자 문의");
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, false);
        }
    }


//    @Transactional
//    @RabbitListener(
//            bindings = @QueueBinding(
//                    exchange = @Exchange(value = "exchange"),
//                    value = @Queue(value = "putOrder",
//                            arguments = @Argument(name="x-dead-letter-exchange", value = "dlqExchange"))
//            ), ackMode = "MANUAL", containerFactory = "rabbitListenerContainerFactory")
//    public void putOrderQueue(OrderRmqDto orderRmqDto, Message message, Channel channel) throws IOException {
//        try {
//            Product validatedProduct = validateOrderMethod.validateProduct(orderRmqDto.getProductId());
//            Order validatedOrder = validateOrderMethod.validateOrder(orderRmqDto.getOrderId(), orderRmqDto.getUserId());
//            Integer inputQuantity = orderRmqDto.getQuantity();
//            String inputOptions = orderRmqDto.getOptions();
//            boolean isStockValid = validateOrderMethod.validateStock(inputQuantity, validatedProduct);
//
//            if (!isStockValid) {
//                // 재고 부족 시 Nack 처리
//                log.debug("재고가 부족합니다");
//                message.getMessageProperties().setHeader("failed_causes", "재고 부족");
//                channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, false);
//                return;
//
//            }
//            validatedOrder.setQuantity(inputQuantity);
//            validatedOrder.setOptions(inputOptions);
//
//            orderRepository.save(
//                    validatedOrder
//            );
//
//            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
//        } catch (IOException e) {
//            log.debug("putOrder exception: " + e.getMessage(), e);
//            message.getMessageProperties().setHeader("failed_causes", "관리자 문의");
//            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, false);
//
//        }
//    }

    public LocalDateTime getKoreanTime(){
        ZoneId koreanZone = ZoneId.of("Asia/Seoul");
        ZonedDateTime koreanTime = ZonedDateTime.now(koreanZone);

        // Convert it to LocalDateTime
        return koreanTime.toLocalDateTime();

    }
}
