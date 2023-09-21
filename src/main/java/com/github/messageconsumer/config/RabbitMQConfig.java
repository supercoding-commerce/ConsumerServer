package com.github.messageconsumer.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.Arrays;
import java.util.List;

@Configuration
public class RabbitMQConfig {
    public static final String EXCHANGE_NAME = "exchange";
    // 여러 개의 큐와 라우팅 키를 리스트로 관리
    public static final List<String> QUEUE_NAMES = Arrays.asList("postCart", "putCart", "postOrder", "putOrder", "postPayment", "putPayment", "postRoom", "postChat1", "postChat2", "postChat3", "postChat4", "postChat5");
    public static final List<String> ROUTING_KEYS = Arrays.asList("postCart", "putCart", "postOrder", "putOrder", "postPayment", "putPayment", "postRoom", "postChat1", "postChat2", "postChat3", "postChat4", "postChat5");


    @Value("${spring.rabbitmq.host}")
    private String rmqHost;

    @Value("${spring.rabbitmq.username}")
    private String rmqUsername;

    @Value("${spring.rabbitmq.password}")
    private String rmqPassword;

    @Bean
    public CachingConnectionFactory cachingConnectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory(rmqHost);
        connectionFactory.setUsername(rmqUsername);
        connectionFactory.setPassword(rmqPassword);
        // 다양한 설정 (캐싱 옵션, 포트, 가상 호스트 등)을 설정할 수 있습니다.
        return connectionFactory;
    }

    @Bean
    public DirectExchange exchange() {
        return new DirectExchange(EXCHANGE_NAME);
    }

    @Bean
    Jackson2JsonMessageConverter messageConverter(ObjectMapper mapper){
        var converter = new Jackson2JsonMessageConverter(mapper);
        converter.setCreateMessageIds(true); //create a unique message id for every message
        return converter;
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory factory, ObjectMapper objectMapper){
        RabbitTemplate template = new RabbitTemplate();
        template.setConnectionFactory(factory);
        template.setMessageConverter(messageConverter(objectMapper));
        return template;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            ObjectMapper objectMapper
    ) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter(objectMapper));
        // prefetch 설정 (한 번에 가져올 메시지 개수)
        factory.setPrefetchCount(1); // 예: 10개의 메시지를 미리 가져옴

        // channel 개수 설정 (동시에 처리할 메시지 수)
        factory.setConcurrentConsumers(3); // 예: 5개의 채널을 사용하여 메시지 처리
        factory.setMaxConcurrentConsumers(5);
        return factory;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory deadListenerContainer(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory container = new SimpleRabbitListenerContainerFactory();
        container.setConnectionFactory(connectionFactory);
        return container;
    }

}
