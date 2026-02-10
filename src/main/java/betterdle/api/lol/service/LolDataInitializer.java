package betterdle.api.lol.service;

import betterdle.api.config.Locale;
import betterdle.api.core.model.GlobalConfiguration;
import betterdle.api.core.repository.GlobalConfigurationRepository;
import betterdle.api.lol.model.Champion;
import betterdle.api.lol.model.enums.SyncStatus;
import betterdle.api.lol.repository.ChampionRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Service orchestrating the initialization and update of LoL data.
 * Uses DDragonService for fetching and ChampionMapper for mapping.
 */
@Service
@RequiredArgsConstructor
public class LolDataInitializer {

    private final ChampionRepository repository;
    private final DDragonService dDragonService;
    private final GlobalConfigurationRepository configRepository;
    private final ChampionSyncService championSyncService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String VERSION_KEY = "LOL_VERSION";
    private static final int THREAD_POOL_SIZE = 10;

    public void init(Locale locale, boolean onlyFirst) {
        try {
            String remoteVersion = dDragonService.fetchLatestVersion();
            String localVersion = getCurrentVersion();

            System.out.println(
                    "=== LOL Data Sync Check (Remote: " + remoteVersion + ", Local: " + localVersion + ") ===");

            // 1. Fetch from DDragon
            JsonNode championsSummary = dDragonService.fetchChampionsSummary(locale.getId(), remoteVersion);

            // 2. Load Local Details (Static mapping)
            Map<String, JsonNode> localDetails = loadLocalDetails();

            // 3. Identify Champions to Sync
            List<String> allRemoteIds = new ArrayList<>();
            championsSummary.fieldNames().forEachRemaining(allRemoteIds::add);

            if (onlyFirst && !allRemoteIds.isEmpty()) {
                allRemoteIds = allRemoteIds.subList(0, 1);
            }

            List<String> championsToSync = identifyChampionsToSync(allRemoteIds, remoteVersion, championsSummary);
            System.out.println("Found " + championsToSync.size() + " champions requiring sync.");

            // 4. Update Metadata (Sequential)
            System.out.println("--- Starting Metadata Sync ---");
            for (String id : championsToSync) {
                // Determine if we need to create a new one or update existing
                Optional<Champion> existingOpt = repository
                        .findByNameIgnoreCase(championsSummary.get(id).get("name").asText());
                Champion champion = existingOpt.orElse(new Champion());

                // If it's a new detected champion, we might want to set basic info first
                if (champion.getId() == null) {
                    champion.setSyncStatus(SyncStatus.DETECTED);
                }

                championSyncService.syncMetadata(champion, id, remoteVersion, locale, championsSummary.get(id),
                        localDetails);
            }

            // 5. Download Assets (Parallel)
            System.out.println("--- Starting Asset Sync (Parallel) ---");
            ExecutorService executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

            // Revised Loop for Assets using the list of IDs we know we need to update
            for (String id : championsToSync) {
                executor.submit(() -> {
                    try {
                        Optional<Champion> opt = repository
                                .findByNameIgnoreCase(championsSummary.get(id).get("name").asText());
                        if (opt.isPresent()) {
                            championSyncService.syncAssets(opt.get(), id, remoteVersion, locale);
                            System.out.println("Synced assets for: " + id);
                        }
                    } catch (Exception e) {
                        System.err.println("Error syncing assets for " + id + ": " + e.getMessage());
                    }
                });
            }

            executor.shutdown();
            try {
                executor.awaitTermination(30, TimeUnit.MINUTES);
            } catch (InterruptedException e) {
                System.err.println("Asset sync interrupted.");
            }

            updateCurrentVersion(remoteVersion);
            System.out.println("=== Sync Complete ===");

        } catch (IOException e) {
            System.err.println("Initialization failed: " + e.getMessage());
        }
    }

    // Helper to filter efficiently
    private List<String> identifyChampionsToSync(List<String> remoteIds, String remoteVersion, JsonNode summary) {
        List<String> toSync = new ArrayList<>();
        List<Champion> localChampions = repository.findAll();

        // map name -> champion
        Map<String, Champion> nameToChampion = localChampions.stream()
                .collect(Collectors.toMap(c -> c.getName().toLowerCase(), c -> c));

        for (String id : remoteIds) {
            String name = summary.get(id).get("name").asText().toLowerCase();
            Champion local = nameToChampion.get(name);

            if (local == null) {
                toSync.add(id); // New
            } else if (!remoteVersion.equals(local.getVersion()) || local.getSyncStatus() != SyncStatus.READY) {
                toSync.add(id); // Outdated or Incomplete
            }
        }
        return toSync;
    }

    private Map<String, JsonNode> loadLocalDetails() {
        Map<String, JsonNode> map = new HashMap<>();
        try (InputStream is = getClass().getResourceAsStream("/data/lol/championsDetail.json")) {
            if (is == null)
                return map;
            JsonNode root = objectMapper.readTree(is);
            JsonNode champions = root.get("champions");
            if (champions != null) {
                for (JsonNode node : champions) {
                    String originalId = node.get("id").asText();
                    String normalizedId = originalId
                            .replace("'", "")
                            .replace(" ", "")
                            .replace(".", "")
                            .toLowerCase();
                    map.put(normalizedId, node);

                    if (normalizedId.equals("wukong")) {
                        map.put("monkeyking", node);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading local details: " + e.getMessage());
        }
        return map;
    }

    public String getCurrentVersion() {
        return configRepository.findById(VERSION_KEY)
                .map(GlobalConfiguration::getConfValue)
                .orElse("0.0.0");
    }

    private void updateCurrentVersion(String version) {
        GlobalConfiguration config = new GlobalConfiguration(VERSION_KEY, version);
        configRepository.save(config);
    }

    public String fetchLatestVersion() throws IOException {
        return dDragonService.fetchLatestVersion();
    }
}
