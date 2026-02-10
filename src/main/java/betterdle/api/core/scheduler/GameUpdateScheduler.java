package betterdle.api.core.scheduler;

import betterdle.api.core.factory.GameUpdaterFactory;
import betterdle.api.core.service.GameUpdater;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GameUpdateScheduler {

    private final GameUpdaterFactory updaterFactory;

    // Run at 4 AM every day
    @Scheduled(cron = "0 0 4 * * *")
    public void scheduleDailyUpdates() {
        System.out.println("Starting daily game updates check...");
        for (GameUpdater updater : updaterFactory.getAllUpdaters()) {
            updater.checkAndUpdate();
        }
    }
}
