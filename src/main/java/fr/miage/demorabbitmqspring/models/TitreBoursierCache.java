package fr.miage.demorabbitmqspring.models;

import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashMap;
/**
 * Service gérant le cache local des titres
 */
@Service
public class TitreBoursierCache {
    private final HashMap<String, TitreBoursier> cache = new HashMap<>();

    /**
     * Permet de mettre à jour un titre dans le cache (calcule la variation)
     * @param titreBoursier : le titre à mettre à jour
     */
    public void updateCache(TitreBoursier titreBoursier) {
        cache.put(titreBoursier.getMnemo(), titreBoursier);
    }

    /**
     * Retourne une entrée du cache
     * @param mnemonic : le mnémonique du titre cherché
     * @return : le titre trouvé ou null
     */
    public TitreBoursier getFromCache(String mnemonic) {
        return cache.get(mnemonic);
    }

    /**
     * Retourne la liste des titres
     *
     * @return : la liste des titres (peut être vide)
     */
    public Collection<TitreBoursier> getListeTitres() {
        return cache.values();
    }


    /**
     * Permet la suppression d'un titre
     * @param mnemonic : le mnémonique du titre à effacer
     */
    public void deleteFromCache(String mnemonic) {
        cache.remove(mnemonic);
    }
}
