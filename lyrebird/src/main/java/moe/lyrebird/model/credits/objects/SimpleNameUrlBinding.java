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

package moe.lyrebird.model.credits.objects;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;

/**
 * Simple helper base for fields of {@link CredittedWork} since they all have in common a name and a related URL.
 *
 * @see CredittedWork
 */
public abstract class SimpleNameUrlBinding {

    @JsonProperty
    protected final String name;

    @JsonProperty
    protected final URL url;

    SimpleNameUrlBinding(final String name, final String url) throws MalformedURLException {
        this.name = name;
        this.url = new URL(url);
    }

    public String getName() {
        return name;
    }

    public URL getUrl() {
        return url;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SimpleNameUrlBinding)) {
            return false;
        }
        final SimpleNameUrlBinding that = (SimpleNameUrlBinding) o;
        return Objects.equals(getName(), that.getName()) &&
               Objects.equals(getUrl(), that.getUrl());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getUrl());
    }

}
