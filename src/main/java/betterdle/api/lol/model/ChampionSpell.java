package betterdle.api.lol.model;

import jakarta.persistence.*;
import lombok.Data;
import java.io.Serializable;

/**
 * Représente un sort (Q, W, E, R) d'un champion.
 */
@Data
@Entity
@Table(name = "champion_spells")
public class ChampionSpell implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    @Column(length = 2000)
    private String description;

    private String cooldown;
    private String imageUrl;
}
