package moe.lyrebird.api.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import moe.lyrebird.api.server.controllers.Endpoints;
import moe.lyrebird.api.server.model.objects.LyrebirdVersion;

@Component
public class LyrebirdServerClient {

    private static final String API_URL = "https://tristan.moe";

    private final RestTemplate restTemplate;

    @Autowired
    public LyrebirdServerClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public LyrebirdVersion getLatestVersion() {
        return restTemplate.getForEntity(
                buildUrl(Endpoints.VERSIONS_CONTROLLER, Endpoints.VERSIONS_LATEST),
                LyrebirdVersion.class
        ).getBody();
    }

    private String buildUrl(final String controller, final String method) {
        return API_URL + "/" + controller + "/" + method;
    }

}
