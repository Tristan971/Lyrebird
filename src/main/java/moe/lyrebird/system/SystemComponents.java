package moe.lyrebird.system;

import moe.lyrebird.model.threading.BackgroundService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by Tristan on 12/03/2017.
 */
@Configuration
public class SystemComponents {
    @Bean
    public CleanupService cleanupService() {
        return new CleanupService();
    }
    
    @Bean
    @Autowired
    public SystemIntegration systemIntegration(final BackgroundService backgroundService) {
        return new SystemIntegration(backgroundService);
    }
}
