package fr.miage.demorabbitmqspring.rest;

import fr.miage.demorabbitmqspring.amqp.Sender;
import fr.miage.demorabbitmqspring.models.TitreBoursier;
import fr.miage.demorabbitmqspring.models.TitreBoursierCache;
import fr.miage.demorabbitmqspring.models.OperationType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

/**
 * Contrôleur REST pour les titres boursiers
 */
@RestController
@RequestMapping("/api/bourse")
public class TitreBoursierRestControler {

    private final Sender sender;
    private final TitreBoursierCache titreBoursierCache;

    /**
     * Constructeur pour l'injection de dépendance
     * @param sender : le service pour envoyer des messages AMQP
     * @param titreBoursierCache : le service qui gère le cache
     */
    public TitreBoursierRestControler(Sender sender, TitreBoursierCache titreBoursierCache) {
        this.sender = sender;
        this.titreBoursierCache = titreBoursierCache;
    }

    /**
     * Création d'un titre boursier
     * POST sur http://localhost:8080/api/bourse
     * {"mnemo":"GOG","nomComplet":"Good Old Games","cours":95.56439,"variation":0}
     * @param titreBoursier : titre à créer
     * @return l'entité réponse
     */
    @PostMapping
    public ResponseEntity<String> createTitreBoursier(@RequestBody TitreBoursier titreBoursier) {
        System.out.println("Post de "+titreBoursier);
        // on envoie la demande au serveur
        sender.sendMessage(titreBoursier, OperationType.CREATE);
        System.out.println("Sent...");
        // on précise que la création est en cours
        return new ResponseEntity<>("{\"status\":\"In progress\"}", HttpStatus.CREATED);
    }

    /**
     * Mise à jour d'un titre
     * PUT sur http://localhost:8080/api/bourse/GOG
     * {"mnemo":"GOG","nomComplet":"Good Old Games","cours":195.56439,"variation":0}
     * @param mnemonic : mnémonique du titre
     * @param titreBoursier : le titre
     * @return l'entité réponse
     */
    @PutMapping("/{mnemonic}")
    public ResponseEntity<String> updateTitreBoursier(@PathVariable("mnemonic") String mnemonic, @RequestBody TitreBoursier titreBoursier) {
        System.out.println("Put sur "+mnemonic+" de "+titreBoursier);
        // On vérifie si les mnémoniques de la route
        // et du titre sont identiques
        if(!mnemonic.equalsIgnoreCase(titreBoursier.getMnemo())) {
            // erreur mnémos différents
            return new ResponseEntity<>("{\"status\":\"The mnemonics do not match\"}", HttpStatus.BAD_REQUEST);
        } else {
            // on envoie la demande au serveur
            sender.sendMessage(titreBoursier, OperationType.UPDATE);
            System.out.println("Sent...");
            // on précise que la modification est en cours
            return new ResponseEntity<>("{\"status\":\"In progress\"}", HttpStatus.ACCEPTED);
        }
    }

    /**
     * Suppression d'un titre
     * DELETE sur http://localhost:8080/api/bourse/GOG
     * @param mnemonic : le mnémonique du titre
     * @return l'entité réponse
     */
    @DeleteMapping("/{mnemonic}")
    public ResponseEntity<String> deleteTitreBoursier(@PathVariable("mnemonic") String mnemonic) {
        TitreBoursier titreBoursier = new TitreBoursier(mnemonic, "",0.0f,0.0f);
        // on envoie la demande au serveur
        sender.sendMessage(titreBoursier, OperationType.DELETE);
        System.out.println("Sent...");
        // on précise que c'est en cours
        return new ResponseEntity<>("{\"status\":\"In progress\"}", HttpStatus.ACCEPTED);
    }

    /**
     * Récupération d'un titre
     * GET sur http://localhost:8080/api/bourse/GOG
     * @param mnemonic : le mnémonique du titre
     * @return l'entité réponse
     */
    @GetMapping("/{mnemonic}")
    public ResponseEntity<TitreBoursier> getTitreBoursier(@PathVariable("mnemonic") String mnemonic) {
        // On tente de récupérer le titre dans le cache
        TitreBoursier titreBoursier = titreBoursierCache.getFromCache(mnemonic);
        if (titreBoursier == null) {
            // il n'y est pas
            return new ResponseEntity<>(new TitreBoursier(), HttpStatus.NOT_FOUND);
        } else {
            // il y est, on le retourne au client
            return new ResponseEntity<>(titreBoursier, HttpStatus.OK);
        }
    }

    /**
     * Récupération de la liste des titres
     * GET sur http://localhost:8080/api/bourse
     * @return l'entité réponse
     */
    @GetMapping
    public ResponseEntity<Collection<TitreBoursier>> getListeTitresBoursiers() {
        // On récupère la liste des titres dans le cache
        Collection<TitreBoursier> liste = titreBoursierCache.getListeTitres();
        // on la retourne au client
        return new ResponseEntity<>(liste, HttpStatus.OK);
    }
}

