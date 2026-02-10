package betterdle.api.core.model;

import jakarta.persistence.*;
import java.util.Date;

/**
 * Classe abstraite implémentant les champs communs à toutes les entités de jeu.
 * Les classes concrètes (Champion, Agent, Unit, etc.) héritent de cette classe
 * et ajoutent leurs champs spécifiques.
 */
@MappedSuperclass
public abstract class BaseEntity implements GameEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String name;

    @Column(length = 2000)
    private String description;

    private String iconURL;

    @Temporal(TemporalType.DATE)
    private Date releaseDate;

    // ============ Getters & Setters ============

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String getIconURL() {
        return iconURL;
    }

    @Override
    public void setIconURL(String iconURL) {
        this.iconURL = iconURL;
    }

    @Override
    public Date getReleaseDate() {
        return releaseDate;
    }

    @Override
    public void setReleaseDate(Date releaseDate) {
        this.releaseDate = releaseDate;
    }

    /**
     * Implémentation par défaut de isComplete().
     * Les classes filles peuvent override pour ajouter leurs propres critères.
     */
    @Override
    public boolean isComplete() {
        return name != null
                && description != null
                && iconURL != null
                && releaseDate != null;
    }
}
