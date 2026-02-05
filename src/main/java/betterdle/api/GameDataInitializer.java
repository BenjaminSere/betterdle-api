package betterdle.api;

import betterdle.api.config.Game;
import betterdle.api.config.Locale;
import java.io.IOException;

public interface GameDataInitializer {
    Game getSupportedGame();

    void init(Locale locale, boolean onlyFirst) throws IOException;
}