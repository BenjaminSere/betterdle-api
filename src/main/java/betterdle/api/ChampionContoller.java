package betterdle.api;

import betterdle.api.config.Game;
import betterdle.api.config.Locale;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api/v1/{gameStr}/{localeStr}/champions")
public class ChampionContoller {

    @Autowired
    private ChampionRepository championRepository;

    private final String IMAGE_ROOT = "src/main/resources/static";

    @GetMapping
    public Page<Champion> findAll(@PathVariable String gameStr,
            @PathVariable String localeStr,
            Pageable pageable) {
        validateParams(gameStr, localeStr);
        return championRepository.findAll(pageable);
    }

    @GetMapping("/{name}")
    public Champion findByName(@PathVariable String gameStr, @PathVariable String localeStr,
            @PathVariable String name) {
        validateParams(gameStr, localeStr);
        return getChampionOr404(name);
    }

    @GetMapping(value = "/{name}/images/icon", produces = "image/webp")
    public ResponseEntity<Resource> getIcon(@PathVariable String gameStr, @PathVariable String localeStr,
            @PathVariable String name) {
        validateParams(gameStr, localeStr);
        Champion c = getChampionOr404(name);
        return serveImage(c.getIconURL());
    }

    @GetMapping(value = "/{name}/images/passive", produces = "image/webp")
    public ResponseEntity<Resource> getPassive(@PathVariable String gameStr, @PathVariable String localeStr,
            @PathVariable String name) {
        validateParams(gameStr, localeStr);
        Champion c = getChampionOr404(name);
        return serveImage(c.getPassiveIconURL());
    }

    @GetMapping(value = "/{name}/images/spells/{spellKey}", produces = "image/webp")
    public ResponseEntity<Resource> getSpell(@PathVariable String gameStr, @PathVariable String localeStr,
            @PathVariable String name, @PathVariable String spellKey) {
        validateParams(gameStr, localeStr);
        Champion c = getChampionOr404(name);
        Champion.Spell spell = c.getSpells().stream()
                .filter(s -> s.getImageUrl().contains("/" + spellKey.toUpperCase() + ".webp"))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sort non trouvé"));
        return serveImage(spell.getImageUrl());
    }

    @GetMapping(value = "/{name}/images/loading", produces = "image/webp")
    public ResponseEntity<Resource> getLoading(@PathVariable String gameStr, @PathVariable String localeStr,
            @PathVariable String name, @RequestParam(defaultValue = "0") int skinNum) {
        validateParams(gameStr, localeStr);
        Champion c = getChampionOr404(name);
        Champion.Skin skin = c.getSkins().stream()
                .filter(s -> s.getNum() == skinNum)
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Skin non trouvé"));
        return serveImage(skin.getLoadingUrl());
    }

    @GetMapping(value = "/{name}/images/splash", produces = "image/webp")
    public ResponseEntity<Resource> getSplash(@PathVariable String gameStr, @PathVariable String localeStr,
            @PathVariable String name, @RequestParam(defaultValue = "0") int skinNum) {
        validateParams(gameStr, localeStr);
        Champion c = getChampionOr404(name);
        Champion.Skin skin = c.getSkins().stream()
                .filter(s -> s.getNum() == skinNum)
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Skin non trouvé"));
        return serveImage(skin.getSplashUrl());
    }

    // --- Helpers ---

    private void validateParams(String gameStr, String localeStr) {
        Game game = Game.fromId(gameStr);
        Locale locale = Locale.fromId(localeStr);

        if (game == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Jeu non supporté");
        if (locale == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Langue non supportée");

        // Pour l'instant on ne gère que LoL dans ce repository
        if (game != Game.LOL)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Ressource indisponible pour ce jeu");
    }

    private Champion getChampionOr404(String name) {
        return championRepository.findByNameIgnoreCase(name)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Champion non trouvé"));
    }

    private ResponseEntity<Resource> serveImage(String publicPath) {
        // publicPath ressemble à "/data/images/..."
        // On le concatène au dossier static physique
        Path path = Paths.get(IMAGE_ROOT + publicPath);
        Resource resource = new FileSystemResource(path);

        if (!resource.exists()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Fichier image manquant sur le serveur");
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("image/webp"))
                .body(resource);
    }
}