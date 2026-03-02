package betterdle.api.user.repository;

import betterdle.api.user.model.UserStreak;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserStreakRepository extends JpaRepository<UserStreak, Integer> {
    Optional<UserStreak> findByUserIdAndDleId(Integer userId, Integer dleId);
}
