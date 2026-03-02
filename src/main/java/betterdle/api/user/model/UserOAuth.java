package betterdle.api.user.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_oauth_accounts", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "provider", "provider_user_id" })
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserOAuth {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OAuthProvider provider;

    @Column(name = "provider_user_id", nullable = false)
    private String providerUserId;

}
