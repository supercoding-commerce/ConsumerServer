package com.github.messageconsumer.service.chat;

import com.github.messageconsumer.web.controller.ChatAlarmController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
@Slf4j
@Component
public class ChatSseListner  implements ApplicationListener<ChatSseEvent> {
    private final ChatAlarmController chatAlarmController;

    @Autowired
    public ChatSseListner(ChatAlarmController chatAlarmController) {
        this.chatAlarmController = chatAlarmController;
    }

//    @Override
//    public void onApplicationEvent(ChatSseEvent event) {
//        // 트랜잭션 커밋 후 비동기 메서드를 호출합니다.
//        this.onAfterCommit(event);
//    }
    @Override
    @Async
    //@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onApplicationEvent(ChatSseEvent event) {
        log.info("Received ChatSseEvent: {}", event.getMessage().getContent());
        chatAlarmController.sendEventToClients(event.getMessage().getSellerId(), event.getMessage());
    }

//    // 이 메서드는 트랜잭션이 커밋된 후에 호출되어 SSE 이벤트를 발행합니다.
//    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
//    public void onAfterCommit(ChatSseEvent event) {
//        sendSseEventAsync(event);
//    }

//    @Async
//    public void sendSseEventAsync(ChatSseEvent event) {
//        log.info("Sending ChatSseEvent asynchronously: {}", event.getMessage().getContent());
//        chatAlarmController.sendEventToClients(event.getMessage().getSellerId(), event.getMessage());
//    }
}
