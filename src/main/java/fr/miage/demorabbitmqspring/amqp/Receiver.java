package fr.miage.demorabbitmqspring.amqp;

import fr.miage.demorabbitmqspring.DemoRabbitMqSpringApplication;
import fr.miage.demorabbitmqspring.models.TitreBoursierCache;
import fr.miage.demorabbitmqspring.models.TitreBoursier;
import fr.miage.demorabbitmqspring.utils.ResponseMessage;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Service contenant les listeners
 */
@Service
public class Receiver {

    public final TitreBoursierCache titreBoursierCache;

    public Receiver(TitreBoursierCache titreBoursierCache) {
        this.titreBoursierCache = titreBoursierCache;
    }

    /**
     * Listener pour les publications
     * @param titreBoursier : le titre boursier reçu
     * @param headersMap : les entêtes
     */
    @RabbitListener(queues = DemoRabbitMqSpringApplication.headersExchangeQueueName)
    public void handleWithHeadersExchange(final TitreBoursier titreBoursier, @Headers Map headersMap) {
        System.out.println("Received message and deserialized to 'TitreBoursier': " + titreBoursier.toString());
        System.out.println("Headers: "+headersMap);
        // on vérifie s'il y a le type d'opération
        if (headersMap.containsKey("OP")) {
            Object op = headersMap.get("OP");
            if (op instanceof String && ((String)op).equalsIgnoreCase("DELETE")) {
                // si c'est un DELETE on l'enlève du cache
                titreBoursierCache.deleteFromCache(titreBoursier.getMnemo());
            } else {
                // sinon, on fait comme si c'était un update
                titreBoursierCache.updateCache(titreBoursier);
            }
        }
    }

    /**
     * Listener du RPC
     * @param response : la réponse à la demande RPC
     */
    @RabbitListener(queues = DemoRabbitMqSpringApplication.rpcResponseQueueName)
    public void handleRPCResponse(final ResponseMessage response) {
        System.out.println("Response: "+response);
    }
}