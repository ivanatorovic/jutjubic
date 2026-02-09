package com.example.jutjubic.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.support.converter.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;


@Configuration
public class RabbitConfig {

    public static final String TRANSCODE_EXCHANGE = "transcode.exchange";
    public static final String TRANSCODE_QUEUE = "transcode.queue";
    public static final String TRANSCODE_ROUTING_KEY = "transcode.request";
    public static final String UPLOAD_EXCHANGE = "upload.exchange";
    public static final String UPLOAD_QUEUE_JSON = "upload.queue.json";
    public static final String UPLOAD_QUEUE_PB = "upload.queue.pb";
    public static final String UPLOAD_ROUTING_JSON = "upload.json";
    public static final String UPLOAD_ROUTING_PB = "upload.pb";


    @Bean
    public DirectExchange transcodeExchange() {
        return new DirectExchange(TRANSCODE_EXCHANGE);
    }

    @Bean
    public Queue transcodeQueue() {
        return QueueBuilder.durable(TRANSCODE_QUEUE).build();
    }

    @Bean
    public Binding transcodeBinding(Queue transcodeQueue, DirectExchange transcodeExchange) {
        return BindingBuilder.bind(transcodeQueue)
                .to(transcodeExchange)
                .with(TRANSCODE_ROUTING_KEY);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory cf,
            MessageConverter jsonMessageConverter
    ) {
        SimpleRabbitListenerContainerFactory f = new SimpleRabbitListenerContainerFactory();
        f.setConnectionFactory(cf);
        f.setMessageConverter(jsonMessageConverter);

        f.setAcknowledgeMode(AcknowledgeMode.MANUAL);

        f.setPrefetchCount(1);

        f.setConcurrentConsumers(2);
        f.setMaxConcurrentConsumers(5);

        return f;
    }

    @Bean
    public DirectExchange uploadExchange() {
        return new DirectExchange(UPLOAD_EXCHANGE);
    }

    @Bean
    public Queue uploadQueueJson() {
        return QueueBuilder.durable(UPLOAD_QUEUE_JSON).build();
    }

    @Bean
    public Queue uploadQueuePb() {
        return QueueBuilder.durable(UPLOAD_QUEUE_PB).build();
    }

    @Bean
    public Binding uploadJsonBinding(Queue uploadQueueJson, DirectExchange uploadExchange) {
        return BindingBuilder.bind(uploadQueueJson)
                .to(uploadExchange)
                .with(UPLOAD_ROUTING_JSON);
    }

    @Bean
    public Binding uploadPbBinding(Queue uploadQueuePb, DirectExchange uploadExchange) {
        return BindingBuilder.bind(uploadQueuePb)
                .to(uploadExchange)
                .with(UPLOAD_ROUTING_PB);
    }


}
