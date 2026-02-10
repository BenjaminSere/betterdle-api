package betterdle.api.lol.model.enums;

public enum Resource {
    MANA,
    ENERGY,
    NONE, // Manaless, Cooldowns only
    HEALTH, // Uses health for abilities (Vladimir, Mundo, Zac)
    RAGE, // Gnar, Renekton, Shyvana, Tryndamere
    FURY, // Renekton (Legacy name often used interchangeably with Rage)
    FEROCITY, // Rengar
    HEAT, // Rumble
    GRIT, // Sett
    FLOW, // Yone, Yasuo
    BLOOD_WELL, // Aatrox
    COURAGE, // Kled
    SHIELD, // Mordekaiser
    OTHER; // For unique or less common resources
}
