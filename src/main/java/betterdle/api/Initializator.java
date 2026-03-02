package betterdle.api;

import betterdle.api.config.Game;
import betterdle.api.config.Locale;
import betterdle.api.core.factory.GameUpdaterFactory;
import betterdle.api.core.service.GameUpdater;
import betterdle.api.lol.repository.ChampionRepository;
import betterdle.api.lol.service.LolDataInitializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class Initializator implements CommandLineRunner {

    private final GameUpdaterFactory gameUpdaterFactory;
    private final ChampionRepository championRepository;
    private final LolDataInitializer lolDataInitializer;

    @Value("${betterdle.sync.lol.only-first:false}")
    private boolean onlyFirst;

    public Initializator(GameUpdaterFactory gameUpdaterFactory, ChampionRepository championRepository,
            LolDataInitializer lolDataInitializer) {
        this.gameUpdaterFactory = gameUpdaterFactory;
        this.championRepository = championRepository;
        this.lolDataInitializer = lolDataInitializer;
    }

    @Override
    public void run(String... args) {
        // Choisir le jeu et la langue facilement
        if (championRepository.count() == 0) {
            System.out.println("Champion repository is empty! Forcing initialization.");
            lolDataInitializer.init(Locale.FR_FR, onlyFirst);
        } else {
            initialize(Game.LOL);
        }
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
