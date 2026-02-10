package betterdle.api.core.service;

import betterdle.api.config.Game;

public interface GameUpdater {
    void checkAndUpdate();

    Game getSupportedGame();
}
