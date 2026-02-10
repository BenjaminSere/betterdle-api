package betterdle.api;

import betterdle.api.config.Game;
import betterdle.api.core.factory.GameUpdaterFactory;
import betterdle.api.core.service.GameUpdater;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class Initializator implements CommandLineRunner {

    private final GameUpdaterFactory gameUpdaterFactory;

    public Initializator(GameUpdaterFactory gameUpdaterFactory) {
        this.gameUpdaterFactory = gameUpdaterFactory;
    }

    @Override
    public void run(String... args) {
        // Choisir le jeu et la langue facilement
        initialize(Game.LOL);
    }

    private void initialize(Game game) {
        GameUpdater updater = gameUpdaterFactory.getUpdater(game);
        if (updater != null) {
            updater.checkAndUpdate();
        } else {
            System.err.println("Aucun initialiseur trouvé pour " + game);
        }
    }
}
