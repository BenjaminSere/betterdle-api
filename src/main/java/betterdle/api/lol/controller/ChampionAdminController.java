package betterdle.api.lol.controller;

import betterdle.api.config.Locale;
import betterdle.api.dto.ChampionPatchDTO;
import betterdle.api.dto.SyncResultDTO;
import betterdle.api.lol.model.Champion;
import betterdle.api.lol.service.ChampionAdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.List;

/**
 * Contrôleur d'administration pour la gestion des champions.
 * Endpoints sécurisés pour synchroniser, valider et corriger les données.
 */
@RestController
@RequestMapping("/api/v1/admin/lol/{localeStr}/champions")
public class ChampionAdminController {

    private final ChampionAdminService adminService;

    @Autowired
    public ChampionAdminController(ChampionAdminService adminService) {
        this.adminService = adminService;
    }

    /**
     * GET /incomplete
     * Retourne la liste des champions ayant au moins un champ incomplet (null ou
     * vide).
     */
    @GetMapping("/incomplete")
    public List<Champion> getIncompleteChampions(@PathVariable String localeStr) {
        validateLocale(localeStr);
        return adminService.getIncompleteChampions();
    }

    /**
     * GET /stats
     * Retourne les statistiques actuelles (total, complets, incomplets, dernière
     * sync).
     */
    @GetMapping("/stats")
    public SyncResultDTO getStats(@PathVariable String localeStr) {
        validateLocale(localeStr);
        return adminService.getStats();
    }

    /**
     * POST /sync
     * Déclenche manuellement la synchronisation avec le Wiki LoL.
     * Logique PATCH : ne met à jour que les champs null existants.
     */
    @PostMapping("/sync")
    public SyncResultDTO synchronize(@PathVariable String localeStr) {
        Locale locale = validateLocale(localeStr);
        try {
            return adminService.synchronize(locale);
        } catch (IOException e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Erreur lors de la synchronisation : " + e.getMessage());
        }
    }

    /**
     * PATCH /{id}
     * Met à jour manuellement un champion (édition partielle).
     * Seuls les champs fournis dans le body seront modifiés.
     */
    @PatchMapping("/{id}")
    public Champion patchChampion(
            @PathVariable String localeStr,
            @PathVariable Integer id,
            @RequestBody ChampionPatchDTO dto) {
        validateLocale(localeStr);
        try {
            return adminService.patchChampion(id, dto);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    /**
     * POST /{id}/refresh
     * Force le rafraîchissement des données (métadonnées + assets) d'un champion.
     */
    @PostMapping("/{id}/refresh")
    public Champion refreshChampion(
            @PathVariable String localeStr,
            @PathVariable Integer id) {
        Locale locale = validateLocale(localeStr);
        try {
            return adminService.refreshChampion(id, locale);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (IOException e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Erreur lors du rafraîchissement : " + e.getMessage());
        }
    }

    // --- Helpers ---

    private Locale validateLocale(String localeStr) {
        Locale locale = Locale.fromId(localeStr);
        if (locale == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Langue non supportée");
        }
        return locale;
    }
}
