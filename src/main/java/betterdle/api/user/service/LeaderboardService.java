package betterdle.api.user.service;

import betterdle.api.user.dto.RankDTO;
import betterdle.api.user.repository.UserRankingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LeaderboardService {

        private final UserRankingRepository userRankingRepository;

        public List<RankDTO> getGlobalLeaderboard(int limit) {
                // Find top N players globally (dleId is null)
                // Here we can use a query method on the repository if we want:
                // By default we emulate it.
                return userRankingRepository
                                .findAll(PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "totalScore")))
                                .stream()
                                .filter(ur -> ur.getDleId() == null)
                                .map(ur -> RankDTO.builder()
                                                .userId(ur.getUser().getId())
                                                .username(ur.getUser().getUsername())
                                                .dleId(ur.getDleId())
                                                .totalScore(ur.getTotalScore())
                                                .rankPosition(ur.getRankPosition())
                                                .build())
                                .collect(Collectors.toList());
        }

        public List<RankDTO> getDleLeaderboard(Integer dleId, int limit) {
                return userRankingRepository
                                .findAll(PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "totalScore")))
                                .stream()
                                .filter(ur -> dleId.equals(ur.getDleId()))
                                .map(ur -> RankDTO.builder()
                                                .userId(ur.getUser().getId())
                                                .username(ur.getUser().getUsername())
                                                .dleId(ur.getDleId())
                                                .totalScore(ur.getTotalScore())
                                                .rankPosition(ur.getRankPosition())
                                                .build())
                                .collect(Collectors.toList());
        }
}
