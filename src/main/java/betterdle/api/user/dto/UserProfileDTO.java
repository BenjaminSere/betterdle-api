package betterdle.api.user.dto;

import betterdle.api.user.model.UserStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class UserProfileDTO {
    private Integer id;
    private String username;
    private UserStatus status;
    private LocalDateTime createdAt;

    // Preferences included
    private String language;
    private String theme;
    private boolean appearInLeaderboards;

    private List<String> roles;
}
