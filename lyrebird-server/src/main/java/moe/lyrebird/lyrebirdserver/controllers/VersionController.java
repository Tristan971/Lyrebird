package moe.lyrebird.lyrebirdserver.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import moe.lyrebird.lyrebirdserver.model.VersionService;
import moe.lyrebird.lyrebirdserver.model.objects.LyrebirdVersion;

import java.net.MalformedURLException;

@RestController
@RequestMapping("versions")
public class VersionController {

    private final VersionService versionService;

    @Autowired
    public VersionController(VersionService versionService) {
        this.versionService = versionService;
    }

    @GetMapping(value = "latest", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public LyrebirdVersion getLatestVersion() throws MalformedURLException {
        return versionService.getLatestVersion();
    }

}
