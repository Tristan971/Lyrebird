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

package moe.lyrebird.model.twitter.twitter4j;

import org.springframework.beans.factory.annotation.Autowired;
import io.vavr.CheckedFunction0;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.control.Try;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import twitter4a.Twitter;
import twitter4a.auth.AccessToken;
import twitter4a.auth.RequestToken;

import java.net.URL;
import java.util.Optional;

import static io.vavr.API.unchecked;

/**
 * This class is a decorator around the {@link Twitter} class.
 */
public class TwitterHandler {

    private static final Logger LOG = LoggerFactory.getLogger(TwitterHandler.class);

    private static final AccessToken FAKE_ACCESS_TOKEN = new AccessToken("fake", "token");

    private final Twitter twitter;

    private AccessToken accessToken = FAKE_ACCESS_TOKEN;

    @Autowired
    public TwitterHandler(final Twitter twitter) {
        this.twitter = twitter;
    }

    public Twitter getTwitter() {
        return twitter;
    }

    public AccessToken getAccessToken() {
        return accessToken;
    }

    /**
     * Creates a user authentication request on Twitter side
     *
     * @return a tuple containing the URL for OTP, and the RequestToken to which this OTP will be bound to
     */
    public Tuple2<URL, RequestToken> newSession() {
        LOG.info("Requesting new Session!");
        final RequestToken requestToken = unchecked((CheckedFunction0<RequestToken>) this.twitter::getOAuthRequestToken)
                .apply();
        LOG.info("Got request token : {}", requestToken);
        return Tuple.of(
                unchecked((CheckedFunction0<URL>) (() -> new URL(requestToken.getAuthorizationURL()))).apply(),
                requestToken
        );
    }

    /**
     * Tries registering a RequestToken with a given OTP to fetch a persistable AccessToken authenticating the current
     * user to Twitter through Lyrebird.
     *
     * @param requestToken The request token of the authentication request
     * @param pinCode      The OTP bound to this request token
     *
     * @return An optional containing the resulting {@link AccessToken} if the authentication was successful.
     */
    public Optional<AccessToken> registerAccessToken(final RequestToken requestToken, final String pinCode) {
        LOG.info("Registering token {} with pincode {}", requestToken, pinCode);

        final Try<AccessToken> tryAccessToken = Try.of(
                () -> this.twitter.getOAuthAccessToken(requestToken, pinCode)
        );

        if (tryAccessToken.isFailure()) {
            LOG.info("Could not get access token! An error was thrown!");
            return Optional.empty();
        }


        final AccessToken successAccessToken = tryAccessToken.get();
        this.twitter.setOAuthAccessToken(successAccessToken);
        LOG.info(
                "Successfully got access token for user @{}! {}",
                successAccessToken.getScreenName(),
                successAccessToken
        );
        this.accessToken = successAccessToken;
        return Optional.of(successAccessToken);
    }

    /**
     * Loads up the underlying Twitter instance with a given {@link AccessToken}. Useful for multiple account
     * management.
     *
     * @param preloadedAccessToken The previously saved {@link AccessToken}.
     *
     * @see #registerAccessToken(RequestToken, String)
     */
    public void registerAccessToken(final AccessToken preloadedAccessToken) {
        this.accessToken = preloadedAccessToken;
        this.twitter.setOAuthAccessToken(preloadedAccessToken);
    }

}
