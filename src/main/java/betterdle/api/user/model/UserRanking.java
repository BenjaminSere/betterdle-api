package betterdle.api.user.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_rankings", uniqueConstraints = {
        // null values in dle_id might not be considered identical by default in some
        // DBs for unique constraints,
        // but typically Postgres handles it if specialized or we manage it carefully.
        // A partial index is better, but this constraint is a good start.
        @UniqueConstraint(columnNames = { "user_id", "dle_id" })
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRanking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private User user;

    @Column(name = "dle_id")
    private Integer dleId; // Null means global ranking

    @Column(name = "total_score", nullable = false)
    @Builder.Default
    private Integer totalScore = 0;

    @Column(name = "rank_position")
    private Integer rankPosition; // Null means not yet ranked

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
