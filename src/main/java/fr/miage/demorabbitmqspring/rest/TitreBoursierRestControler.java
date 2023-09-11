package fr.miage.demorabbitmqspring.rest;

import fr.miage.demorabbitmqspring.amqp.Sender;
import fr.miage.demorabbitmqspring.models.TitreBoursier;
import fr.miage.demorabbitmqspring.models.TitreBoursierCache;
import fr.miage.demorabbitmqspring.models.OperationType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bourse")
public class TitreBoursierRestControler {

    private final Sender sender;
    private final TitreBoursierCache titreBoursierCache;

    public TitreBoursierRestControler(Sender sender, TitreBoursierCache titreBoursierCache) {
        this.sender = sender;
        this.titreBoursierCache = titreBoursierCache;
    }

    @PostMapping
    public ResponseEntity<String> createTitreBoursier(@RequestBody TitreBoursier titreBoursier) {
        System.out.println("Post de "+titreBoursier);
        sender.sendMessage(titreBoursier, OperationType.CREATE);
        System.out.println("Sent...");
        return new ResponseEntity("{\"status\":\"In progress\"}", HttpStatus.CREATED);
    }

    @PutMapping("/{mnemonic}")
    public ResponseEntity<String> updateTitreBoursier(@PathVariable("mnemonic") String mnemonic, @RequestBody TitreBoursier titreBoursier) {
        System.out.println("Put sur "+mnemonic+" de "+titreBoursier);
        if(!mnemonic.equalsIgnoreCase(titreBoursier.getMnemo())) {
            return new ResponseEntity("{\"status\":\"The mnemonics do not match\"}", HttpStatus.BAD_REQUEST);
        } else {
            sender.sendMessage(titreBoursier, OperationType.UPDATE);
            System.out.println("Sent...");
            return new ResponseEntity("{\"status\":\"In progress\"}", HttpStatus.ACCEPTED);
        }
    }

    @DeleteMapping("/{mnemonic}")
    public ResponseEntity<String> deleteTitreBoursier(@PathVariable("mnemonic") String mnemonic) {
        TitreBoursier titreBoursier = new TitreBoursier(mnemonic, "",0.0f,0.0f);
        sender.sendMessage(titreBoursier, OperationType.DELETE);
        System.out.println("Sent...");
        return new ResponseEntity("{\"status\":\"In progress\"}", HttpStatus.ACCEPTED);
    }

    @GetMapping("/{mnemonic}")
    public ResponseEntity<TitreBoursier> getTitreBoursier(@PathVariable("mnemonic") String mnemonic) {
        TitreBoursier titreBoursier = titreBoursierCache.getFromCache(mnemonic);
        if (titreBoursier == null) {
            return new ResponseEntity(new TitreBoursier(), HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity(titreBoursier, HttpStatus.OK);
        }
    }

}

