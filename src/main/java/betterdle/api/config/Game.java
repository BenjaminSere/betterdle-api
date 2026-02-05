package betterdle.api.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Game {
    LOL("lol");
    // VALORANT("valorant");

    private final String id;

    public static Game fromId(String id) {
        for (Game g : values()) {
            if (g.id.equalsIgnoreCase(id))
                return g;
        }
        return null;
    }
}
