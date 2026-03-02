package betterdle.api.user.service;

import betterdle.api.user.dto.ParticipationSubmitDTO;
import betterdle.api.user.dto.StreakDTO;
import betterdle.api.user.model.Participation;
import betterdle.api.user.model.User;
import betterdle.api.user.model.UserStreak;
import betterdle.api.user.repository.ParticipationRepository;
import betterdle.api.user.repository.UserRepository;
import betterdle.api.user.repository.UserStreakRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class ParticipationService {

    private final ParticipationRepository participationRepository;
    private final UserStreakRepository userStreakRepository;
    private final UserRepository userRepository;

    @Transactional
    public StreakDTO submitParticipation(Integer userId, ParticipationSubmitDTO dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        LocalDate today = LocalDate.now();

        // Prevent duplicate participations for the same day
        if (participationRepository.findByUserIdAndDleIdAndPlayedDate(userId, dto.getDleId(), today).isPresent()) {
            throw new RuntimeException("Already participated today");
        }

        Participation participation = Participation.builder()
                .user(user)
                .dleId(dto.getDleId())
                .playedDate(today)
                .score(dto.getScore())
                .isSuccess(dto.getIsSuccess())
                .attempts(dto.getAttempts())
                .build();

        participationRepository.save(participation);

        // Update streaks
        UserStreak streak = userStreakRepository.findByUserIdAndDleId(userId, dto.getDleId())
                .orElseGet(() -> UserStreak.builder().user(user).dleId(dto.getDleId()).build());

        if (dto.getIsSuccess()) {
            if (streak.getLastPlayedDate() != null && streak.getLastPlayedDate().equals(today.minusDays(1))) {
                streak.setCurrentStreak(streak.getCurrentStreak() + 1);
            } else {
                streak.setCurrentStreak(1);
            }
            if (streak.getCurrentStreak() > streak.getBestStreak()) {
                streak.setBestStreak(streak.getCurrentStreak());
            }
        } else {
            streak.setCurrentStreak(0);
        }

        streak.setLastPlayedDate(today);
        userStreakRepository.save(streak);

        // Update Total Score Ranking can be done here or asynchronously

        return StreakDTO.builder()
                .userId(userId)
                .dleId(dto.getDleId())
                .currentStreak(streak.getCurrentStreak())
                .bestStreak(streak.getBestStreak())
                .lastPlayedDate(streak.getLastPlayedDate())
                .build();
    }
}
