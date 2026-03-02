package betterdle.api.core.repository;

import betterdle.api.config.Game;
import betterdle.api.core.model.GameVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameVersionRepository extends JpaRepository<GameVersion, Game> {
}
