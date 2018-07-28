/*
 *     Lyrebird, a free open-source cross-platform twitter client.
 *     Copyright (C) 2017-2018, Tristan Deloche
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package moe.lyrebird.model.sessions;

import io.vavr.control.Try;
import moe.lyrebird.model.twitter.twitter4j.TwitterHandler;
import twitter4a.User;
import twitter4a.auth.AccessToken;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.Objects;

/**
 * A session represents one {@link AccessToken} and the corresponding user id, which is the primary key.
 * <p>
 * It is serializable (and serialized) and can be retrieved to construct a {@link TwitterHandler instance}.
 * <p>
 * Unused warnings are disabled because setters need to be public for hibernate mapping to database.
 */
@SuppressWarnings("unused")
@Entity
public class Session implements Serializable {

    private static final long serialVersionUID = -9038797949832585362L;

    @Id
    private String userId;

    @Column(length = 1000, name = "access_token")
    private AccessToken accessToken;

    @Transient
    private transient TwitterHandler twitterHandler;

    public Session() {
    }

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

    public String getUserScreenName() {
        if (accessToken == null) {
            return "<NOT_LOGGED_IN>";
        }
        return accessToken.getScreenName();
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

    /**
     * @return the Twitter {@link User} associated with this Session.
     */
    public Try<User> getTwitterUser() {
        final long self = accessToken.getUserId();
        return Try.of(twitterHandler::getTwitter).mapTry(twitter -> twitter.showUser(self));
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Session)) {
            return false;
        }
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
