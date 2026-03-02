package betterdle.api.user.controller;

import betterdle.api.user.dto.RankDTO;
import betterdle.api.user.service.LeaderboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/leaderboards")
@Tag(name = "Classements", description = "Endpoints pour récupérer les classements globaux et par DLE")
@RequiredArgsConstructor
public class LeaderboardController {

    private final LeaderboardService leaderboardService;

    @GetMapping("/global")
    @Operation(summary = "Classement global", description = "Consulter le classement mondial des joueurs")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Classement récupéré avec succès")
    })
    public ResponseEntity<List<RankDTO>> getGlobalLeaderboard(
            @Parameter(description = "Nombre maximal de résultats") @RequestParam(defaultValue = "100") int limit) {
        return ResponseEntity.ok(leaderboardService.getGlobalLeaderboard(limit));
    }

    @GetMapping("/dle/{dleId}")
    @Operation(summary = "Classement spécifique", description = "Consulter le classement spécifique à un DLE")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Classement récupéré avec succès")
    })
    public ResponseEntity<List<RankDTO>> getDleLeaderboard(
            @Parameter(description = "ID du DLE", required = true) @PathVariable Integer dleId,
            @Parameter(description = "Nombre maximal de résultats") @RequestParam(defaultValue = "100") int limit) {
        return ResponseEntity.ok(leaderboardService.getDleLeaderboard(dleId, limit));
    }
}
