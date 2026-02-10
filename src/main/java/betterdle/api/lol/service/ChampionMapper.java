package betterdle.api.lol.service;

import betterdle.api.lol.model.Champion;
import betterdle.api.lol.model.ChampionSkin;
import betterdle.api.lol.model.ChampionSpell;
import betterdle.api.lol.model.enums.ChampionClass;
import betterdle.api.lol.model.enums.Gender;
import betterdle.api.lol.model.enums.Region;
import betterdle.api.lol.model.enums.Resource;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Mapper responsible for converting JSON data (from API or local files)
 * into strong typed Champion entities.
 */
@Service
public class ChampionMapper {

    public Champion mapToChampion(String id, JsonNode apiData, JsonNode localDetail, Champion existing) {
        Champion champ = (existing != null) ? existing : new Champion();

        // Basic Data from API
        if (champ.getName() == null)
            champ.setName(apiData.get("name").asText());

        // Map Tags to ChampionClass (Role)
        if (champ.getChampionClass() == null && apiData.has("tags") && apiData.get("tags").size() > 0) {
            String roleStr = apiData.get("tags").get(0).asText();
            champ.setChampionClass(resolveEnum(ChampionClass.class, roleStr, ChampionClass.UNKNOWN));
        }

        // Local Details Mapping
        if (localDetail != null) {
            if (champ.getGender() == null && localDetail.has("gender")) {
                champ.setGender(resolveEnum(Gender.class, localDetail.get("gender").asText(), Gender.UNKNOWN));
            }

            if ((champ.getPositions() == null || champ.getPositions().isEmpty()) && localDetail.has("positions")) {
                List<String> positions = new ArrayList<>();
                for (JsonNode pos : localDetail.get("positions")) {
                    positions.add(pos.asText());
                }
                champ.setPositions(positions);
            }

            if ((champ.getSpecies() == null || champ.getSpecies().isEmpty()) && localDetail.has("species")) {
                List<String> species = new ArrayList<>();
                for (JsonNode sp : localDetail.get("species")) {
                    species.add(sp.asText());
                }
                champ.setSpecies(species);
            }

            if ((champ.getRegions() == null || champ.getRegions().isEmpty()) && localDetail.has("region")) {
                List<Region> regions = new ArrayList<>();
                JsonNode regionNode = localDetail.get("region");
                if (regionNode.isArray()) {
                    for (JsonNode reg : regionNode) {
                        regions.add(resolveEnum(Region.class, reg.asText(), Region.UNKNOWN));
                    }
                } else {
                    regions.add(resolveEnum(Region.class, regionNode.asText(), Region.UNKNOWN));
                }
                champ.setRegions(regions);
            }

            if (champ.getResource() == null && localDetail.has("resource")) {
                champ.setResource(resolveEnum(Resource.class, localDetail.get("resource").asText(), Resource.OTHER));
            }

            if (champ.getAttackRangeType() == null && localDetail.has("rangeType")) {
                champ.setAttackRangeType(localDetail.get("rangeType").asText());
            }

            if (champ.getReleaseDate() == null && localDetail.has("releaseYear")) {
                try {
                    int year = localDetail.get("releaseYear").asInt();
                    champ.setReleaseDate(new SimpleDateFormat("yyyy-MM-dd").parse(year + "-01-01"));
                } catch (Exception ignored) {
                }
            }
        }

        return champ;
    }

    public void updateDetails(Champion champ, JsonNode detail, String version, String apiPathPrefix) {
        if (champ.getDescription() == null) {
            champ.setDescription(detail.get("lore").asText());
        }

        if (champ.getPassiveIconURL() == null && detail.has("passive")) {
            // Image saving is handled by DDragonService, here we just set the URL path
            // The filename is enforced to be 'icon.webp' by ChampionSyncService.
            champ.setPassiveIconURL(apiPathPrefix + "passive/icon.webp");
        }

        if (champ.getSpells() == null || champ.getSpells().isEmpty()) {
            if (detail.has("spells")) {
                List<ChampionSpell> spells = new ArrayList<>();
                for (JsonNode spellNode : detail.get("spells")) {
                    ChampionSpell spell = new ChampionSpell();
                    spell.setName(spellNode.get("name").asText());
                    spell.setDescription(spellNode.get("description").asText());
                    spell.setCooldown(spellNode.get("cooldownBurn").asText());

                    String imageFull = spellNode.get("image").get("full").asText();
                    spell.setImageUrl(apiPathPrefix + "spells/" + imageFull);

                    spells.add(spell);
                }
                if (champ.getSpells() == null) {
                    champ.setSpells(spells);
                } else {
                    champ.getSpells().addAll(spells);
                }
            }
        }

        if (champ.getSkins() == null || champ.getSkins().isEmpty()) {
            if (detail.has("skins")) {
                List<ChampionSkin> skins = new ArrayList<>();
                for (JsonNode skinNode : detail.get("skins")) {
                    ChampionSkin skin = new ChampionSkin();
                    skin.setName(skinNode.get("name").asText());
                    skin.setNum(skinNode.get("num").asInt());

                    // Construct local API paths for images (assuming they will be downloaded to
                    // these locations)
                    skin.setSplashUrl(apiPathPrefix + "skins/splash_" + skin.getNum() + ".jpg");
                    skin.setLoadingUrl(apiPathPrefix + "skins/loading_" + skin.getNum() + ".jpg");

                    skins.add(skin);
                }
                if (champ.getSkins() == null) {
                    champ.setSkins(skins);
                } else {
                    champ.getSkins().addAll(skins);
                }
            }
        }
    }

    private <T extends Enum<T>> T resolveEnum(Class<T> enumType, String value, T defaultValue) {
        if (value == null)
            return defaultValue;
        try {
            // Normalize: UPPER_CASE, replace spaces/dashes with underscore
            String normalized = value.trim().toUpperCase()
                    .replace(" ", "_")
                    .replace("-", "_")
                    .replace("'", ""); // Kai'Sa -> KAI_SA? No Kai'Sa is name. Void -> VOID.

            // Special cases mapping if needed
            if (enumType == Region.class) {
                if (normalized.equals("BANDLE_CITY"))
                    return Enum.valueOf(enumType, "BANDLE_CITY");
                if (normalized.equals("SHADOW_ISLES"))
                    return Enum.valueOf(enumType, "SHADOW_ISLES");
                // Add aliases if needed
            }

            return Enum.valueOf(enumType, normalized);
        } catch (IllegalArgumentException e) {
            System.err.println("Warning: Unknown enum value '" + value + "' for " + enumType.getSimpleName());
            return defaultValue;
        }
    }
}
