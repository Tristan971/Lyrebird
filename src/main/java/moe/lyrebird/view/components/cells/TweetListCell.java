package moe.lyrebird.view.components.cells;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import moe.tristan.easyfxml.EasyFxml;
import moe.tristan.easyfxml.model.components.listview.ComponentListCell;
import moe.lyrebird.view.components.Components;
import twitter4j.Status;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;

@Component
@Scope(scopeName = SCOPE_PROTOTYPE)
public class TweetListCell extends ComponentListCell<Status> {

    public TweetListCell(final EasyFxml easyFxml) {
        super(easyFxml, Components.TWEET);
    }

}
