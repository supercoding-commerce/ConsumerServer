package com.github.messageconsumer.web.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

@RestController
public class ChatAlarmController {
    private final CopyOnWriteArrayList<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    @GetMapping("/chat-alarm")
    public SseEmitter handleSse() {
        SseEmitter emitter = new SseEmitter();

        // 클라이언트 연결이 종료되면 emitters 목록에서 제거
        emitter.onCompletion(() -> emitters.remove(emitter));
        emitter.onTimeout(() -> emitters.remove(emitter));

        emitters.add(emitter);
        return emitter;
    }

    public void sendEventToClients( Map<String, String> data) {
        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(data);
            } catch (Exception ignored) {
                emitters.remove(emitter);
            }
        }
    }
}
