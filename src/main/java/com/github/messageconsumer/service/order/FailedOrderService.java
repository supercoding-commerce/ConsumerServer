package com.github.messageconsumer.service.order;

import com.github.messageconsumer.dto.OrderRmqDto;
import com.github.messageconsumer.entity.FailedLog;
import com.github.messageconsumer.entity.Order;
import com.github.messageconsumer.entity.Product;
import com.github.messageconsumer.repository.FailedLogRepository;
import com.github.messageconsumer.repository.OrderRepository;
import com.github.messageconsumer.repository.ProductRepository;
import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class FailedOrderService {

    private final FailedLogRepository failedLogRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    @Transactional
    @RabbitListener(queues = "dlqOrder", containerFactory = "deadListenerContainer")
    public void getDlqOrder(OrderRmqDto orderRmqDto, Message message, Channel channel) throws IOException {
        try {
            String messageBody = new String(message.getBody(), "UTF-8");

            // 실패한 메시지를 데이터베이스에 저장
            FailedLog failedLog = new FailedLog();
            failedLog.setMessageBody(messageBody);
            failedLog.setCreatedAt(getKoreanTime());
            failedLogRepository.save(failedLog);

            String causes = (String) message.getMessageProperties().getHeaders().getOrDefault("failed-causes", "서버오류");
            Optional<Product> productOptional = productRepository.findById(orderRmqDto.getProductId());
            if(productOptional.isPresent()){
                orderRepository.save(
                                Order.builder()
                                        .products(productOptional.get())
                                        .createdAt(getKoreanTime())
                                        .orderState(6)
                                        .totalPrice(orderRmqDto.getTotal_price())
                                        .quantity(orderRmqDto.getQuantity())
                                        .options(orderRmqDto.getOptions())
                                        .failed_causes(causes)
                                        .build()
            );
            }


            log.info("Failed Order message saved to the database.");
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (IOException e) {
            log.error("Error processing failed order message: " + e.getMessage(), e);
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, false);
        }


    }
    public LocalDateTime getKoreanTime(){
        ZoneId koreanZone = ZoneId.of("Asia/Seoul");
        ZonedDateTime koreanTime = ZonedDateTime.now(koreanZone);

        // Convert it to LocalDateTime
        return koreanTime.toLocalDateTime();

    }
}
