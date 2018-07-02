package moe.lyrebird.api.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import moe.lyrebird.api.server.controllers.Endpoints;
import moe.lyrebird.api.server.model.objects.LyrebirdVersion;

@Component
public class LyrebirdServerClient {

    private final RestTemplate restTemplate;
    private final String apiUrl;

    @Autowired
    public LyrebirdServerClient(final RestTemplate restTemplate, @Value("${api.url}") final String apiUrl) {
        this.restTemplate = restTemplate;
        this.apiUrl = apiUrl;
    }

    public LyrebirdVersion getLatestVersion() {
        return restTemplate.getForEntity(
                buildUrl(Endpoints.VERSIONS_CONTROLLER, Endpoints.VERSIONS_LATEST),
                LyrebirdVersion.class
        ).getBody();
    }

    private String buildUrl(final String controller, final String method) {
        return apiUrl + "/" + controller + "/" + method;
    }

}
