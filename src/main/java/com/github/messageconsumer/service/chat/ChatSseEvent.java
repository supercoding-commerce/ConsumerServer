package com.github.messageconsumer.service.chat;

import com.github.messageconsumer.dto.ChatRmqDto;
import org.springframework.context.ApplicationEvent;
import org.springframework.stereotype.Component;

public class ChatSseEvent extends ApplicationEvent {
    private ChatRmqDto chatRmqDto;

    public ChatSseEvent(Object source, ChatRmqDto chatRmqDto) {
        super(source);
        this.chatRmqDto = chatRmqDto;
    }

    public ChatRmqDto getMessage() {
        return chatRmqDto;
    }
}
