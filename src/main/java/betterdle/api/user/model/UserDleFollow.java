package betterdle.api.user.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_dle_follows", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "user_id", "dle_id" })
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDleFollow {

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

    @Column(name = "followed_at", nullable = false)
    private LocalDateTime followedAt;

    @PrePersist
    protected void onCreate() {
        if (followedAt == null) {
            followedAt = LocalDateTime.now();
        }
    }
}
