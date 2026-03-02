package betterdle.api.user.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ParticipationSubmitDTO {
    @NotNull
    private Integer dleId;

    @NotNull
    private Integer score;

    @NotNull
    private Boolean isSuccess;

    @NotNull
    private Integer attempts;
}
