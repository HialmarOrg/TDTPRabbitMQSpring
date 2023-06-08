package fr.miage.demorabbitmqspring;

import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.Map;

@Service
public class Receiver {

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "bourse_headers", autoDelete = "true",
                    arguments = @Argument(name = "x-message-ttl", value = "10000",
                            type = "java.lang.Integer")),
            exchange = @Exchange(value = "bourse_headers", type = ExchangeTypes.HEADERS, durable="false"),
            arguments = {
                    @Argument(name = "x-match", value = "any"),
                    @Argument(name = "GOOG", value = "TRUE"),
                    @Argument(name = "MSFT")
            })
    )
    public void handleWithHeadersExchange(final TitreBoursier customMessage, @Headers Map headersMap) {
        System.out.println("Received message on auto.headers and deserialized to 'CustomMessage': " + customMessage.toString());
        System.out.println("Header thing2 = "+headersMap);
    }


}