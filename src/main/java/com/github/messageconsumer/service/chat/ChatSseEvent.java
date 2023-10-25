package com.github.messageconsumer.service.chat;

import org.springframework.context.ApplicationEvent;

import java.util.Map;

public class ChatSseEvent extends ApplicationEvent {
    private Map<String, String> message;

    public ChatSseEvent(Object source, Map<String, String> message) {
        super(source);
        this.message = message;
    }

    public Map<String, String> getMessage() {
        return message;
    }
}
