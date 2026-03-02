package betterdle.api.user.repository;

import betterdle.api.user.model.UserDleFollow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserDleFollowRepository extends JpaRepository<UserDleFollow, Integer> {
    List<UserDleFollow> findByUserId(Integer userId);

    Optional<UserDleFollow> findByUserIdAndDleId(Integer userId, Integer dleId);

    boolean existsByUserIdAndDleId(Integer userId, Integer dleId);
}
