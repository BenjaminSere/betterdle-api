package betterdle.api.lol.service;

import betterdle.api.config.Locale;
import betterdle.api.lol.model.Champion;
import betterdle.api.lol.model.enums.SyncStatus;
import betterdle.api.lol.repository.ChampionRepository;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ChampionSyncService {

    private final ChampionRepository repository;
    private final DDragonService dDragonService;
    private final ChampionMapper championMapper;

    /**
     * Synchronizes metadata (stats, spells, etc.) for a champion.
     */
    public Champion syncMetadata(Champion champion, String id, String version, Locale locale, JsonNode summary,
            Map<String, JsonNode> localDetails) {
        try {
            // Map basic data
            JsonNode localDetail = localDetails.get(id.toLowerCase());
            champion = championMapper.mapToChampion(id, summary, localDetail, champion);

            // Fetch detailed data from DDragon
            JsonNode detail = dDragonService.fetchChampionDetail(locale.getId(), version, id);

            // Update details (description, assets paths)
            String apiPathPrefix = "data/images/lol/" + locale.getId() + "/champions/" + id + "/";
            championMapper.updateDetails(champion, detail, version, apiPathPrefix);

            champion.setVersion(version);
            champion.setSyncStatus(SyncStatus.METADATA_SYNCED);

            return repository.save(champion);
        } catch (Exception e) {
            System.err.println("Metadata sync failed for " + id + ": " + e.getMessage());
            champion.setSyncStatus(SyncStatus.INCOMPLETE);
            return repository.save(champion);
        }
    }

    /**
     * Synchronizes assets (images) for a champion.
     */
    public Champion syncAssets(Champion champion, String id, String version, Locale locale) {
        try {
            JsonNode detail = dDragonService.fetchChampionDetail(locale.getId(), version, id);

            String relativePath = "data/images/lol/" + locale.getId() + "/champions/";
            String localBaseDir = "data/images/lol/" + locale.getId() + "/champions/";
            String apiBaseDir = "/" + relativePath;
            String localDir = localBaseDir + id + "/";
            String apiPath = apiBaseDir + id + "/";

            // Icon
            dDragonService.downloadImage(
                    dDragonService.getDDragonBaseUrl() + version + "/img/champion/" + id + ".png",
                    localDir + "icon/icon.webp");
            champion.setIconURL(apiPath + "icon/icon.webp");

            // Passive
            if (detail.has("passive")) {
                String passiveImg = detail.get("passive").get("image").get("full").asText();
                dDragonService.downloadImage(
                        dDragonService.getDDragonBaseUrl() + version + "/img/passive/" + passiveImg,
                        localDir + "passive/icon.webp");
                champion.setPassiveIconURL(apiPath + "passive/icon.webp");
            }

            // Spells
            if (detail.has("spells")) {
                for (JsonNode spell : detail.get("spells")) {
                    String imageFull = spell.get("image").get("full").asText();
                    dDragonService.downloadImage(
                            dDragonService.getDDragonBaseUrl() + version + "/img/spell/" + imageFull,
                            localDir + "spells/" + imageFull);
                }
            }

            // Skins
            if (detail.has("skins")) {
                for (JsonNode skin : detail.get("skins")) {
                    int num = skin.get("num").asInt();
                    // Splash Art
                    dDragonService.downloadImage(
                            "https://ddragon.leagueoflegends.com/cdn/img/champion/splash/" + id + "_" + num + ".jpg",
                            localDir + "skins/splash_" + num + ".jpg");

                    // Loading Screen
                    dDragonService.downloadImage(
                            "https://ddragon.leagueoflegends.com/cdn/img/champion/loading/" + id + "_" + num + ".jpg",
                            localDir + "skins/loading_" + num + ".jpg");
                }
            }

            champion.setSyncStatus(SyncStatus.READY);
            return repository.save(champion);
        } catch (IOException e) {
            System.err.println("Asset download failed for " + id + ": " + e.getMessage());
            // Should we mark as INCOMPLETE or keep as METADATA_SYNCED?
            // If assets fail, it's not ready.
            champion.setSyncStatus(SyncStatus.INCOMPLETE);
            return repository.save(champion);
        }
    }
}
