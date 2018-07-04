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

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public final class CreditedWork {

    @JsonProperty
    private String creditedWorkTitle;

    @JsonProperty
    private String creditedWorkAuthor;

    public String getCreditedWorkTitle() {
        return creditedWorkTitle;
    }

    public String getCreditedWorkAuthor() {
        return creditedWorkAuthor;
    }

    @Override
    public String toString() {
        return "CreditedWork{" +
               "creditedWorkTitle='" + creditedWorkTitle + '\'' +
               ", creditedWorkAuthor='" + creditedWorkAuthor + '\'' +
               '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CreditedWork)) {
            return false;
        }
        CreditedWork that = (CreditedWork) o;
        return Objects.equals(getCreditedWorkTitle(), that.getCreditedWorkTitle()) &&
               Objects.equals(getCreditedWorkAuthor(), that.getCreditedWorkAuthor());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCreditedWorkTitle(), getCreditedWorkAuthor());
    }

}
