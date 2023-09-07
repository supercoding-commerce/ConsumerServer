package com.github.messageconsumer.service.cart;


import com.github.messageconsumer.entity.FailedLog;
import com.github.messageconsumer.repository.FailedLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;

@Slf4j
@RequiredArgsConstructor
@Service
public class FailedCartService {

    private final RabbitTemplate rabbitTemplate;
    private final FailedLogRepository failedLogRepository;

    @RabbitListener(queues = "dlqCart", containerFactory = "rabbitListenerContainerFactory")
    public void getDlqCart(@Payload Message message){
        try {
            String messageBody = new String(message.getBody(), "UTF-8");

            // 실패한 메시지를 데이터베이스에 저장
            FailedLog failedLog = new FailedLog();
            failedLog.setMessageBody(messageBody);
            failedLog.setCreatedAt(LocalDateTime.now());
            failedLogRepository.save(failedLog);

            log.info("Failed Cart message saved to the database.");
        } catch (IOException e) {
            log.error("Error processing failed cart message: " + e.getMessage(), e);
        }


    }

}
