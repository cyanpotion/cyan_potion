/**
 * Copyright (C) 2020 XenoAmess
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.xenoamess.cyan_potion.civilization.character;

import lombok.Getter;

import java.util.Objects;

/**
 * Clan (宗族) represents a family lineage.
 * Each clan has a unique ID and a name (typically the family surname).
 *
 * @author XenoAmess
 * @version 0.167.3-SNAPSHOT
 */
public class Clan {

    @Getter
    private final String id;

    @Getter
    private final String name;

    @Getter
    private final String description;

    /**
     * Creates a new clan.
     *
     * @param id unique clan identifier
     * @param name clan name (typically surname)
     */
    public Clan(String id, String name) {
        this(id, name, null);
    }

    /**
     * Creates a new clan with description.
     *
     * @param id unique clan identifier
     * @param name clan name (typically surname)
     * @param description optional description
     */
    public Clan(String id, String name, String description) {
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("Clan ID cannot be null or empty");
        }
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Clan name cannot be null or empty");
        }
        this.id = id;
        this.name = name;
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Clan clan = (Clan) o;
        return Objects.equals(id, clan.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Clan{" + "id='" + id + '\'' + ", name='" + name + '\'' + '}';
    }
}
