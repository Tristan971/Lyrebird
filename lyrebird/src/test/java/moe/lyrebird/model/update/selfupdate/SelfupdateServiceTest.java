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

package moe.lyrebird.model.update.selfupdate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import moe.lyrebird.api.client.LyrebirdServerClient;
import moe.lyrebird.api.model.LyrebirdVersion;
import org.junit.Test;
import org.junit.runner.RunWith;

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
