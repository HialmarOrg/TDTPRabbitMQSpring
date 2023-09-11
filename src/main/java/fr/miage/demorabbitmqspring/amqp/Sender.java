package fr.miage.demorabbitmqspring.amqp;

import fr.miage.demorabbitmqspring.DemoRabbitMqSpringApplication;
import fr.miage.demorabbitmqspring.models.TitreBoursier;
import fr.miage.demorabbitmqspring.models.OperationType;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class Sender implements CommandLineRunner {

    private final AmqpTemplate template;

    public Sender(AmqpTemplate template) {
        this.template = template;
    }

    @Override
    public void run(String... args) throws Exception {
        TitreBoursier google = new TitreBoursier("GOOG", "Google Inc.", 391.03f, 0.0f);
        TitreBoursier microsoft = new TitreBoursier("MSFT", "Microsoft Corp.", 25.79f, 0.0f);

        float variation = 0.0f;
        google.setVariation(variation);
        sendMessage(google, OperationType.CREATE);
        System.out.println("Publication de "+google);
        variation = (float)Math.random() * 20.0f - 10.0f;
        microsoft.setVariation(variation);
        sendMessage(microsoft, OperationType.CREATE);
        System.out.println("Publication de "+microsoft);
        Thread.sleep(1000);

        /*
        for(int i=0; i<10; i++) {
            variation = (float)Math.random() * 20.0f - 10.0f;
            google.setVariation(variation);
            sendMessage(google, OperationType.UPDATE);
            System.out.println("Publication de "+google);
            variation = (float)Math.random() * 20.0f - 10.0f;
            microsoft.setVariation(variation);
            sendMessage(microsoft, OperationType.UPDATE);
            System.out.println("Publication de "+microsoft);
            Thread.sleep(1000);
        }*/

    }

    public void sendMessage(TitreBoursier titreBoursier, OperationType operationType) {
        template.convertAndSend("", DemoRabbitMqSpringApplication.rpcQueueName, titreBoursier, m -> {
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