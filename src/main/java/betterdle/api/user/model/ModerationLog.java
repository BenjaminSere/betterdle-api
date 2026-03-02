package betterdle.api.user.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "moderation_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ModerationLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private User admin;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_user_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private User targetUser;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ModerationActionType action;

    @Column(length = 2000)
    private String reason;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
