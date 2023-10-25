package com.github.messageconsumer.service.chat;

import com.github.messageconsumer.web.controller.ChatAlarmController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class ChatSseListner  implements ApplicationListener<ChatSseEvent> {
    private final ChatAlarmController chatAlarmController;

    @Autowired
    public ChatSseListner(ChatAlarmController chatAlarmController) {
        this.chatAlarmController = chatAlarmController;
    }

    @Override
    public void onApplicationEvent(ChatSseEvent event) {
        System.out.println(event.getMessage());
        chatAlarmController.sendEventToClients(event.getMessage());
    }
}
