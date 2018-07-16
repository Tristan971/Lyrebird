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

package moe.lyrebird.model.systemtray;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

final class OnMouseClickListener implements MouseListener {

    private final Runnable onClick;

    OnMouseClickListener(final Runnable onClick) {
        this.onClick = onClick;
    }

    @Override
    public void mouseClicked(final MouseEvent e) {
        onClick.run();
    }

    @Override
    public void mousePressed(final MouseEvent e) {
        // ignore
    }

    @Override
    public void mouseReleased(final MouseEvent e) {
        // ignore
    }

    @Override
    public void mouseEntered(final MouseEvent e) {
        // ignore
    }

    @Override
    public void mouseExited(final MouseEvent e) {
        // ignore
    }

}
