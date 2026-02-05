package betterdle.api;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ChampionRepository extends JpaRepository<Champion, Integer> {
    Optional<Champion> findByNameIgnoreCase(String name);
}