package fr.miage.demorabbitmqspring;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

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

        for(int i=0; i<100; i++) {
            float variation = (float)Math.random() * 20.0f - 10.0f;
            google.setVariation(variation);
            sendMessage(google);
            System.out.println("Publication de "+google);
            variation = (float)Math.random() * 20.0f - 10.0f;
            microsoft.setVariation(variation);
            sendMessage(microsoft);
            System.out.println("Publication de "+microsoft);
            Thread.sleep(1000);
        }

    }

    public void sendMessage(TitreBoursier titreBoursier) {
        template.convertAndSend(DemoRabbitMqSpringApplication.topicExchangeName, "", titreBoursier, m -> {
            m.getMessageProperties().setHeader(titreBoursier.getMnemo(), "TRUE");
            System.out.println("Message : "+m);
            return m;
        });
    }

}