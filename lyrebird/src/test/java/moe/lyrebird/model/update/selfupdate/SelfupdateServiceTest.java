package moe.lyrebird.model.update.selfupdate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import moe.lyrebird.api.client.LyrebirdServerClient;
import moe.lyrebird.api.server.model.objects.LyrebirdVersion;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

@SpringBootTest
@RunWith(SpringRunner.class)
public class SelfupdateServiceTest {

    @Autowired
    private LyrebirdServerClient client;

    @Autowired
    private SelfupdateService selfupdateService;

    @Test
    public void selfupdate() {
        final LyrebirdVersion lastVersion = client.getLatestVersion();
        selfupdateService.selfupdate(lastVersion);
    }
}
