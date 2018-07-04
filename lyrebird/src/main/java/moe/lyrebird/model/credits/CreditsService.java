/*
 *     Lyrebird, a free open-source cross-platform twitter client.
 *     Copyright (C) 2017-2018, Tristan Deloche
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package moe.lyrebird.model.credits;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.vavr.collection.Stream;
import io.vavr.control.Try;
import moe.lyrebird.model.credits.objects.CredittedWork;

import javafx.collections.ObservableList;

import java.util.List;

import static io.vavr.API.unchecked;
import static javafx.collections.FXCollections.observableList;
import static javafx.collections.FXCollections.unmodifiableObservableList;

@Lazy
@Component
public class CreditsService {

    private static final String CREDITS_RESOURCES_PATH = "classpath:assets/credits/*.json";

    private final ObservableList<CredittedWork> credittedWorks;

    @Autowired
    public CreditsService(ObjectMapper objectMapper) {
        this.credittedWorks = unmodifiableObservableList(observableList(loadCreditsFiles(
                objectMapper,
                new PathMatchingResourcePatternResolver()
        )));
    }

    private List<CredittedWork> loadCreditsFiles(
            final ObjectMapper objectMapper,
            final PathMatchingResourcePatternResolver pmpr
    ) {
        return Try.of(() -> pmpr.getResources(CREDITS_RESOURCES_PATH))
                  .toStream()
                  .flatMap(Stream::of)
                  .map(unchecked(Resource::getInputStream))
                  .map(unchecked(cis -> objectMapper.readValue(cis, CredittedWork.class)))
                  .toJavaList();
    }

    public ObservableList<CredittedWork> creditedWorks() {
        return credittedWorks;
    }

}
