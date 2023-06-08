package fr.miage.demorabbitmqspring;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/CustomMessage")
public class TitreBoursierRestControler {

    private final Sender sender;

    public TitreBoursierRestControler(Sender sender) {
        this.sender = sender;
    }

    @PostMapping
    public TitreBoursier createTitreBoursier(@RequestBody TitreBoursier titreBoursier) {
        System.out.println("Post de "+titreBoursier);
        sender.sendMessage(titreBoursier);
        System.out.println("Sent...");
        return titreBoursier;
    }
}

