package moe.lyrebird.model.sessions;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import io.vavr.control.Option;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import moe.lyrebird.model.twitter4j.TwitterHandler;
import twitter4j.auth.AccessToken;

import javax.annotation.PostConstruct;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * The session manager is responsible for persisting the sessions in database and providing handles to them should
 * another component need access to them (i.e. the JavaFX controllers per example).
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SessionManager {

    private final ApplicationContext context;
    private final SessionRepository sessionRepository;

    private final Set<Session> loadedSessions = new HashSet<>();

    @Getter
    @Setter
    private Option<Session> currentSession = Option.none();

    public Optional<Session> getSessionForUser(final String userId) {
        return this.loadedSessions.stream()
                                  .filter(session -> session.getUserId().equals(userId))
                                  .findFirst();
    }

    /**
     * Loads all sessions stored in database. For each of them it will also create the respective {@link
     * TwitterHandler}.
     *
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
                "Loaded {} Twitter sessions. Total loaded sessions so far is {}. Sessions : {}",
                finalSize - initialSize,
                finalSize,
                sessionUsernames
        );

        return finalSize - initialSize;
    }

    public void reloadAllSessions() {
        this.loadedSessions.clear();
        this.loadAllSessions();
    }

    public void loadSession(final Session session) {
        final TwitterHandler handler = this.context.getBean(TwitterHandler.class);
        handler.registerAccessToken(session.getAccessToken());
        session.setTwitterHandler(handler);
        this.loadedSessions.add(session);
        this.setCurrentSession(Option.of(session));
    }

    public void addNewSession(final TwitterHandler twitterHandler) {
        final AccessToken accessToken = twitterHandler.getAccessToken();
        final Session session = new Session(
                accessToken.getScreenName(),
                accessToken,
                twitterHandler
        );

        this.loadSession(session);
        this.setCurrentSession(Option.of(session));
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
