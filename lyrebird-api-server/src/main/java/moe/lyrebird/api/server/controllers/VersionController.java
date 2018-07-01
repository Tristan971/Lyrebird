package moe.lyrebird.api.server.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import moe.lyrebird.api.server.model.VersionService;
import moe.lyrebird.api.server.model.objects.LyrebirdVersion;

import java.net.MalformedURLException;

@RestController
@RequestMapping(Endpoints.VERSIONS_CONTROLLER)
public class VersionController {

    private final VersionService versionService;

    @Autowired
    public VersionController(VersionService versionService) {
        this.versionService = versionService;
    }

    @GetMapping(value = Endpoints.VERSIONS_LATEST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public LyrebirdVersion getLatestVersion() throws MalformedURLException {
        return versionService.getLatestVersion();
    }

}
