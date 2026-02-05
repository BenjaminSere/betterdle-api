package betterdle.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import betterdle.api.config.Game;
import betterdle.api.config.Locale;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Initialise les données des champions en croisant plusieurs sources.
 * Gère les erreurs 500 des APIs tierces pour garantir le fonctionnement de
 * l'app.
 */
@Component
public class LolDataInitializer implements GameDataInitializer {

    private final ChampionRepository championRepository;
    private final ObjectMapper mapper = new ObjectMapper();

    private static final String DDRAGON_URL = "https://ddragon.leagueoflegends.com/cdn/";
    private static final String CDRAGON_URL = "https://raw.communitydragon.org/latest/plugins/rcp-be-lol-game-data/global/default/v1/champions/";
    private static final String MERAKI_URL = "https://cdn.merakianalytics.com/riot/lol/resources/latest/en-US/champions.json";

    @Autowired
    public LolDataInitializer(ChampionRepository championRepository) {
        this.championRepository = championRepository;
    }

    @Override
    public Game getSupportedGame() {
        return Game.LOL;
    }

    @Override
    public void init(Locale locale, boolean onlyFirst) throws IOException {
        String lang = locale.getId();
        String version = fetchLatestVersion();

        // 1. Chargement sécurisé de Meraki (Source fragile)
        JsonNode merakiData = null;
        try {
            merakiData = mapper.readTree(new URL(MERAKI_URL));
        } catch (Exception e) {
            System.err.println(
                    "WARNING: Meraki Analytics indisponible (Erreur 500?). Les données 'Gender/Region/Species' seront limitées.");
        }

        // 2. Index Riot (Source principale)
        JsonNode riotSummary = mapper.readTree(new URL(DDRAGON_URL + version + "/data/" + lang + "/champion.json"))
                .get("data");

        String relativePath = "data/images/lol/" + lang + "/champions/";
        String localBaseDir = "src/main/resources/static/" + relativePath;
        String apiBaseDir = "/" + relativePath;

        List<Champion> champions = new ArrayList<>();
        Iterator<String> championNames = riotSummary.fieldNames();

        while (championNames.hasNext()) {
            String nameId = championNames.next();
            int numericId = riotSummary.get(nameId).get("key").asInt();

            try {
                // 3. Chargement sécurisé de CommunityDragon
                JsonNode cdChamp = null;
                try {
                    cdChamp = mapper.readTree(new URL(CDRAGON_URL + numericId + ".json"));
                } catch (Exception e) {
                    System.err.println("WARNING: CommunityDragon indisponible pour " + nameId);
                }

                // 4. Extraction sécurisée de Meraki
                JsonNode extra = (merakiData != null) ? merakiData.get(nameId) : null;

                champions.add(processChampion(nameId, version, lang, localBaseDir, apiBaseDir, cdChamp, extra));
                System.out.println("Initialisé : " + nameId);
            } catch (Exception e) {
                System.err.println("CRITICAL: Échec total pour " + nameId + " : " + e.getMessage());
            }

            if (onlyFirst)
                break;
        }
        championRepository.saveAll(champions);
    }

