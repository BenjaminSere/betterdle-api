package betterdle.api.user.service;

import betterdle.api.user.model.ModerationActionType;
import betterdle.api.user.model.ModerationLog;
import betterdle.api.user.model.User;
import betterdle.api.user.model.UserStatus;
import betterdle.api.user.repository.ModerationLogRepository;
import betterdle.api.user.repository.ParticipationRepository;
import betterdle.api.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ModerationService {

    private final UserRepository userRepository;
    private final ModerationLogRepository moderationLogRepository;
    private final ParticipationRepository participationRepository;

    @Transactional
    public void banUser(Integer adminId, Integer targetUserId, String reason) {
        User admin = userRepository.findById(adminId).orElseThrow();
        User targetUser = userRepository.findById(targetUserId).orElseThrow();

        targetUser.setStatus(UserStatus.BANNED);
        userRepository.save(targetUser);

        ModerationLog log = ModerationLog.builder()
                .admin(admin)
                .targetUser(targetUser)
                .action(ModerationActionType.BAN)
                .reason(reason)
                .build();
        moderationLogRepository.save(log);
    }

    @Transactional
    public void suspendUser(Integer adminId, Integer targetUserId, String reason) {
        User admin = userRepository.findById(adminId).orElseThrow();
        User targetUser = userRepository.findById(targetUserId).orElseThrow();

        targetUser.setStatus(UserStatus.SUSPENDED);
        userRepository.save(targetUser);

        ModerationLog log = ModerationLog.builder()
                .admin(admin)
                .targetUser(targetUser)
                .action(ModerationActionType.SUSPEND)
                .reason(reason)
                .build();
        moderationLogRepository.save(log);
    }

    @Transactional
    public void deleteParticipation(Integer adminId, Integer participationId, String reason) {
        User admin = userRepository.findById(adminId).orElseThrow();
        var participation = participationRepository.findById(participationId).orElseThrow();

        participationRepository.delete(participation);

        ModerationLog log = ModerationLog.builder()
                .admin(admin)
                .targetUser(participation.getUser()) // target user is the owner of participation
                .action(ModerationActionType.DELETE_PARTICIPATION)
                .reason(reason + " [ParticipationID: " + participationId + "]")
                .build();
        moderationLogRepository.save(log);
    }
}
