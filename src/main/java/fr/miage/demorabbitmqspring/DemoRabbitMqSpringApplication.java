package fr.miage.demorabbitmqspring;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class DemoRabbitMqSpringApplication {

    static public final String headersExchangeName = "bourse_headers";

    static public final String headersExchangeQueueName = "bourse_headers_queue";

    static public final String rpcQueueName = "bourse_rpc";
    static public final String rpcResponseQueueName = "bourse_rpc_response";

    @Bean
    public RabbitTemplate rabbitTemplate(final ConnectionFactory connectionFactory) {
        final var rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(producerJackson2MessageConverter());
        return rabbitTemplate;
    }

    @Bean
    public Jackson2JsonMessageConverter producerJackson2MessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public Queue rpcQueue() { return new Queue(rpcQueueName); }

    @Bean
    public Queue headersExchangeQueue() { return new Queue(headersExchangeQueueName); }

    @Bean
    public HeadersExchange headersExchange() { return new HeadersExchange(headersExchangeName); }


    @Bean
    Binding binding(Queue headersExchangeQueue, HeadersExchange headersExchange) {
        return new Binding(headersExchangeQueueName, Binding.DestinationType.QUEUE, headersExchangeName, "", null);
    }


        @Bean
    public Queue rpcResponseQueue() { return new Queue(rpcResponseQueueName); }


    public static void main(String[] args) {
        SpringApplication.run(DemoRabbitMqSpringApplication.class, args);
    }

}
