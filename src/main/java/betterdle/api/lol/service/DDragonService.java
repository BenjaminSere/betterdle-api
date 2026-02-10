package betterdle.api.lol.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Service responsible for fetching data from Riot's DataDragon API.
 * Handles HTTP requests and JSON parsing.
 */
@Service
public class DDragonService {

    private static final String DDRAGON_URL = "https://ddragon.leagueoflegends.com/cdn/";
    private final ObjectMapper mapper = new ObjectMapper();

    public String fetchLatestVersion() throws IOException {
        String[] versions = mapper.readValue(
                new URL("https://ddragon.leagueoflegends.com/api/versions.json"),
                String[].class);
        return versions[0];
    }

    public JsonNode fetchChampionsSummary(String locale, String version) throws IOException {
        return mapper.readTree(
                new URL(DDRAGON_URL + version + "/data/" + locale + "/champion.json")).get("data");
    }

    public JsonNode fetchChampionDetail(String locale, String version, String championId) throws IOException {
        return mapper.readTree(
                new URL(DDRAGON_URL + version + "/data/" + locale + "/champion/" + championId + ".json"))
                .get("data").get(championId);
    }

    public void downloadImage(String urlString, String localPath) {
        try {
            Path path = Paths.get(localPath);
            if (Files.exists(path)) {
                return;
            }
            Files.createDirectories(path.getParent());

            URL url = new URL(urlString);
            java.net.HttpURLConnection connection = (java.net.HttpURLConnection) url.openConnection();
            connection.setRequestProperty("User-Agent",
                    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36");

            try (var in = connection.getInputStream()) {
                Files.copy(in, path);
            }
        } catch (Exception e) {
            System.err.println("Error downloading image " + urlString + ": " + e.getMessage());
        }
    }

    public String getDDragonBaseUrl() {
        return DDRAGON_URL;
    }
}
