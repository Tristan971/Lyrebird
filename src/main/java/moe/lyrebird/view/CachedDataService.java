package moe.lyrebird.view;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import twitter4j.User;

import javafx.scene.image.Image;

@Component
public class CachedDataService {

    private static final Logger LOG = LoggerFactory.getLogger(CachedDataService.class);

    @Cacheable("userProfileImage")
    public Image userProfileImage(final User user) {
        LOG.debug("First load of user pp for user : @{}", user.getScreenName());
        return new Image(user.getBiggerProfileImageURLHttps());
    }

}
