package betterdle.api.lol.model.enums;

public enum SyncStatus {
    DETECTED, // Found in DDragon version list, but no local data
    METADATA_SYNCED, // Database entity populated with stats/spells
    ASSETS_DOWNLOADED, // Images (icon, splash, loading) verified on disk
    READY, // Fully playable state
    INCOMPLETE // Something failed or is missing
}
