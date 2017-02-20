package moe.lyrebird.model.sessions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import twitter4j.Twitter;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * The session manager is responsible for persisting the sessions in database
 * and providing handles to them should another component need access to them
 * (i.e. the JavaFX controllers per example).
 */
@Slf4j
public class SessionManager implements ApplicationListener<ContextClosedEvent> {
    
    private final SessionRepository sessionRepository;
    private final Map<Session, Twitter> currentSessions;
    
    public SessionManager(final SessionRepository sessionRepository) {
        log.info("Started the Twitter session manager service.");
        this.sessionRepository = sessionRepository;
        
        // For performance reasons we assume most users
        // only have 1 session
        this.currentSessions = new HashMap<>(1);
    }
    
    public Optional<Session> getSession(final String userHandle) {
        return this.currentSessions.keySet().stream()
                .filter(session -> session.getUserHandle().equals(userHandle))
                .findFirst();
    }
    
    /**
     * Returns the Twitter instance associated to a session if it exists.
     *
     * @param session
     *         The session which's {@link Twitter} we look for.
     * @return The optional representing an existing Twitter implementation,
     * or empty (was null in {@link #currentSessions}).
     */
    public Optional<Twitter> getTwitterForSession(final Session session) {
        return Optional.ofNullable(this.currentSessions.get(session));
    }
    
    /**
     * Loads all sessions in memory.
     * TODO : autoload the {@link Twitter} for each.
     *
     * @return The number of new sessions loaded
     */
    private long loadAllSessions() {
        final long initialSize = this.currentSessions.size();
        
        this.sessionRepository.findAll().forEach(session -> {
            this.currentSessions.putIfAbsent(session, null);
        });
        
        final long finalSize = this.currentSessions.size();
        
        log.info(
                "Loaded {} new sessions! Total loaded sessions is {}",
                finalSize - initialSize,
                finalSize
        );
        return finalSize - initialSize;
    }
    
    /**
     * Saves all sessions.
     * Do not use bulk-save as it crashes under null reference saving.
     * We want to make absolutely sure to never corrupt the database.
     */
    private void saveAllSessions() {
        this.currentSessions.keySet().stream()
                .filter(Objects::nonNull)
                .forEach(this.sessionRepository::save);
        log.info("Saving current state of Twitter sessions.");
    }
    
    /**
     * Saving all sessions on shutdown signal.
     *
     * @param event
     *         The shutdown event.
     */
    @Override
    public void onApplicationEvent(final ContextClosedEvent event) {
        this.saveAllSessions();
    }
}
