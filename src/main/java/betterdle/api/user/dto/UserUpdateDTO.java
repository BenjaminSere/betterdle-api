package betterdle.api.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserUpdateDTO {
    @NotBlank
    private String username;

    @NotBlank
    private String email;
}
