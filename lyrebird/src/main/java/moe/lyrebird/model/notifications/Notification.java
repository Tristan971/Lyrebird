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

package moe.lyrebird.model.notifications;

import java.util.Objects;

public final class Notification {

    private final String title;
    private final String text;

    public Notification(final String title, final String text) {
        this.title = title;
        this.text = text != null ? text : "";
    }

    @Override
    public int hashCode() {
        return Objects.hash(getTitle(), getText());
    }

    public String getTitle() {
        return title;
    }

    public String getText() {
        return text;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Notification)) {
            return false;
        }
        final Notification that = (Notification) o;
        return Objects.equals(getTitle(), that.getTitle()) &&
               Objects.equals(getText(), that.getText());
    }

    @Override
    public String toString() {
        return "Notification{" +
               "title='" + title + '\'' +
               ", text='" + text + '\'' +
               '}';
    }

}
