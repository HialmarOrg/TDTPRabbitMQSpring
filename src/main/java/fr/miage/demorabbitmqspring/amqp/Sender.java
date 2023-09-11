package fr.miage.demorabbitmqspring.amqp;

import fr.miage.demorabbitmqspring.DemoRabbitMqSpringApplication;
import fr.miage.demorabbitmqspring.models.TitreBoursier;
import fr.miage.demorabbitmqspring.models.OperationType;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Service pour les émissions AMQP
 */
@Component
public class Sender {

    private final AmqpTemplate template;

    public Sender(AmqpTemplate template) {
        this.template = template;
    }

    public void sendMessage(TitreBoursier titreBoursier, OperationType operationType) {
        // On envoie le message RPC avec le titre donné
        template.convertAndSend("", DemoRabbitMqSpringApplication.rpcQueueName, titreBoursier, m -> {
            // on ajoute un correlation ID aléatoire,
            // le type d'Opération
            // et la file pour la réponse
            final String corrId = UUID.randomUUID().toString();
            m.getMessageProperties().setHeader(titreBoursier.getMnemo(), "TRUE");
            m.getMessageProperties().setHeader("OP", operationType.name());
            m.getMessageProperties().setCorrelationId(corrId);
            m.getMessageProperties().setReplyTo(DemoRabbitMqSpringApplication.rpcResponseQueueName);
            System.out.println("Message : "+m);
            return m;
        });
    }

}