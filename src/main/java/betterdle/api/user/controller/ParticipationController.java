package betterdle.api.user.controller;

import betterdle.api.user.dto.ParticipationSubmitDTO;
import betterdle.api.user.dto.StreakDTO;
import betterdle.api.user.service.ParticipationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/participations")
@Tag(name = "Participations", description = "Endpoints pour gérer les participations aux DLE")
@RequiredArgsConstructor
public class ParticipationController {

    private final ParticipationService participationService;

    // We pass userId in request for now, or via header. Ideally from
    // SecurityContext.
    @PostMapping
    @Operation(summary = "Enregistrer une participation", description = "Enregistrer son résultat quotidien pour un DLE")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Participation enregistrée avec succès"),
            @ApiResponse(responseCode = "400", description = "Participation déjà effectuée aujourd'hui")
    })
    public ResponseEntity<StreakDTO> submitParticipation(
            @Parameter(description = "ID de l'utilisateur", required = true) @RequestParam Integer userId,
            @Valid @RequestBody ParticipationSubmitDTO dto) {
        return ResponseEntity.ok(participationService.submitParticipation(userId, dto));
    }
}
