package tasks;

import com.dreamGames.rowMatchBackend.service.TournamentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class TournamentRestartTask {
    private final TournamentService tournamentService;

    public TournamentRestartTask (final TournamentService tournamentService) {
        this.tournamentService = tournamentService;
    }

    @EventListener(ApplicationReadyEvent.class)
    @Scheduled(cron = "0 20 * * * ?")
    public void startTournament() {
        tournamentService.startTournament();
    }
}
