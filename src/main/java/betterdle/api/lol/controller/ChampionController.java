package betterdle.api.lol.controller;

import betterdle.api.config.Game;
import betterdle.api.config.Locale;
import betterdle.api.lol.model.Champion;
import betterdle.api.lol.model.ChampionSpell;
import betterdle.api.lol.model.ChampionSkin;
import betterdle.api.lol.repository.ChampionRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import betterdle.api.exceptions.ResourceNotFoundException;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.FileInputStream;
import java.io.IOException;

import org.springframework.data.domain.PageRequest;
import io.swagger.v3.oas.annotations.Hidden;

@RestController
@RequestMapping("/api/v1/{gameStr}/{localeStr}/champions")
@Tag(name = "Champions", description = "Endpoints pour gérer les champions League of Legends")
public class ChampionController {

        @Autowired
        private ChampionRepository championRepository;

        @GetMapping
        @Operation(summary = "Récupérer tous les champions", description = "Retourne une liste paginée de tous les champions")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Liste des champions récupérée avec succès"),
                        @ApiResponse(responseCode = "404", description = "Jeu ou langue non supporté(e)")
        })
        public Page<Champion> findAll(
                        @Parameter(description = "Identifiant du jeu (ex: lol)", required = true) @PathVariable String gameStr,
                        @Parameter(description = "Code de la langue (ex: fr_FR, en_US)", required = true) @PathVariable String localeStr,
                        @Parameter(description = "Numéro de la page (commence à 0)") @RequestParam(required = false) Integer page,
                        @Parameter(description = "Nombre d'éléments par page") @RequestParam(required = false) Integer size) {
                validateParams(gameStr, localeStr);

                if (page == null || size == null) {
                        return championRepository.findAll(org.springframework.data.domain.Pageable.unpaged());
                }

                return championRepository.findAll(PageRequest.of(page, size));
        }

        @GetMapping("/{name}")
        @Operation(summary = "Récupérer un champion par nom", description = "Retourne les détails d'un champion spécifique")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Champion trouvé"),
                        @ApiResponse(responseCode = "404", description = "Champion, jeu ou langue non trouvé(e)")
        })
        public Champion findByName(
                        @Parameter(description = "Identifiant du jeu", required = true) @PathVariable String gameStr,
                        @Parameter(description = "Code de la langue", required = true) @PathVariable String localeStr,
                        @Parameter(description = "Nom du champion", required = true, example = "Ahri") @PathVariable String name) {
                validateParams(gameStr, localeStr);
                return getChampionOr404(name);
        }

        @Hidden
        @GetMapping(value = "/{name}/images/icon", produces = "image/webp")
        @Operation(summary = "Récupérer l'icône du champion", description = "Retourne l'image de l'icône du champion au format WebP")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Image récupérée avec succès", content = @Content(mediaType = "image/webp", schema = @Schema(type = "string", format = "binary"))),
                        @ApiResponse(responseCode = "404", description = "Champion ou image non trouvé(e)")
        })
        public ResponseEntity<Resource> getIcon(
                        @Parameter(description = "Identifiant du jeu", required = true) @PathVariable String gameStr,
                        @Parameter(description = "Code de la langue", required = true) @PathVariable String localeStr,
                        @Parameter(description = "Nom du champion", required = true) @PathVariable String name) {
                validateParams(gameStr, localeStr);
                Champion c = getChampionOr404(name);
                return serveImage(c.getIconURL());
        }

        @Hidden
        @GetMapping(value = "/{name}/images/passive", produces = "image/webp")
        @Operation(summary = "Récupérer l'icône de la compétence passive", description = "Retourne l'image de la compétence passive du champion au format WebP")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Image récupérée avec succès", content = @Content(mediaType = "image/webp", schema = @Schema(type = "string", format = "binary"))),
                        @ApiResponse(responseCode = "404", description = "Champion ou image non trouvé(e)")
        })
        public ResponseEntity<Resource> getPassive(
                        @Parameter(description = "Identifiant du jeu", required = true) @PathVariable String gameStr,
                        @Parameter(description = "Code de la langue", required = true) @PathVariable String localeStr,
                        @Parameter(description = "Nom du champion", required = true) @PathVariable String name) {
                validateParams(gameStr, localeStr);
                Champion c = getChampionOr404(name);
                return serveImage(c.getPassiveIconURL());
        }

        @Hidden
        @GetMapping(value = "/{name}/images/spells/{spellKey}", produces = "image/webp")
        @Operation(summary = "Récupérer l'icône d'une compétence", description = "Retourne l'image d'une compétence spécifique du champion (Q, W, E, R)")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Image récupérée avec succès", content = @Content(mediaType = "image/webp", schema = @Schema(type = "string", format = "binary"))),
                        @ApiResponse(responseCode = "404", description = "Champion, compétence ou image non trouvé(e)")
        })
        public ResponseEntity<Resource> getSpell(
                        @Parameter(description = "Identifiant du jeu", required = true) @PathVariable String gameStr,
                        @Parameter(description = "Code de la langue", required = true) @PathVariable String localeStr,
                        @Parameter(description = "Nom du champion", required = true) @PathVariable String name,
                        @Parameter(description = "Touche de la compétence", required = true, example = "Q") @PathVariable String spellKey) {
                validateParams(gameStr, localeStr);
                Champion c = getChampionOr404(name);
                ChampionSpell spell = c.getSpells().stream()
                                .filter(s -> s.getImageUrl().contains("/" + spellKey.toUpperCase() + ".webp"))
                                .findFirst()
                                .orElseThrow(() -> new ResourceNotFoundException("Sort non trouvé"));
                return serveImage(spell.getImageUrl());
        }

        @GetMapping(value = "/{name}/images/loading", produces = "image/webp")
        @Operation(summary = "Récupérer l'image de chargement d'un skin", description = "Retourne l'image de chargement de jeu pour un skin spécifique")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Image récupérée avec succès", content = @Content(mediaType = "image/webp", schema = @Schema(type = "string", format = "binary"))),
                        @ApiResponse(responseCode = "404", description = "Champion, skin ou image non trouvé(e)")
        })
        public ResponseEntity<Resource> getLoading(
                        @Parameter(description = "Identifiant du jeu", required = true) @PathVariable String gameStr,
                        @Parameter(description = "Code de la langue", required = true) @PathVariable String localeStr,
                        @Parameter(description = "Nom du champion", required = true) @PathVariable String name,
                        @Parameter(description = "Numéro du skin", example = "0") @RequestParam(defaultValue = "0") int skinNum) {
                validateParams(gameStr, localeStr);
                Champion c = getChampionOr404(name);
                ChampionSkin skin = c.getSkins().stream()
                                .filter(s -> s.getNum() == skinNum)
                                .findFirst()
                                .orElseThrow(() -> new ResourceNotFoundException("Skin non trouvé"));
                return serveImage(skin.getLoadingUrl());
        }

        @Hidden
        @GetMapping(value = "/{name}/images/splash", produces = "image/webp")
        @Operation(summary = "Récupérer l'image splash d'un skin", description = "Retourne l'image splash (grand format) pour un skin spécifique")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Image récupérée avec succès", content = @Content(mediaType = "image/webp", schema = @Schema(type = "string", format = "binary"))),
                        @ApiResponse(responseCode = "404", description = "Champion, skin ou image non trouvé(e)")
        })
        public ResponseEntity<Resource> getSplash(
                        @Parameter(description = "Identifiant du jeu", required = true) @PathVariable String gameStr,
                        @Parameter(description = "Code de la langue", required = true) @PathVariable String localeStr,
                        @Parameter(description = "Nom du champion", required = true) @PathVariable String name,
                        @Parameter(description = "Numéro du skin", example = "0") @RequestParam(defaultValue = "0") int skinNum) {
                validateParams(gameStr, localeStr);
                Champion c = getChampionOr404(name);
                ChampionSkin skin = c.getSkins().stream()
                                .filter(s -> s.getNum() == skinNum)
                                .findFirst()
                                .orElseThrow(() -> new ResourceNotFoundException("Skin non trouvé"));
                return serveImage(skin.getSplashUrl());
        }

        // --- Helpers ---

        private void validateParams(String gameStr, String localeStr) {
                Game game = Game.fromId(gameStr);
                Locale locale = Locale.fromId(localeStr);

                if (game == null)
                        throw new ResourceNotFoundException("Jeu non supporté");
                if (locale == null)
                        throw new ResourceNotFoundException("Langue non supportée");

                // Pour l'instant on ne gère que LoL dans ce repository
                if (game != Game.LOL)
                        throw new ResourceNotFoundException("Ressource indisponible pour ce jeu");
        }

        private Champion getChampionOr404(String name) {
                return championRepository.findByNameIgnoreCase(name)
                                .orElseThrow(() -> new ResourceNotFoundException("Champion non trouvé"));
        }

        private ResponseEntity<Resource> serveImage(String publicPath) {
                String relativePath = publicPath.startsWith("/") ? publicPath.substring(1) : publicPath;
                Path path = Paths.get(relativePath);

                if (!java.nio.file.Files.exists(path)) {
                        throw new ResourceNotFoundException("Fichier image manquant sur le serveur");
                }

                try {
                        InputStreamResource resource = new InputStreamResource(new FileInputStream(path.toFile()));
                        return ResponseEntity.ok()
                                        .contentType(MediaType.parseMediaType("image/webp"))
                                        .body(resource);
                } catch (IOException e) {
                        throw new RuntimeException("Erreur lors de la lecture de l'image");
                }
        }
}