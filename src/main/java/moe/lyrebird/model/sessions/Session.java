package moe.lyrebird.model.sessions;

import lombok.*;
import moe.lyrebird.model.twitter4j.TwitterHandler;
import twitter4j.auth.AccessToken;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;

/**
 * A session represents one {@link AccessToken} and the corresponding
 * user id, which is the primary key.
 *
 * It is storable and can be retrieved to construct a {@link TwitterHandler instance}.
 */
@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Session implements Serializable {
    
    private static final long serialVersionUID = -9038797949832585362L;
    
    @Id
    @NonNull
    private String userId;

    @NonNull
    private AccessToken accessToken;
}
