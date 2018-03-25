package moe.lyrebird.view;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import moe.tristan.easyfxml.EasyFxml;
import moe.tristan.easyfxml.api.FxmlNode;
import moe.tristan.easyfxml.api.FxmlStylesheet;
import moe.tristan.easyfxml.spring.application.FxUiManager;
import lombok.extern.slf4j.Slf4j;
import moe.lyrebird.view.views.Styles;
import moe.lyrebird.view.views.Views;

import java.util.Optional;

/**
 * The {@link LyrebirdUiManager} is responsible for bootstraping the GUI of the application correctly.
 */
@Slf4j
@Component
public class LyrebirdUiManager extends FxUiManager {

    private final String version;

    @Autowired
    public LyrebirdUiManager(final EasyFxml easyFxml, final Environment environment) {
        super(easyFxml);
        this.version = environment.getProperty("app.version");
    }

    @Override
    protected String getTitle() {
        return String.format(
                "%s [%s]",
                "Lyrebird",
                version
        );
    }

    @Override
    protected FxmlNode getMainScene() {
        return Views.ROOT_VIEW;
    }

    @Override
    protected Optional<FxmlStylesheet> getStylesheet() {
        return Optional.of(Styles.LYREBIRD);
    }

}
