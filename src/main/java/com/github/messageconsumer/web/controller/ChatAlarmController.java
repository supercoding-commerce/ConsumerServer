package com.github.messageconsumer.web.controller;

import com.github.messageconsumer.dto.ChatRmqDto;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import java.util.concurrent.ConcurrentHashMap;


@RestController
@Slf4j
public class ChatAlarmController {

    //CopyOnWriteArrayList는 쓰기 작업이 드물고 읽기 작업이 빈번할 때 가장 잘 작동합니다.
    //쓰기 작업이 빈번하게 발생하면 매번 전체 리스트의 복사본을 만들어야 하기 때문에 메모리 사용량이 증가하고 성능이 저하될 수 있습니다.
    //private final CopyOnWriteArrayList<SseEmitterWrapper> emitters = new CopyOnWriteArrayList<>();

    private final ConcurrentHashMap<Long, SseEmitter> userEmitters = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Long, SseEmitter> sellerEmitters = new ConcurrentHashMap<>();

    @CrossOrigin(origins = "*")
    @GetMapping("/chat-alarm/seller/{sellerId}")
    public SseEmitter handleSellerSse(@PathVariable Long sellerId) {
        SseEmitter emitter = new SseEmitter(3600000L);

        // 클라이언트 연결이 종료되면 emitters 목록에서 제거
        emitter.onCompletion(() -> removeEmitter(sellerId, sellerEmitters));
        emitter.onTimeout(() -> removeEmitter(sellerId, sellerEmitters));

        // 새로운 연결이 만들어질 때 기존의 연결을 제거하고 새 연결을 저장
        sellerEmitters.put(sellerId, emitter);
        return emitter;
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/chat-alarm/user/{sellerId}/{userId}")
    public SseEmitter handleUserSse(@PathVariable Long sellerId) {
        SseEmitter emitter = new SseEmitter(3600000L);

        // 클라이언트 연결이 종료되면 emitters 목록에서 제거
        emitter.onCompletion(() -> removeEmitter(sellerId, userEmitters));
        emitter.onTimeout(() -> removeEmitter(sellerId, userEmitters));

        // 새로운 연결이 만들어질 때 기존의 연결을 제거하고 새 연결을 저장
        userEmitters.put(sellerId, emitter);
        return emitter;
    }


    private void removeEmitter(Long sellerId, ConcurrentHashMap<Long, SseEmitter> emitters) {
        emitters.remove(sellerId);
    }

    public void sendEventToClients(Long sellerId, ChatRmqDto data) {
        SseEmitter userEmitter = userEmitters.get(sellerId);
        SseEmitter sellerEmitter = sellerEmitters.get(sellerId);
        if (userEmitter != null) {
            try {
                userEmitter.send(SseEmitter.event().name("sse").data(data).reconnectTime(500));
            } catch (Exception ignored) {
                removeEmitter(sellerId, userEmitters);
            }
        }

        if (sellerEmitter != null) {
            try {
                sellerEmitter.send(SseEmitter.event().name("sse").data(data).reconnectTime(500));
            } catch (Exception ignored) {
                removeEmitter(sellerId, sellerEmitters);
            }
        }
    }
}

