package com.github.messageconsumer.web.controller;

import com.github.messageconsumer.dto.ChatRmqDto;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@RestController
@Slf4j
public class ChatAlarmController {

//    @Getter
//    private static class AlarmKey {
//        private final Long sellerId;
//        private final Long userId;
//
//        public AlarmKey(Long sellerId, Long userId) {
//            this.sellerId = sellerId;
//            this.userId = userId;
//        }
//    }

    //CopyOnWriteArrayList는 쓰기 작업이 드물고 읽기 작업이 빈번할 때 가장 잘 작동합니다.
    //쓰기 작업이 빈번하게 발생하면 매번 전체 리스트의 복사본을 만들어야 하기 때문에 메모리 사용량이 증가하고 성능이 저하될 수 있습니다.
    //private final CopyOnWriteArrayList<SseEmitterWrapper> emitters = new CopyOnWriteArrayList<>();

    private final ConcurrentHashMap<String, SseEmitter> userEmitters = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, SseEmitter> sellerEmitters = new ConcurrentHashMap<>();

    @CrossOrigin(origins = "*")
    @GetMapping("/chat-alarm/seller/{customRoomId}")
    public SseEmitter handleSellerSse(@PathVariable String customRoomId) {
        SseEmitter emitter = new SseEmitter(3600000L);

        // 클라이언트 연결이 종료되면 emitters 목록에서 제거
        emitter.onCompletion(() -> removeEmitter(customRoomId, sellerEmitters));
        emitter.onTimeout(() -> removeEmitter(customRoomId, sellerEmitters));

        // 새로운 연결이 만들어질 때 기존의 연결을 제거하고 새 연결을 저장
        sellerEmitters.put(customRoomId, emitter);
        return emitter;
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/chat-alarm/user/{customRoomId}")
    public SseEmitter handleUserSse(@PathVariable String customRoomId) {
        SseEmitter emitter = new SseEmitter(3600000L);

        // 클라이언트 연결이 종료되면 emitters 목록에서 제거
        emitter.onCompletion(() -> removeEmitter(customRoomId, userEmitters));
        emitter.onTimeout(() -> removeEmitter(customRoomId, userEmitters));

        // 새로운 연결이 만들어질 때 기존의 연결을 제거하고 새 연결을 저장
        userEmitters.put(customRoomId, emitter);
        return emitter;
    }


    private void removeEmitter(String customRoomId, ConcurrentHashMap<String, SseEmitter> emitters) {
        emitters.remove(customRoomId);
    }

    public void sendEventToClients(String customRoomId, ChatRmqDto data) {
        SseEmitter userEmitter = userEmitters.get(customRoomId);
        SseEmitter sellerEmitter = sellerEmitters.get(customRoomId);
        if (userEmitter != null) {
            try {
                userEmitter.send(SseEmitter.event().name("sse").data(data).reconnectTime(500));
            } catch (Exception ignored) {
                removeEmitter(customRoomId, userEmitters);
            }
        }

        if (sellerEmitter != null) {
            try {
                sellerEmitter.send(SseEmitter.event().name("sse").data(data).reconnectTime(500));
            } catch (Exception ignored) {
                removeEmitter(customRoomId, sellerEmitters);
            }
        }
    }
}

