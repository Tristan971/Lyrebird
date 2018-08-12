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

package moe.lyrebird.view.viewmodel.javafx;

import java.util.function.Consumer;

import javafx.scene.text.Text;

public final class ClickableText extends Text {

    public <T> ClickableText(final T element, final Consumer<T> onClicked) {
        super(String.valueOf(element));
        setOnMouseClicked(e -> onClicked.accept(element));
        getStyleClass().setAll("clickable-hyperlink");
    }

    public <T> ClickableText(final T element, final Runnable onClicked) {
        super(String.valueOf(element));
        setOnMouseClicked(e -> onClicked.run());
        getStyleClass().setAll("clickable-hyperlink");
    }

}
