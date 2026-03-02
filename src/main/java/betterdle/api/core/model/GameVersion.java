package betterdle.api.core.model;

import betterdle.api.config.Game;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "game_version")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GameVersion {
    @Id
    @Enumerated(EnumType.STRING)
    private Game game;

    private String version;
}
