package betterdle.api.lol.model;

import jakarta.persistence.*;
import lombok.Data;
import java.io.Serializable;

/**
 * Représente un skin (apparence cosmétique) d'un champion.
 */
@Data
@Entity
@Table(name = "champion_skins")
public class ChampionSkin implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;
    private int num; // Numéro du skin (0 = default)
    private String splashUrl;
    private String loadingUrl;
}
