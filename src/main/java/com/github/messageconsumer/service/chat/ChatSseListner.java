package com.github.messageconsumer.service.chat;

import com.github.messageconsumer.web.controller.ChatAlarmController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
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

    @Override
    //@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onApplicationEvent(ChatSseEvent event) {
        log.info("Received ChatSseEvent: {}", event.getMessage().getContent());
        chatAlarmController.sendEventToClients(event.getMessage().getSellerId(), event.getMessage());
    }
}
