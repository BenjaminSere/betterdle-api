package betterdle.api.core.model;

import java.util.Date;

/**
 * Interface commune pour toutes les entités de jeu (Champions LoL, Agents
 * Valorant, Units TFT, etc.).
 * Définit le contrat minimum que chaque entité doit respecter.
 */
public interface GameEntity {

    /**
     * ID unique de l'entité en base de données.
     */
    Integer getId();

    void setId(Integer id);

    /**
     * Nom de l'entité (affiché dans le jeu).
     */
    String getName();

    void setName(String name);

    /**
     * Description/bio de l'entité.
     */
    String getDescription();

    void setDescription(String description);

    /**
     * URL de l'icône/avatar de l'entité.
     */
    String getIconURL();

    void setIconURL(String iconURL);

    /**
     * Date de sortie de l'entité dans le jeu.
     */
    Date getReleaseDate();

    void setReleaseDate(Date releaseDate);

    /**
     * Vérifie si l'entité a toutes ses données essentielles remplies.
     * Utilisé pour identifier les entités incomplètes dans l'Admin API.
     * 
     * @return true si tous les champs requis sont non-null
     */
    boolean isComplete();
}
