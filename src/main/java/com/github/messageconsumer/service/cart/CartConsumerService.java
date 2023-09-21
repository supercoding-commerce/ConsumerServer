package com.github.messageconsumer.service.cart;

import com.github.messageconsumer.dto.CartRmqDto;
import com.github.messageconsumer.entity.Cart;
import com.github.messageconsumer.entity.Product;
import com.github.messageconsumer.entity.User;
import com.github.messageconsumer.repository.CartRepository;
import com.github.messageconsumer.service.cart.util.ValidatCartMethod;
import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;

@Slf4j
@RequiredArgsConstructor
@Service
public class CartConsumerService {
    private final CartRepository cartRepository;
    private final ValidatCartMethod validatCartMethod;

    @Transactional
    @RabbitListener(
            bindings = @QueueBinding(
                    exchange = @Exchange(value = "exchange"),
                    value = @Queue(value = "postCart",
                            arguments = @Argument(name="x-dead-letter-exchange", value = "dlqExchange"))
            ), ackMode = "MANUAL", containerFactory = "rabbitListenerContainerFactory")
    public void postCartQueue(CartRmqDto cartRmqDto, Message message, Channel channel) throws IOException {

           try {
               Product validatedProduct = validatCartMethod.validateProduct(cartRmqDto.getProductId());
               User validatedUser = validatCartMethod.validateUser(cartRmqDto.getUserId());
               cartRepository.save(
                       Cart.builder()
                               .users(validatedUser)
                               .products(validatedProduct)
                               .createdAt(LocalDateTime.now())
                               .isOrdered(false)
                               .cartState(0)
                               .quantity(cartRmqDto.getQuantity())
                               .options(cartRmqDto.getOptions())
                               .build()
               );
               channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
           }catch (Exception e){
               log.error("postCart exception: " + e.getMessage(), e);
               message.getMessageProperties().setHeader("failed_causes", "관리자 문의");
               channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, false);
           }

    }

    @Transactional
    @RabbitListener(queues = "putCart", containerFactory = "rabbitListenerContainerFactory")
    public void putCartQueue(CartRmqDto cartRmqDto, Message message, Channel channel) throws IOException {
        try {
            Cart validatedCart = validatCartMethod.validateCart(cartRmqDto.getCartId(), cartRmqDto.getUserId());
            validatedCart.setQuantity(cartRmqDto.getQuantity());
            validatedCart.setOptions(cartRmqDto.getOptions());

            cartRepository.save(
                    validatedCart
            );
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            log.error("putCart exception: " + e.getMessage(), e);
            message.getMessageProperties().setHeader("failed_causes", "관리자 문의");
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, false);
        }
    }
}
