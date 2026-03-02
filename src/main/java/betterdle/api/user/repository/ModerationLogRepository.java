package betterdle.api.user.repository;

import betterdle.api.user.model.ModerationLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ModerationLogRepository extends JpaRepository<ModerationLog, Integer> {
    List<ModerationLog> findByTargetUserIdOrderByCreatedAtDesc(Integer targetUserId);
}
