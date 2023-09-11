package fr.miage.demorabbitmqspring;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * L'application Spring Boot de l'API Bourse
 */
@SpringBootApplication
public class DemoRabbitMqSpringApplication {

    /**
     * Échangeur pour la publication des cours
     */
    static public final String headersExchangeName = "bourse_headers";

    /**
     * File connectée à l'échangeur de publication des cours
     */
    static public final String headersExchangeQueueName = "bourse_headers_queue";

    /**
     * File pour l'envoie des RPC au serveur Bourse
     */
    static public final String rpcQueueName = "bourse_rpc";

    /**
     * File pour recevoir les réponses des RPC
     */
    static public final String rpcResponseQueueName = "bourse_rpc_response";

    /**
     * Génère un Bean rabbitTemplate avec conversion JSON
     * @param connectionFactory l'usine à connexions
     * @return le bean
     */
    @Bean
    public RabbitTemplate rabbitTemplate(final ConnectionFactory connectionFactory) {
        final var rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(producerJackson2MessageConverter());
        return rabbitTemplate;
    }

    /**
     * Génère un Bean de conversion JSON pour les messages
     * @return le bean
     */
    @Bean
    public Jackson2JsonMessageConverter producerJackson2MessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    /**
     * Génère le bean pour la file RPC
     * @return le bean
     */
    @Bean
    public Queue rpcQueue() { return new Queue(rpcQueueName); }

    /**
     * Génère le bean pour la file de publication des cours
     * @return le bean
     */
    @Bean
    public Queue headersExchangeQueue() { return new Queue(headersExchangeQueueName); }

    /**
     * Génère le bean pour l'échangeur de publication des cours
     * @return le bean
     */
    @Bean
    public HeadersExchange headersExchange() { return new HeadersExchange(headersExchangeName); }

    /**
     * Génère le bean qui lie la file pour la publication des cours à l'échangeur correspondant
     * @param headersExchangeQueue la file
     * @param headersExchange l'échangeur
     * @return le bean
     */
    @Bean
    Binding binding(Queue headersExchangeQueue, HeadersExchange headersExchange) {
        // Note : ici on n'utilise pas de Headers ni de topic et donc on recevra tout
        return new Binding(headersExchangeQueueName, Binding.DestinationType.QUEUE, headersExchangeName, "", null);
    }

    /**
     * Génère le bean pour recevoir les réponses RPC
     * @return le bean
     */
    @Bean
    public Queue rpcResponseQueue() { return new Queue(rpcResponseQueueName); }

    /**
     * Le main
     * @param args arguments pour Spring
     */
    public static void main(String[] args) {
        SpringApplication.run(DemoRabbitMqSpringApplication.class, args);
    }

}
