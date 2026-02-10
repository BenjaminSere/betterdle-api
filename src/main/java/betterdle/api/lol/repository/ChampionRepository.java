package betterdle.api.lol.repository;

import betterdle.api.core.repository.GameEntityRepository;
import betterdle.api.lol.model.Champion;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository pour les champions de League of Legends.
 * Hérite des méthodes CRUD de base de GameEntityRepository.
 */
@Repository
public interface ChampionRepository extends GameEntityRepository<Champion> {

    /**
     * Trouve les champions avec des données incomplètes.
     * Utilise la méthode isComplete() de l'entité Champion.
     */
    @Query("SELECT c FROM Champion c WHERE " +
            "c.name IS NULL OR " +
            "c.description IS NULL OR " +
            "c.championClass IS NULL OR " +
            "c.gender IS NULL OR " +
            "c.positions IS EMPTY OR " +
            "c.species IS EMPTY OR " +
            "c.regions IS EMPTY OR " +
            "c.resource IS NULL OR " +
            "c.attackRangeType IS NULL OR " +
            "c.releaseDate IS NULL OR " +
            "c.iconURL IS NULL OR " +
            "c.passiveIconURL IS NULL OR " +
            "c.spells IS EMPTY OR " +
            "c.skins IS EMPTY")
    List<Champion> findIncompleteChampions();
}
