package betterdle.api.user.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class StreakDTO {
    private Integer userId;
    private Integer dleId;
    private Integer currentStreak;
    private Integer bestStreak;
    private LocalDate lastPlayedDate;
}
