package betterdle.api.lol.service;

import betterdle.api.config.Game;
import betterdle.api.config.Locale;
import betterdle.api.core.service.GameUpdater;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class LolGameUpdater implements GameUpdater {

    private final LolDataInitializer lolDataInitializer;

    @Value("${betterdle.sync.lol.only-first:false}")
    private boolean onlyFirst;

    @Override
    public void checkAndUpdate() {
        System.out.println("Checking updates for LOL...");
        try {
            String remoteVersion = lolDataInitializer.fetchLatestVersion();
            String localVersion = lolDataInitializer.getCurrentVersion();

            if (!remoteVersion.equals(localVersion)) {
                System.out.println("Versions differ (Local: " + localVersion + ", Remote: " + remoteVersion
                        + "). Triggering init.");
                lolDataInitializer.init(Locale.FR_FR, onlyFirst);
            } else {
                System.out.println("Local version is up-to-date with remote version (" + localVersion
                        + "). No need to trigger init.");
                // Note: The forced init when DB is empty is handled in Initializator as
                // requested.
            }
        } catch (IOException e) {
            System.err.println("Failed to check LOL versions: " + e.getMessage());
        }
    }

    @Override
    public Game getSupportedGame() {
        return Game.LOL;
    }
}
