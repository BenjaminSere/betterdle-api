package betterdle.api.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserPreferencesUpdateDTO {
    @NotBlank
    private String language;

    @NotBlank
    private String theme;

    @NotNull
    private Boolean appearInLeaderboards;
}
