package moe.lyrebird.model.sessions;

import lombok.extern.slf4j.Slf4j;
import moe.lyrebird.model.twitter4j.TwitterHandler;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import twitter4j.Twitter;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * The session manager is responsible for persisting the sessions in database
 * and providing handles to them should another component need access to them
 * (i.e. the JavaFX controllers per example).
 */
@Slf4j
public class SessionManager implements ApplicationListener<ContextClosedEvent> {
    
    private final ApplicationContext context;
    private final SessionRepository sessionRepository;
    private final Map<Session, TwitterHandler> currentSessions;
    
    public SessionManager(final ApplicationContext context, final SessionRepository sessionRepository) {
        log.info("Started the twitter session manager.");
        this.sessionRepository = sessionRepository;
        this.context = context;
        this.currentSessions = new ConcurrentHashMap<>(1);
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
    public Optional<TwitterHandler> getTwitter(final Session session) {
        return Optional.ofNullable(this.currentSessions.get(session));
    }
    
    /**
     * Loads all sessions in memory.
     * TODO : autoload the {@link Twitter} for each.
     *
     * @return The number of new sessions loaded
     */
    @PostConstruct
    private long loadAllSessions() {
        final long initialSize = this.currentSessions.size();
        
        this.sessionRepository.findAll().forEach(session -> {
            this.currentSessions.putIfAbsent(session, this.context.getBean(TwitterHandler.class));
        });
        
        final long finalSize = this.currentSessions.size();
    
        final List<String> sessionUsernames = this.currentSessions.keySet().stream()
                .map(Session::getUserHandle)
                .collect(Collectors.toList());
        
        log.info(
                "Loaded {} Twitter sessions. Total loaded sessions so far is : {} {}",
                finalSize - initialSize,
                finalSize,
                sessionUsernames
        );
    
        return finalSize - initialSize;
    }
    
    public long reloadAllSessions() {
        // "force" kill all references to avoid memory leaks
        this.currentSessions.forEach((s, t) -> this.currentSessions.remove(s));
        return this.loadAllSessions();
    }
    
    public void addSession(final Session session) {
        this.currentSessions.put(session, this.context.getBean(TwitterHandler.class));
    }
    
    /**
     * Saves all sessions.
     * Do not use bulk-save as it crashes under null reference saving.
     * We want to make absolutely sure to never corrupt the database.
     */
    public void saveAllSessions() {
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
        log.info("Received application shutdown signal. Saving all sessions.");
        this.saveAllSessions();
    }
}
