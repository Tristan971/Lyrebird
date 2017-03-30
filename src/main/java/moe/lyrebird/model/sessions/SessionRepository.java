package moe.lyrebird.model.sessions;

import org.springframework.data.repository.CrudRepository;

/**
 * This class is the interface of the DAO of the application.
 * It is used to store {@link Session}s with their credentials
 * in the database.
 */
public interface SessionRepository extends CrudRepository<Session, String> {
}
