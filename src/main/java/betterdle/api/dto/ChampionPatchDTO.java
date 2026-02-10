package betterdle.api.dto;

import betterdle.api.lol.model.enums.ChampionClass;
import betterdle.api.lol.model.enums.Gender;
import betterdle.api.lol.model.enums.Region;
import betterdle.api.lol.model.enums.Resource;
import lombok.Data;
import java.util.Date;
import java.util.List;

/**
 * DTO pour les requêtes PATCH sur les champions.
 * Tous les champs sont optionnels (seuls les champs fournis seront mis à jour).
 */
@Data
public class ChampionPatchDTO {
    private Gender gender;
    private ChampionClass championClass; // Added this field
    private List<String> positions;
    private List<String> species;
    private List<Region> regions;
    private Resource resource;
    private String attackRangeType;
    private Date releaseDate;
}
