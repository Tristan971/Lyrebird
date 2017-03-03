package moe.lyrebird.model.twitter4j;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import twitter4j.conf.Configuration;

import javax.persistence.Id;

@Data
@Builder
@AllArgsConstructor
public class TwitterConfiguration {
    @Id
    @NonNull
    private Long uid;

    @NonNull
    private Configuration configuration;
}
