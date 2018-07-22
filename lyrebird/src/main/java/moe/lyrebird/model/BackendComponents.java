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

package moe.lyrebird.model;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import moe.lyrebird.model.sessions.SessionManager;
import moe.lyrebird.model.sessions.SessionRepository;
import moe.lyrebird.model.twitter.twitter4j.Twitter4JComponents;
import moe.lyrebird.model.twitter.twitter4j.TwitterHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import twitter4j.Twitter;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;

/**
 * Back-end (Twitter, persistence etc.) components go here.
 * <p>
 * For Twitter4J wrapping see {@link Twitter4JComponents}
 */
@Configuration
public class BackendComponents {

    private static final Logger LOG = LoggerFactory.getLogger(BackendComponents.class);

    @Bean
    @Lazy
    @Scope(scopeName = SCOPE_PROTOTYPE)
    public TwitterHandler twitterHandler(final Twitter twitter) {
        return new TwitterHandler(twitter);
    }

    @Bean
    public SessionManager sessionManager(final ApplicationContext context, final SessionRepository sessionRepository) {
        final SessionManager sessionManager = new SessionManager(context, sessionRepository);
        final long loadedSessions = sessionManager.loadAllSessions();
        LOG.info("Loaded {} previously saved sessions.", loadedSessions);
        return sessionManager;
    }

    /**
     * @return The Jackson object mapper used for deserialization
     */
    @Lazy
    @Bean
    public ObjectMapper objectMapper() {
        final ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(
                DeserializationFeature.FAIL_ON_NULL_CREATOR_PROPERTIES,
                DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
                DeserializationFeature.FAIL_ON_TRAILING_TOKENS
        );
        return objectMapper;
    }

}
