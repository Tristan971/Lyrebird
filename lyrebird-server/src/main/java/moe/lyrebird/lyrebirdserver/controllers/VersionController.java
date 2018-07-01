package moe.lyrebird.lyrebirdserver.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import moe.lyrebird.lyrebirdserver.model.objects.LyrebirdVersion;
import moe.lyrebird.lyrebirdserver.model.VersionService;

@Controller
@RequestMapping("versions")
public class VersionController {

    private final VersionService versionService;

    @Autowired
    public VersionController(VersionService versionService) {
        this.versionService = versionService;
    }

    @GetMapping("latest")
    public LyrebirdVersion getLatestVersion() {
        return versionService.getLatestVersion();
    }

}
