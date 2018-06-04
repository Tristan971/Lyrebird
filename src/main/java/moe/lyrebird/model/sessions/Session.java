package moe.lyrebird.model.sessions;

import moe.lyrebird.model.twitter.twitter4j.TwitterHandler;
import twitter4j.auth.AccessToken;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.Objects;

/**
 * A session represents one {@link AccessToken} and the corresponding user id, which is the primary key.
 * <p>
 * It is storable and can be retrieved to construct a {@link TwitterHandler instance}.
 */
@Entity
public class Session implements Serializable {

    private static final long serialVersionUID = -9038797949832585362L;

    @Id
    private String userId;

    @Column(length = 1000, name = "access_token")
    private AccessToken accessToken;

    @Transient
    private transient TwitterHandler twitterHandler;

    public Session() {}

    public Session(final String userId, final AccessToken accessToken, final TwitterHandler twitterHandler) {
        this.userId = userId;
        this.accessToken = accessToken;
        this.twitterHandler = twitterHandler;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(final String userId) {
        this.userId = userId;
    }

    public AccessToken getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(final AccessToken accessToken) {
        this.accessToken = accessToken;
    }

    public TwitterHandler getTwitterHandler() {
        return twitterHandler;
    }

    public void setTwitterHandler(final TwitterHandler twitterHandler) {
        this.twitterHandler = twitterHandler;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof Session)) return false;
        final Session session = (Session) o;
        return Objects.equals(userId, session.userId) &&
                Objects.equals(accessToken, session.accessToken) &&
                Objects.equals(twitterHandler, session.twitterHandler);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, accessToken, twitterHandler);
    }

    @Override
    public String toString() {
        return "Session{" +
                "userId='" + userId + '\'' +
                ", accessToken=" + accessToken +
                '}';
    }
}
