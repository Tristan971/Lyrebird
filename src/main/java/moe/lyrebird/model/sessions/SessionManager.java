package moe.lyrebird.model.sessions;

import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import moe.lyrebird.lang.collections.MapUtils;
import moe.lyrebird.model.twitter4j.TwitterHandler;
import org.springframework.context.ApplicationContext;
import twitter4j.auth.AccessToken;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static lombok.AccessLevel.NONE;

/**
 * The session manager is responsible for persisting the sessions in database
 * and providing handles to them should another component need access to them
 * (i.e. the JavaFX controllers per example).
 */
@Slf4j
@Data
public class SessionManager {

    @Getter(NONE)
    private final ApplicationContext context;

    @Getter(NONE)
    private final SessionRepository sessionRepository;

    private final Map<Session, TwitterHandler> loadedSessions;
    private Entry<Session, TwitterHandler> currentSession;
    
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
        this.setCurrentSession(MapUtils.entryFor(session, this.loadedSessions));
        this.saveAllSessions();
    }

    public void addTwitterHandler(final TwitterHandler twitterHandler) {
        final AccessToken accessToken = twitterHandler.getAccessToken();
        final Session session = Session.builder()
                .userId(accessToken.getScreenName())
                .accessToken(accessToken)
                .build();

        addSession(session);
        this.setCurrentSession(MapUtils.entryFor(session, this.loadedSessions));
    }
    
    /**
     * Saves all sessions.
     */
    public void saveAllSessions() {
        this.loadedSessions.keySet().forEach(this.sessionRepository::save);
        log.info("Saving sessions {}", this.loadedSessions.keySet().toString());
    }
}
