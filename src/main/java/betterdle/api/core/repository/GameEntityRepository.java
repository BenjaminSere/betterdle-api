package betterdle.api.core.repository;

import betterdle.api.core.model.GameEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * Interface repository générique pour toutes les entités de jeu.
 * Étend JpaRepository pour fournir les opérations CRUD de base.
 * 
 * @param <T> Type de l'entité (doit implémenter GameEntity)
 * 
 * @NoRepositoryBean empêche Spring de créer une implémentation de cette
 *                   interface directement
 */
@NoRepositoryBean
public interface GameEntityRepository<T extends GameEntity> extends JpaRepository<T, Integer> {

    /**
     * Recherche une entité par son nom (insensible à la casse).
     * 
     * @param name Nom de l'entité à rechercher
     * @return Optional contenant l'entité si trouvée
     */
    java.util.Optional<T> findByNameIgnoreCase(String name);
}
