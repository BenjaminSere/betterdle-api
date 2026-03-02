package betterdle.api.user.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "participations", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "user_id", "dle_id", "played_date" })
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Participation {

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

    @Column(name = "played_date", nullable = false)
    private LocalDate playedDate;

    @Column(nullable = false)
    @Builder.Default
    private Integer score = 0;

    @Column(name = "is_success", nullable = false)
    @Builder.Default
    private Boolean isSuccess = false;

    @Column(nullable = false)
    @Builder.Default
    private Integer attempts = 0;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
