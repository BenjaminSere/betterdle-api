package betterdle.api;

import betterdle.api.config.Game;
import betterdle.api.config.Locale;
import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class Initializator implements CommandLineRunner {

    @Autowired
    private List<GameDataInitializer> initializers;

    @Override
    public void run(String... args) {
        // Tu peux ici choisir le jeu et la langue facilement
        initialize(Game.LOL, Locale.FR_FR, true);
    }

    private void initialize(Game game, Locale locale, boolean onlyFirst) {
        initializers.stream()
                .filter(i -> i.getSupportedGame() == game)
                .findFirst()
                .ifPresentOrElse(
                        i -> {
                            try {
                                i.init(locale, onlyFirst);
                            } catch (IOException e) {
                                System.err.println("Erreur init : " + e.getMessage());
                            }
                        },
                        () -> System.err.println("Aucun initialiseur trouvé pour " + game));
    }
}
