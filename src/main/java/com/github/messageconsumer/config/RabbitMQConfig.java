package com.github.messageconsumer.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Configuration
public class RabbitMQConfig {
    public static final String EXCHANGE_NAME = "exchange";
    // 여러 개의 큐와 라우팅 키를 리스트로 관리
    public static final List<String> QUEUE_NAMES = Arrays.asList("postCart", "putCart", "postOrder", "putOrder", "postPayment", "putPayment");
    public static final List<String> ROUTING_KEYS = Arrays.asList("postCart", "putCart", "postOrder", "putOrder", "postPayment", "putPayment");

    @Bean
    public DirectExchange exchange() {
        return new DirectExchange(EXCHANGE_NAME);
    }

    @Bean
    public List<Queue> queues() {
        // 큐들을 생성하고 리스트로 반환
        List<Queue> queues = new ArrayList<>();
        for (String queueName : QUEUE_NAMES) {
            queues.add(new Queue(queueName));
        }
        return queues;
    }
    @Bean
    public List<Binding> bindings(List<Queue> queues, DirectExchange exchange) {
        // 바인딩들을 생성하고 리스트로 반환
        List<Binding> bindings = new ArrayList<>();
        for (int i = 0; i < QUEUE_NAMES.size(); i++) {
            bindings.add(BindingBuilder.bind(queues.get(i)).to(exchange).with(ROUTING_KEYS.get(i)));
        }
        return bindings;
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        // JSON 형식의 메시지를 직렬화하고 역직렬할 수 있도록 설정
        rabbitTemplate.setMessageConverter(jackson2JsonMessageConverter());
        return rabbitTemplate;
    }

    /**
     * Jackson 라이브러리를 사용하여 메시지를 JSON 형식으로 변환하는 MessageConverter 빈을 생성
     *
     * @return MessageConverter 객체
     */
    @Bean
    public MessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
