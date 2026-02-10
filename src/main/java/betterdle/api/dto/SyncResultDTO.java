package betterdle.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * DTO de réponse pour les résultats de synchronisation.
 */
@Data
@AllArgsConstructor
public class SyncResultDTO {
    private int totalChampions;
    private int complete;
    private int incomplete;
    private String lastSync; // ISO 8601 timestamp
    private String currentLoLVersion;
}
