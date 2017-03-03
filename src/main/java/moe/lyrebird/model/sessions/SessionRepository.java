package moe.lyrebird.model.sessions;

import org.springframework.data.repository.CrudRepository;

/**
 * This class is the interface of the DAO of the application.
 * It is used to store {@link Session}s with their credentials
 * in the database.
 */
public interface SessionRepository extends CrudRepository<Session, String> {
    
    /**
     * Saves a session to the repository.
     *
     * @param session
     *         The session to save
     * @param <S>
     *         {@link Session} or a subtype of it.
     * @return The session you just saved.
     */
    <S extends Session> S save(final S session);
    
    /**
     * Gets a session from the repo using the UID.
     *
     * @param uid
     *         The session's UID.
     * @return The session.
     */
    Session findOne(final String uid);
    
    /**
     * Finds all the saved sessions.
     *
     * @return The saved sessions
     */
    @Override
    Iterable<Session> findAll();
    
    /**
     * Checks if a session exists in the repository (from the UID).
     *
     * @param uid
     *         The uid of the session looked for
     * @return Whether it exists or not.
     */
    boolean exists(final String uid);
    
    /**
     * Deletes a session from the repo.
     *
     * @param uid
     *         The ID of the session to be deleted.
     */
    void delete(final String uid);
}
