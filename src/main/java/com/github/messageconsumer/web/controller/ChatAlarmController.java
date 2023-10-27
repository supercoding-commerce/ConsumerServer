package com.github.messageconsumer.web.controller;

import com.github.messageconsumer.dto.ChatRmqDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

@RestController
public class ChatAlarmController {
    private static class SseEmitterWrapper {
        private final Long sellerId;
        private final SseEmitter emitter;

        public SseEmitterWrapper(Long sellerId, SseEmitter emitter) {
            this.sellerId = sellerId;
            this.emitter = emitter;
        }

        public Long getSellerId() {
            return sellerId;
        }

        public SseEmitter getEmitter() {
            return emitter;
        }
    }

    private final CopyOnWriteArrayList<SseEmitterWrapper> emitters = new CopyOnWriteArrayList<>();

    @GetMapping("/chat-alarm/{sellerId}")
    public SseEmitter handleSse(@PathVariable Long sellerId) {
        SseEmitter emitter = new SseEmitter(60000L);

        // 클라이언트 연결이 종료되면 emitters 목록에서 제거
        emitter.onCompletion(() -> removeEmitter(sellerId, emitter));
        emitter.onTimeout(() -> removeEmitter(sellerId, emitter));

        SseEmitterWrapper wrapper = new SseEmitterWrapper(sellerId, emitter);
        emitters.add(wrapper);
        return emitter;
    }

    private void removeEmitter(Long sellerId, SseEmitter emitter) {
        emitters.removeIf(wrapper -> wrapper.getSellerId().equals(sellerId) && wrapper.getEmitter().equals(emitter));
    }

    public void sendEventToClients(Long sellerId, ChatRmqDto data) {
        for (SseEmitterWrapper wrapper : emitters) {
            if (wrapper.getSellerId().equals(sellerId)) {
                try {
                    wrapper.getEmitter().send(data);
                } catch (Exception ignored) {
                    removeEmitter(sellerId, wrapper.getEmitter());
                }
            }
        }
    }
}