    private Champion processChampion(String id, String version, String lang, String localBase, String apiBase,
            JsonNode cdData, JsonNode merakiData) throws IOException {
        // DataDragon est notre source de repli obligatoire
        JsonNode detail = mapper
                .readTree(new URL(DDRAGON_URL + version + "/data/" + lang + "/champion/" + id + ".json")).get("data")
                .get(id);

        Champion champ = new Champion();
        champ.setName(detail.get("name").asText());

        // --- GESTION DES REPLIS (FALLBACKS) ---

        // Bio : CDragon > DataDragon
        if (cdData != null && cdData.has("shortBio")) {
            champ.setDescription(cdData.get("shortBio").asText());
        } else {
            champ.setDescription(detail.get("lore").asText());
        }

        // Rôle : CDragon > DataDragon
        if (cdData != null && cdData.has("roles")) {
            champ.setRole(cdData.get("roles").get(0).asText());
        } else {
            champ.setRole(detail.get("tags").get(0).asText());
        }

        // Portée : CDragon > Calcul manuel DataDragon
        if (cdData != null && cdData.has("tacticalInfo")) {
            champ.setAttackRangeType(cdData.get("tacticalInfo").get("attackType").asText());
        } else {
            int range = detail.get("stats").get("attackrange").asInt();
            champ.setAttackRangeType(range > 200 ? "ranged" : "melee");
        }

        // Métadonnées Meraki (Optionnelles)
        if (merakiData != null) {
            champ.setGender(merakiData.path("attributeRatings").path("gender").asText("Unknown"));
            champ.setSpecies(merakiData.path("species").asText("Unknown"));
            champ.setRegion(merakiData.path("biography").path("region").asText("Unknown"));
            try {
                String dateStr = merakiData.get("releaseDate").asText();
                champ.setReleaseDate(new SimpleDateFormat("yyyy-MM-dd").parse(dateStr));
            } catch (Exception ignored) {
            }
        } else {
            // Valeurs par défaut si Meraki est HS
            champ.setGender("Unknown");
            champ.setSpecies("Unknown");
            champ.setRegion("Unknown");
        }

        // --- ASSETS (Toujours via DataDragon) ---
        String localDir = localBase + id + "/";
        String apiPath = apiBase + id + "/";

        saveImage(DDRAGON_URL + version + "/img/champion/" + id + ".png", localDir + "icon/icon.webp");
        champ.setIconURL(apiPath + "icon/icon.webp");

        if (detail.has("passive")) {
            String passiveImg = detail.get("passive").get("image").get("full").asText();
            saveImage(DDRAGON_URL + version + "/img/passive/" + passiveImg, localDir + "passive/icon.webp");
            champ.setPassiveIconURL(apiPath + "passive/icon.webp");
        }

        champ.setSpells(processSpells(detail, version, localDir, apiPath));
        champ.setSkins(processSkins(id, detail, localDir, apiPath));

        return champ;
    }

    private List<Champion.Spell> processSpells(JsonNode detail, String version, String localDir, String apiPath) {
        List<Champion.Spell> spells = new ArrayList<>();
        String[] keys = { "Q", "W", "E", "R" };
        int i = 0;
        for (JsonNode s : detail.get("spells")) {
            String key = (i < keys.length) ? keys[i] : "Extra_" + i;
            saveImage(DDRAGON_URL + version + "/img/spell/" + s.get("image").get("full").asText(),
                    localDir + "spells/" + key + ".webp");

            Champion.Spell spell = new Champion.Spell();
            spell.setName(s.get("name").asText());
            spell.setDescription(s.get("description").asText());
            spell.setCooldown(s.get("cooldownBurn").asText());
            spell.setImageUrl(apiPath + "spells/" + key + ".webp");
            spells.add(spell);
            i++;
        }
        return spells;
    }

    private List<Champion.Skin> processSkins(String id, JsonNode detail, String localDir, String apiPath) {
        List<Champion.Skin> skins = new ArrayList<>();
        for (JsonNode sk : detail.get("skins")) {
            int num = sk.get("num").asInt();
            saveImage(DDRAGON_URL + "img/champion/splash/" + id + "_" + num + ".jpg",
                    localDir + "skins/splash/" + num + ".webp");
            saveImage(DDRAGON_URL + "img/champion/loading/" + id + "_" + num + ".jpg",
                    localDir + "skins/loading/" + num + ".webp");

            Champion.Skin skin = new Champion.Skin();
            skin.setName(sk.get("name").asText());
            skin.setNum(num);
            skin.setSplashUrl(apiPath + "skins/splash/" + num + ".webp");
            skin.setLoadingUrl(apiPath + "skins/loading/" + num + ".webp");
            skins.add(skin);
        }
        return skins;
    }

    private String fetchLatestVersion() throws IOException {
        return mapper.readValue(new URL("https://ddragon.leagueoflegends.com/api/versions.json"), String[].class)[0];
    }

    private void saveImage(String url, String dest) {
        try (InputStream in = new URL(url).openStream()) {
            Path target = Paths.get(dest);
            Files.createDirectories(target.getParent());
            Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            System.err.println("Fail asset: " + url);
        }
    }
}