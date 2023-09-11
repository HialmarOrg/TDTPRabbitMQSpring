package fr.miage.demorabbitmqspring.models;

import fr.miage.demorabbitmqspring.models.TitreBoursier;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class TitreBoursierCache {
    private final HashMap<String, TitreBoursier> cache = new HashMap<>();

    public void updateCache(TitreBoursier titreBoursier) {
        cache.put(titreBoursier.getMnemo(), titreBoursier);
    }

    public TitreBoursier getFromCache(String mnemonic) {
        return cache.get(mnemonic);
    }

    public void deleteFromCache(String mnemonic) {
        cache.remove(mnemonic);
    }
}
