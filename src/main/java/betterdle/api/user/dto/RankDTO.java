package betterdle.api.user.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RankDTO {
    private Integer userId;
    private String username;
    private Integer dleId;
    private Integer totalScore;
    private Integer rankPosition;
}
