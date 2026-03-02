package betterdle.api.user.controller;

import betterdle.api.user.dto.UserProfileDTO;
import betterdle.api.user.dto.UserPreferencesUpdateDTO;
import betterdle.api.user.dto.UserUpdateDTO;
import betterdle.api.user.service.UserService;
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
@RequestMapping("/api/v1/users")
@Tag(name = "Utilisateurs", description = "Endpoints pour gérer le profil, le compte et les préférences d'un utilisateur")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer le profil public", description = "Consulter le profil public d’un joueur")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profil récupéré avec succès"),
            @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé")
    })
    public ResponseEntity<UserProfileDTO> getProfile(
            @Parameter(description = "ID de l'utilisateur", required = true) @PathVariable Integer id) {
        return ResponseEntity.ok(userService.getUserProfile(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour le profil", description = "Modifier ses informations de profil (username, email)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profil mis à jour avec succès"),
            @ApiResponse(responseCode = "400", description = "Données invalides (ex: nom d'utilisateur existant)"),
            @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé")
    })
    public ResponseEntity<UserProfileDTO> updateProfile(
            @Parameter(description = "ID de l'utilisateur", required = true) @PathVariable Integer id,
            @Valid @RequestBody UserUpdateDTO dto) {
        return ResponseEntity.ok(userService.updateProfile(id, dto));
    }

    @PutMapping("/{id}/preferences")
    @Operation(summary = "Mettre à jour les préférences", description = "Modifier ses préférences (langue, thème)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Préférences mises à jour avec succès"),
            @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé")
    })
    public ResponseEntity<UserProfileDTO> updatePreferences(
            @Parameter(description = "ID de l'utilisateur", required = true) @PathVariable Integer id,
            @Valid @RequestBody UserPreferencesUpdateDTO dto) {
        return ResponseEntity.ok(userService.updatePreferences(id, dto));
    }
}
