package betterdle.api.lol.controller;

import betterdle.api.config.Locale;
import betterdle.api.dto.ChampionPatchDTO;
import betterdle.api.dto.SyncResultDTO;
import betterdle.api.lol.model.Champion;
import betterdle.api.lol.service.ChampionAdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Administration", description = "Endpoints d'administration pour la gestion des champions")
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
    @Operation(summary = "Récupérer les champions incomplets", 
               description = "Retourne la liste des champions ayant au moins un champ incomplet (null ou vide)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Liste des champions incomplets récupérée avec succès"),
        @ApiResponse(responseCode = "404", description = "Langue non supportée")
    })
    public List<Champion> getIncompleteChampions(
            @Parameter(description = "Code de la langue", required = true) @PathVariable String localeStr) {
        validateLocale(localeStr);
        return adminService.getIncompleteChampions();
    }

    /**
     * GET /stats
     * Retourne les statistiques actuelles (total, complets, incomplets, dernière
     * sync).
     */
    @GetMapping("/stats")
    @Operation(summary = "Récupérer les statistiques", 
               description = "Retourne les statistiques actuelles (total, complets, incomplets, dernière synchronisation)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Statistiques récupérées avec succès"),
        @ApiResponse(responseCode = "404", description = "Langue non supportée")
    })
    public SyncResultDTO getStats(
            @Parameter(description = "Code de la langue", required = true) @PathVariable String localeStr) {
        validateLocale(localeStr);
        return adminService.getStats();
    }

    /**
     * POST /sync
     * Déclenche manuellement la synchronisation avec le Wiki LoL.
     * Logique PATCH : ne met à jour que les champs null existants.
     */
    @PostMapping("/sync")
    @Operation(summary = "Synchroniser les données", 
               description = "Déclenche manuellement la synchronisation avec le Wiki LoL. Ne met à jour que les champs null existants")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Synchronisation réussie"),
        @ApiResponse(responseCode = "404", description = "Langue non supportée"),
        @ApiResponse(responseCode = "500", description = "Erreur lors de la synchronisation")
    })
    public SyncResultDTO synchronize(
            @Parameter(description = "Code de la langue", required = true) @PathVariable String localeStr) {
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
    @Operation(summary = "Mettre à jour un champion", 
               description = "Met à jour manuellement un champion (édition partielle). Seuls les champs fournis seront modifiés")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Champion mis à jour avec succès"),
        @ApiResponse(responseCode = "404", description = "Champion ou langue non trouvé(e)")
    })
    public Champion patchChampion(
            @Parameter(description = "Code de la langue", required = true) @PathVariable String localeStr,
            @Parameter(description = "ID du champion", required = true) @PathVariable Integer id,
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
    @Operation(summary = "Rafraîchir un champion", 
               description = "Force le rafraîchissement des données (métadonnées + assets) d'un champion spécifique")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Champion rafraîchi avec succès"),
        @ApiResponse(responseCode = "404", description = "Champion ou langue non trouvé(e)"),
        @ApiResponse(responseCode = "500", description = "Erreur lors du rafraîchissement")
    })
    public Champion refreshChampion(
            @Parameter(description = "Code de la langue", required = true) @PathVariable String localeStr,
            @Parameter(description = "ID du champion", required = true) @PathVariable Integer id) {
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
