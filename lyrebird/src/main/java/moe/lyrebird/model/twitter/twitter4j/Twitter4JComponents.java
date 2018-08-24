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

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;

/**
 * Configuration class for Twitter4J related components.
 */
@Lazy
@Configuration
public class Twitter4JComponents {

    /**
     * @param environment The Spring environment to fetch authentication keys
     * @return this application's configuration for connecting to the Twitter API
     */
    @Bean
    public twitter4j.conf.Configuration configuration(final Environment environment) {
        final ConfigurationBuilder cb = new ConfigurationBuilder();
        final String consumerKey = environment.getProperty("twitter.consumerKey");
        final String consumerSecret = environment.getProperty("twitter.consumerSecret");
        cb.setOAuthConsumerSecret(consumerSecret);
        cb.setOAuthConsumerKey(consumerKey);
        cb.setTweetModeExtended(true);
        return cb.build();
    }

    /**
     * @param configuration this application's configuration
     * @return a configured TwitterFactory to generate twitter instances
     */
    @Bean
    public TwitterFactory twitterFactory(final twitter4j.conf.Configuration configuration) {
        return new TwitterFactory(configuration);
    }

    /**
     * @param factory the TwitterFactory to use for configuration
     * @return an instance of the {@link Twitter} class that is configured with Lyrebird's credentials.
     */
    @Bean
    @Scope(value = SCOPE_PROTOTYPE)
    public Twitter twitter(final TwitterFactory factory) {
        return factory.getInstance();
    }

}
