package betterdle.api.lol.service;

import betterdle.api.config.Locale;
import betterdle.api.dto.ChampionPatchDTO;
import betterdle.api.dto.SyncResultDTO;
import betterdle.api.lol.model.Champion;
import betterdle.api.lol.repository.ChampionRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.Instant;
import java.util.List;

/**
 * Service d'administration pour la gestion des champions.
 * Gère la synchronisation, la détection des données incomplètes et les mises à
 * jour manuelles.
 */
@Service
public class ChampionAdminService {

    private final ChampionRepository championRepository;
    private final LolDataInitializer lolDataInitializer;
    private final ChampionSyncService championSyncService;
    private String lastSyncTime = null;
    private String currentVersion = null;

    @Autowired
    public ChampionAdminService(ChampionRepository championRepository, LolDataInitializer lolDataInitializer,
            ChampionSyncService championSyncService) {
        this.championRepository = championRepository;
        this.lolDataInitializer = lolDataInitializer;
        this.championSyncService = championSyncService;
    }

    /**
     * Retourne la liste des champions ayant au moins un champ incomplet.
     */
    public List<Champion> getIncompleteChampions() {
        return championRepository.findIncompleteChampions();
    }

    /**
     * Déclenche la synchronisation avec le Wiki LoL.
     * Met à jour uniquement les champs null (logique PATCH).
     */
    @Transactional
    public SyncResultDTO synchronize(Locale locale) throws IOException {
        // Déclencher la synchronisation via LolDataInitializer
        lolDataInitializer.init(locale, false);

        // Mettre à jour les métadonnées
        lastSyncTime = Instant.now().toString();
        currentVersion = lolDataInitializer.fetchLatestVersion();

        // Calculer les statistiques
        long total = championRepository.count();
        long incomplete = championRepository.findIncompleteChampions().size();
        long complete = total - incomplete;

        return new SyncResultDTO(
                (int) total,
                (int) complete,
                (int) incomplete,
                lastSyncTime,
                currentVersion);
    }

    /**
     * Met à jour partiellement un champion (PATCH).
     * Seuls les champs non-null du DTO seront appliqués.
     */
    @Transactional
    public Champion patchChampion(Integer id, ChampionPatchDTO dto) {
        Champion champion = championRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Champion introuvable avec l'ID : " + id));

        // Application des modifications uniquement si le champ DTO n'est pas null
        if (dto.getGender() != null)
            champion.setGender(dto.getGender());
        if (dto.getChampionClass() != null)
            champion.setChampionClass(dto.getChampionClass());
        if (dto.getPositions() != null)
            champion.setPositions(dto.getPositions());
        if (dto.getSpecies() != null)
            champion.setSpecies(dto.getSpecies());
        if (dto.getRegions() != null)
            champion.setRegions(dto.getRegions());
        if (dto.getResource() != null)
            champion.setResource(dto.getResource());
        if (dto.getAttackRangeType() != null)
            champion.setAttackRangeType(dto.getAttackRangeType());
        if (dto.getReleaseDate() != null)
            champion.setReleaseDate(dto.getReleaseDate());

        return championRepository.save(champion);
    }

    /**
     * Retourne les statistiques actuelles (sans déclencher de sync).
     */
    public SyncResultDTO getStats() {
        long total = championRepository.count();
        long incomplete = championRepository.findIncompleteChampions().size();
        long complete = total - incomplete;

        return new SyncResultDTO(
                (int) total,
                (int) complete,
                (int) incomplete,
                lastSyncTime != null ? lastSyncTime : "Jamais synchronisé",
                currentVersion != null ? currentVersion : "Inconnue");
    }

    /**
     * Force le rafraîchissement des métadonnées et des assets d'un champion
     * spécifique.
     */
    @Transactional
    public Champion refreshChampion(Integer id, Locale locale) throws IOException {
        Champion champion = championRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Champion introuvable avec l'ID : " + id));

        String version = lolDataInitializer.fetchLatestVersion();

        // On utilise le nom comme ID DDragon approximatif pour l'instant
        String dDragonId = champion.getName();
        if ("Wukong".equalsIgnoreCase(dDragonId))
            dDragonId = "MonkeyKing";

        return championSyncService.syncAssets(champion, dDragonId, version, locale);
    }
}
