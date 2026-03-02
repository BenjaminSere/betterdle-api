package betterdle.api.user.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "user_streaks", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "user_id", "dle_id" })
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserStreak {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private User user;

    @Column(name = "dle_id", nullable = false)
    private Integer dleId;

    @Column(name = "current_streak", nullable = false)
    @Builder.Default
    private Integer currentStreak = 0;

    @Column(name = "best_streak", nullable = false)
    @Builder.Default
    private Integer bestStreak = 0;

    @Column(name = "last_played_date")
    private LocalDate lastPlayedDate;
}
