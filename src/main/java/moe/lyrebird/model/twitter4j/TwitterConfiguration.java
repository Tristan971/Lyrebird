package moe.lyrebird.model.twitter4j;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import twitter4j.conf.Configuration;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Wrapper that can be safely stored wvia spring jpa
 */
@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TwitterConfiguration {
    
    @Id
    private Long uid;
    
    private Configuration configuration;
    
    public TwitterConfiguration(final Configuration configuration) {
        this.uid = (long) this.hashCode();
        this.configuration = configuration;
    }
}
