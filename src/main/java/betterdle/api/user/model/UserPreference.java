package betterdle.api.user.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_preferences")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserPreference {

    @Id
    private Integer id; // Mapped identical to User's ID using MapsId if possible, or just auto-generate

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private User user;

    @Column(nullable = false)
    @Builder.Default
    private String language = "en_US";

    @Column(nullable = false)
    @Builder.Default
    private String theme = "dark";

    @Column(name = "appear_in_leaderboards", nullable = false)
    @Builder.Default
    private boolean appearInLeaderboards = true;

}
