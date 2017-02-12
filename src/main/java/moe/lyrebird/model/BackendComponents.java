package moe.lyrebird.model;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import twitter4j.JSONObject;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * Back-end (Twitter, persistence etc.) components go here.
 */
@Configuration
public class BackendComponents {
    public TwitterFactory tf;

    public void testSetup() {
        ConfigurationBuilder cb = new ConfigurationBuilder();
        /*
            The consumer key secret needs to be obfuscated.
            The authentication token and secret will be received from an oauth request. See:
                https://dev.twitter.com/web/sign-in/implementing
            For testing it's being loaded from a text file: resources/config.txt
            the format is key=value with no spaces
         */
        String consumerKey = "";
        String consumerSecret = "";
        String accessToken = "";
        String accessTokenSecret = "";
        try {
            List<String> configLines = Files.readAllLines(Paths.get("/home/chris/IdeaProjects/Lyrebird/src/main/resources/config.txt"));
            for (String line : configLines) {
                String[] key_value = line.split("=");
                switch (key_value[0]) {
                    case "consumerKey": consumerKey = key_value[1];
                        break;
                    case "consumerSecret": consumerSecret = key_value[1];
                        break;
                    case "accessToken": accessToken = key_value[1];
                        break;
                    case "accessTokenSecret": accessTokenSecret = key_value[1];
                        break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        cb.setOAuthConsumerSecret(consumerSecret);
        cb.setOAuthConsumerKey(consumerKey);
        cb.setOAuthAccessTokenSecret(accessTokenSecret);
        cb.setOAuthAccessToken(accessToken);
        tf = new TwitterFactory(cb.build());
    }

    @Bean(name = "twitterHandler")
    public Twitter twitter() {
        testSetup();
        return tf.getInstance();
    }

}
