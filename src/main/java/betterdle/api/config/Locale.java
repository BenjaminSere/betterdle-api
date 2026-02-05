package betterdle.api.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Locale {
    FR_FR("fr_FR"),
    EN_US("en_US");

    private final String id;

    public static Locale fromId(String id) {
        for (Locale l : values()) {
            if (l.id.equalsIgnoreCase(id)) return l;
        }
        return null;
    }
}