package betterdle.api.user.controller;

import betterdle.api.user.service.ModerationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin")
@Tag(name = "Administration", description = "Endpoints d'administration pour la gestion des utilisateurs et la modération")
@RequiredArgsConstructor
public class AdminController {

    private final ModerationService moderationService;

    @PostMapping("/users/{userId}/ban")
    @Operation(summary = "Bannir un utilisateur", description = "Bannir définitivement un utilisateur")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Utilisateur banni avec succès"),
            @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé")
    })
    public ResponseEntity<Void> banUser(
            @Parameter(description = "ID de l'administrateur effectuant l'action", required = true) @RequestParam Integer adminId,
            @Parameter(description = "ID de l'utilisateur à bannir", required = true) @PathVariable Integer userId,
            @Parameter(description = "Raison du bannissement", required = true) @RequestParam String reason) {
        moderationService.banUser(adminId, userId, reason);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/users/{userId}/suspend")
    @Operation(summary = "Suspendre un utilisateur", description = "Suspendre temporairement un utilisateur")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Utilisateur suspendu avec succès"),
            @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé")
    })
    public ResponseEntity<Void> suspendUser(
            @Parameter(description = "ID de l'administrateur effectuant l'action", required = true) @RequestParam Integer adminId,
            @Parameter(description = "ID de l'utilisateur à suspendre", required = true) @PathVariable Integer userId,
            @Parameter(description = "Raison de la suspension", required = true) @RequestParam String reason) {
        moderationService.suspendUser(adminId, userId, reason);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/participations/{participationId}")
    @Operation(summary = "Supprimer une participation", description = "Supprimer une participation quotidienne")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Participation supprimée avec succès"),
            @ApiResponse(responseCode = "404", description = "Participation non trouvée")
    })
    public ResponseEntity<Void> deleteParticipation(
            @Parameter(description = "ID de l'administrateur effectuant l'action", required = true) @RequestParam Integer adminId,
            @Parameter(description = "ID de la participation à supprimer", required = true) @PathVariable Integer participationId,
            @Parameter(description = "Raison de la suppression", required = true) @RequestParam String reason) {
        moderationService.deleteParticipation(adminId, participationId, reason);
        return ResponseEntity.ok().build();
    }
}
