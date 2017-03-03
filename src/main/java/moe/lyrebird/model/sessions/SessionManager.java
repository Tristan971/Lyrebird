package moe.lyrebird.model.sessions;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import moe.lyrebird.model.twitter4j.TwitterHandler;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import twitter4j.auth.AccessToken;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * The session manager is responsible for persisting the sessions in database
 * and providing handles to them should another component need access to them
 * (i.e. the JavaFX controllers per example).
 */
@Slf4j
@Data
public class SessionManager implements ApplicationListener<ContextClosedEvent> {

    @Getter(AccessLevel.NONE)
    private final ApplicationContext context;

    @Getter(AccessLevel.NONE)
    private final SessionRepository sessionRepository;

    private Session currentSession;
    private final Map<Session, TwitterHandler> loadedSessions;
    
    public SessionManager(final ApplicationContext context, final SessionRepository sessionRepository) {
        this.context = context;

        log.info("Started the twitter session manager!");
        this.sessionRepository = sessionRepository;
        log.info("Loaded the session repository : {}", sessionRepository.toString());
        this.loadedSessions = new ConcurrentHashMap<>(1);
    }
    
    public Optional<Session> getSession(final String userId) {
        return this.loadedSessions.keySet().stream()
                .filter(session -> session.getUserId().equals(userId))
                .findFirst();
    }
    
    /**
     * Returns the Twitter instance associated to a session if it exists.
     *
     * @param session
     *         The session which's {@link TwitterHandler} we look for.
     * @return The potential TwitterHandler for this session.
     */
    public Optional<TwitterHandler> getTwitterHandler(final Session session) {
        return Optional.ofNullable(this.loadedSessions.get(session));
    }
    
    /**
     * Loads all sessions stored in database. For each of them it will also create
     * the respective {@link TwitterHandler}.
     * @return The number of new sessions loaded
     */
    @PostConstruct
    private long loadAllSessions() {
        final long initialSize = this.loadedSessions.size();
        
        this.sessionRepository.findAll().forEach(this::addSession);
        
        final long finalSize = this.loadedSessions.size();
    
        final List<String> sessionUsernames = this.loadedSessions.keySet().stream()
                .map(Session::getUserId)
                .collect(Collectors.toList());
        
        log.info(
                "Loaded {} new Twitter sessions. " +
                        "Total loaded sessions so far is {} : {}",
                finalSize - initialSize,
                finalSize,
                sessionUsernames
        );
    
        return finalSize - initialSize;
    }

    @SuppressWarnings("UnusedReturnValue")
    public long reloadAllSessions() {
        // "force" kill all references to avoid memory leaks
        this.loadedSessions.forEach((s, t) -> this.loadedSessions.remove(s));
        return this.loadAllSessions();
    }
    
    public void addSession(final Session session) {
        final TwitterHandler handler = this.context.getBean(TwitterHandler.class);
        handler.setAccessToken(session.getAccessToken());
        this.loadedSessions.put(session, handler);
        this.setCurrentSession(session);
    }

    public void addTwitterHandler(final TwitterHandler twitterHandler) {
        final AccessToken accessToken = twitterHandler.getAccessToken();
        final Session session = Session.builder()
                .userId(accessToken.getScreenName())
                .accessToken(accessToken)
                .build();

        this.loadedSessions.put(session, twitterHandler);
        this.setCurrentSession(session);
    }
    
    /**
     * Saves all sessions.
     * Do not use bulk-save as it crashes under null reference saving.
     * We want to make absolutely sure to never corrupt the database.
     */
    @SuppressWarnings("UseBulkOperation")
    public void saveAllSessions() {
        this.loadedSessions.keySet().forEach(this.sessionRepository::save);
        log.info("Saving sessions {}", this.loadedSessions.keySet().toString());
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
