package betterdle.api.core.factory;

import betterdle.api.config.Game;
import betterdle.api.core.service.GameUpdater;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class GameUpdaterFactory {
    private final Map<Game, GameUpdater> updaterMap;

    public GameUpdaterFactory(List<GameUpdater> updaters) {
        this.updaterMap = updaters.stream()
                .collect(Collectors.toMap(GameUpdater::getSupportedGame, Function.identity()));
    }

    public GameUpdater getUpdater(Game game) {
        return updaterMap.get(game);
    }

    public List<GameUpdater> getAllUpdaters() {
        return List.copyOf(updaterMap.values());
    }
}
