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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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

    @Getter
    private final String surname;

    @Getter
    private final SurnamePosition surnamePosition;

    /**
     * Creates a new clan.
     *
     * @param id unique clan identifier
     * @param name clan name (typically surname)
     */
    public Clan(@NotNull String id, @NotNull String name) {
        this(id, name, null, name, SurnamePosition.PREFIX);
    }

    /**
     * Creates a new clan with description.
     *
     * @param id unique clan identifier
     * @param name clan name (typically surname)
     * @param description optional description
     */
    public Clan(@NotNull String id, @NotNull String name, @Nullable String description) {
        this(id, name, description, name, SurnamePosition.PREFIX);
    }

    /**
     * Creates a new clan with full parameters.
     *
     * @param id unique clan identifier
     * @param name clan name
     * @param description optional description
     * @param surname the family surname
     * @param surnamePosition whether surname appears before or after given name
     */
    public Clan(@NotNull String id, @NotNull String name, @Nullable String description, @NotNull String surname, @NotNull SurnamePosition surnamePosition) {
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("Clan ID cannot be null or empty");
        }
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Clan name cannot be null or empty");
        }
        if (surname == null || surname.isEmpty()) {
            throw new IllegalArgumentException("Surname cannot be null or empty");
        }
        this.id = id;
        this.name = name;
        this.description = description;
        this.surname = surname;
        this.surnamePosition = surnamePosition != null ? surnamePosition : SurnamePosition.PREFIX;
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
        return "Clan{" + "id='" + id + '\'' + ", name='" + name + '\'' + ", surname='" + surname + '\'' + ", surnamePosition=" + surnamePosition + '}';
    }

    /**
     * Enum representing the position of surname in a full name.
     */
    public enum SurnamePosition {
        PREFIX,   // Surname appears before given name (e.g., Chinese: 张三)
        SUFFIX    // Surname appears after given name (e.g., Western: John Smith)
    }
}
