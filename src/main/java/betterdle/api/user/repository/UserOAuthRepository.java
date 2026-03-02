package betterdle.api.user.repository;

import betterdle.api.user.model.OAuthProvider;
import betterdle.api.user.model.UserOAuth;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserOAuthRepository extends JpaRepository<UserOAuth, Integer> {
    Optional<UserOAuth> findByProviderAndProviderUserId(OAuthProvider provider, String providerUserId);

    List<UserOAuth> findByUserId(Integer userId);
}
