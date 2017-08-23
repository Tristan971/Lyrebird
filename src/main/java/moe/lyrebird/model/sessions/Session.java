package moe.lyrebird.model.sessions;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import moe.lyrebird.model.twitter4j.TwitterHandler;
import org.hibernate.annotations.Columns;
import twitter4j.auth.AccessToken;

import javax.persistence.Column;
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
@AllArgsConstructor
@NoArgsConstructor
public class Session implements Serializable {
    
    private static final long serialVersionUID = -9038797949832585362L;
    
    @Id
    private String userId;

    @Columns(columns = {
            @Column(length = 1000, name = "access_token"),
    })
    private AccessToken accessToken;
}
