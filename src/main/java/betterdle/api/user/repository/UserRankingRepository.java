package betterdle.api.user.repository;

import betterdle.api.user.model.UserRanking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRankingRepository extends JpaRepository<UserRanking, Integer> {
    Optional<UserRanking> findByUserIdAndDleId(Integer userId, Integer dleId);
}
