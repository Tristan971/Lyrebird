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

package moe.lyrebird.model.update.system;

import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.SystemInfo;
import oshi.software.os.OSProcess;
import oshi.software.os.OperatingSystem;

@Component
public class DistribuableExecutionService {

    private static final Logger LOG = LoggerFactory.getLogger(DistribuableExecutionService.class);

    String[] getExecutionCommandLine() {
        final SystemInfo currentSystem = new SystemInfo();
        final OperatingSystem currentOs = currentSystem.getOperatingSystem();
        final OSProcess lyrebirdProcess = currentOs.getProcess(currentOs.getProcessId());
        LOG.debug(
                "Current process : [name = {}, id = {}, cmdLine = {}]",
                lyrebirdProcess.getName(),
                lyrebirdProcess.getProcessID(),
                lyrebirdProcess.getCommandLine()
        );
        return lyrebirdProcess.getCommandLine().split(" ");
    }

}
