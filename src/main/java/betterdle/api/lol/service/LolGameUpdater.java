package betterdle.api.lol.service;

import betterdle.api.config.Game;
import betterdle.api.config.Locale;
import betterdle.api.core.service.GameUpdater;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LolGameUpdater implements GameUpdater {

    private final LolDataInitializer lolDataInitializer;

    @Override
    public void checkAndUpdate() {
        // In a real scenario, we would check versions here before triggering full init.
        // For now, we delegate to init which fetches latest version anyway.
        // If we want to optimize, we can check version against stored version here.
        System.out.println("Checking updates for LOL...");
        lolDataInitializer.init(Locale.FR_FR, false);
    }

    @Override
    public Game getSupportedGame() {
        return Game.LOL;
    }
}
