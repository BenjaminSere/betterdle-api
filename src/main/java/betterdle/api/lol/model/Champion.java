package betterdle.api.lol.model;

import betterdle.api.core.model.BaseEntity;
import betterdle.api.lol.model.enums.ChampionClass;
import betterdle.api.lol.model.enums.Gender;
import betterdle.api.lol.model.enums.Region;
import betterdle.api.lol.model.enums.Resource;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Entité représentant un champion de League of Legends.
 * Hérite des champs communs (id, name, description, iconURL, releaseDate) de
 * BaseEntity.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "champions")
public class Champion extends BaseEntity implements Serializable {

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Enumerated(EnumType.STRING)
    private betterdle.api.lol.model.enums.SyncStatus syncStatus = betterdle.api.lol.model.enums.SyncStatus.DETECTED;

    private String version;

    @Enumerated(EnumType.STRING)
    private ChampionClass championClass; // Primary role from Riot (Fighter, Mage, etc.)

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> positions; // Top, Mid, Jungle, ADC, Support

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> species; // Human, Yordle, Vastaya, etc.

    @Enumerated(EnumType.STRING)
    private Resource resource; // Mana, Energy, Sans mana, etc.

    private String attackRangeType; // Melee, Ranged

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "champion_regions", joinColumns = @JoinColumn(name = "champion_id"))
    @Column(name = "region")
    private List<Region> regions = new ArrayList<>(); // Demacia, Noxus, Freljord, etc.

    private String passiveIconURL;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinColumn(name = "champion_id")
    private List<ChampionSpell> spells;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinColumn(name = "champion_id")
    private List<ChampionSkin> skins;

    /**
     * Un champion est considéré comme complet s'il a tous les champs essentiels
     * remplis.
     */
    @Override
    public boolean isComplete() {
        return super.isComplete() // Champs de BaseEntity (name, description, iconURL, releaseDate)
                && championClass != null
                && gender != null
                && positions != null && !positions.isEmpty()
                && species != null && !species.isEmpty()
                && regions != null && !regions.isEmpty()
                && resource != null
                && attackRangeType != null
                && spells != null && !spells.isEmpty()
                && skins != null && !skins.isEmpty()
                && passiveIconURL != null;
    }
}
