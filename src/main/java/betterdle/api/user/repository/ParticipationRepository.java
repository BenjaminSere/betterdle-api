package betterdle.api.user.repository;

import betterdle.api.user.model.Participation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ParticipationRepository extends JpaRepository<Participation, Integer> {
    Optional<Participation> findByUserIdAndDleIdAndPlayedDate(Integer userId, Integer dleId, LocalDate playedDate);

    List<Participation> findByUserIdAndDleIdOrderByPlayedDateDesc(Integer userId, Integer dleId);
}
