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

package moe.lyrebird.view.util;

import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

public final class Clipping {

    private Clipping() {
    }

    public static Rectangle getSquareClip(final double side, final double roundedRadius) {
        final Rectangle rectangle = new Rectangle();
        rectangle.setHeight(side);
        rectangle.setWidth(side);
        rectangle.setArcWidth(roundedRadius);
        rectangle.setArcHeight(roundedRadius);
        return rectangle;
    }

    public static Circle getCircleClip(final double radius) {
        return new Circle(radius);
    }

}
