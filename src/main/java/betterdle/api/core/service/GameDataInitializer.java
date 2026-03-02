package betterdle.api.core.service;

import betterdle.api.config.Locale;

/**
 * Interface for game data initializers.
 * Provides a standard way to initialize data for different games.
 */
public interface GameDataInitializer {
    void init(Locale locale, boolean onlyFirst);
}
