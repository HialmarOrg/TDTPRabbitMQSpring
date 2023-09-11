package fr.miage.demorabbitmqspring;

import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service
public class Receiver {

    public final TitreBoursierCache titreBoursierCache;

    public Receiver(TitreBoursierCache titreBoursierCache) {
        this.titreBoursierCache = titreBoursierCache;
    }


    @RabbitListener(queues = DemoRabbitMqSpringApplication.headersExchangeQueueName)
    public void handleWithHeadersExchange(final TitreBoursier titreBoursier, @Headers Map headersMap) {
        System.out.println("Received message on auto.headers and deserialized to 'TitreBoursier': " + titreBoursier.toString());
        System.out.println("Header thing2 = "+headersMap);
        if (headersMap.containsKey("OP")) {
            Object op = headersMap.get("OP");
            if (op instanceof String && ((String)op).equalsIgnoreCase("DELETE")) {
                titreBoursierCache.deleteFromCache(titreBoursier.getMnemo());
            } else {
                titreBoursierCache.updateCache(titreBoursier);
            }
        }
    }

    @RabbitListener(queues = DemoRabbitMqSpringApplication.rpcResponseQueueName)
    public void handleRPCResponse(final ResponseMessage response) {
        System.out.println("Response "+response);
    }
}