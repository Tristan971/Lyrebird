package moe.lyrebird.model.sessions;

import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import moe.lyrebird.model.twitter4j.TwitterHandler;
import org.springframework.context.ApplicationContext;
import twitter4j.auth.AccessToken;

import javax.annotation.PostConstruct;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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

    private final Set<Session> loadedSessions;
    private Session currentSession;
    
    public SessionManager(final ApplicationContext context, final SessionRepository sessionRepository) {
        this.context = context;

        log.info("Started the Twitter session manager!");
        this.sessionRepository = sessionRepository;
        this.loadedSessions = new HashSet<>(1);
    }
    
    public Optional<Session> getSessionForUser(final String userId) {
        return this.loadedSessions.stream()
                .filter(session -> session.getUserId().equals(userId))
                .findFirst();
    }
    
    /**
     * Loads all sessions stored in database. For each of them it will also create
     * the respective {@link TwitterHandler}.
     * @return The number of new sessions loaded
     */
    @PostConstruct
    private long loadAllSessions() {
        final long initialSize = this.loadedSessions.size();
        
        this.sessionRepository.findAll().forEach(this::loadSession);
        
        final long finalSize = this.loadedSessions.size();
    
        final List<String> sessionUsernames = this.loadedSessions.stream()
                .map(Session::getUserId)
                .collect(Collectors.toList());
        
        log.info(
                "Loaded {} Twitter sessions. " +
                        "Total loaded sessions so far is {} : {}",
                finalSize - initialSize,
                finalSize,
                sessionUsernames
        );

        return finalSize - initialSize;
    }

    public long reloadAllSessions() {
        this.loadedSessions.clear();
        return this.loadAllSessions();
    }
    
    public void loadSession(final Session session) {
        final TwitterHandler handler = this.context.getBean(TwitterHandler.class);
        handler.registerAccessToken(session.getAccessToken());
        session.setTwitterHandler(handler);
        this.loadedSessions.add(session);
        this.setCurrentSession(session);
    }

    public void addNewSession(final TwitterHandler twitterHandler) {
        final AccessToken accessToken = twitterHandler.getAccessToken();
        final Session session = new Session(
                accessToken.getScreenName(),
                accessToken,
                twitterHandler
        );

        this.loadSession(session);
        this.setCurrentSession(session);
        this.saveAllSessions();
    }
    
    /**
     * Saves all sessions.
     */
    public void saveAllSessions() {
        this.loadedSessions.stream()
                .peek(session -> log.info("Saving Twitter session : {}", session.toString()))
                .forEach(this.sessionRepository::save);
        log.debug("Saved all sessions !");
    }
}
