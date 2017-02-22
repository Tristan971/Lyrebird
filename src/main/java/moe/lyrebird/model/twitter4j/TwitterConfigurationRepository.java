package moe.lyrebird.model.twitter4j;

import org.springframework.data.repository.CrudRepository;

/**
 * Created by Tristan on 21/02/2017.
 */
public interface TwitterConfigurationRepository extends CrudRepository<TwitterConfiguration, String> {
    <C extends TwitterConfiguration> C save(final C session);
    
    TwitterConfiguration findOne(final String associatedUserName);
    
    Iterable<TwitterConfiguration> findAll();
    
    boolean exists(final String associatedUserName);
    
    void delete(final String associatedUserName);
}
