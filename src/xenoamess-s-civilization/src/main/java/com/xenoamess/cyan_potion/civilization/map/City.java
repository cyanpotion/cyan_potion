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
package com.xenoamess.cyan_potion.civilization.map;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Represents a city in the civilization world.
 * Each city has a unique fixed location and can contain multiple persons.
 *
 * @author XenoAmess
 * @version 0.167.3-SNAPSHOT
 */
@Data
@EqualsAndHashCode(of = "id")
@ToString(exclude = "residents")
public class City {

    /**
     * City type affecting visual representation.
     */
    public enum CityType {
        CAPITAL("首都", 12, 0xFFD700),
        CITY("城市", 10, 0xFFA500),
        TOWN("城镇", 8, 0x87CEEB),
        VILLAGE("村庄", 6, 0x98FB98);

        @Getter
        private final String displayName;
        @Getter
        private final int size;
        @Getter
        private final int color;

        CityType(String displayName, int size, int color) {
            this.displayName = displayName;
            this.size = size;
            this.color = color;
        }
    }

    @Getter
    @Setter
    @NotNull
    private String id;

    @Getter
    @Setter
    @NotNull
    private String name;

    /**
     * X coordinate in map space (0.0 - 1.0 normalized).
     */
    @Getter
    @Setter
    private float x;

    /**
     * Y coordinate in map space (0.0 - 1.0 normalized).
     */
    @Getter
    @Setter
    private float y;

    @Getter
    @Setter
    @NotNull
    private CityType type;

    /**
     * Connected neighboring cities (roads).
     */
    @Getter
    private final Collection<City> connectedCities = new ConcurrentLinkedDeque<>();

    /**
     * Residents of this city.
     */
    @Getter
    private final Collection<com.xenoamess.cyan_potion.civilization.character.Person> residents = new ConcurrentLinkedDeque<>();

    public City(@NotNull String id, @NotNull String name, float x, float y, @NotNull CityType type) {
        this.id = id;
        this.name = name;
        this.x = x;
        this.y = y;
        this.type = type;
    }

    /**
     * Adds a road connection to another city.
     *
     * @param other the city to connect to
     * @return true if connection was added
     */
    public boolean connectTo(@NotNull City other) {
        if (other == this || connectedCities.contains(other)) {
            return false;
        }
        connectedCities.add(other);
        other.connectedCities.add(this);
        return true;
    }

    /**
     * Checks if this city is connected to another.
     *
     * @param other the city to check
     * @return true if connected
     */
    public boolean isConnectedTo(@NotNull City other) {
        return connectedCities.contains(other);
    }

    /**
     * Adds a resident to this city.
     *
     * @param person the person to add
     * @return true if added
     */
    public boolean addResident(@NotNull com.xenoamess.cyan_potion.civilization.character.Person person) {
        return residents.add(person);
    }

    /**
     * Removes a resident from this city.
     *
     * @param person the person to remove
     * @return true if removed
     */
    public boolean removeResident(@NotNull com.xenoamess.cyan_potion.civilization.character.Person person) {
        return residents.remove(person);
    }

    /**
     * Gets the resident count.
     *
     * @return number of residents
     */
    public int getResidentCount() {
        return residents.size();
    }

    /**
     * Calculates Euclidean distance to another city.
     *
     * @param other the other city
     * @return distance in normalized coordinates
     */
    public float distanceTo(@NotNull City other) {
        float dx = this.x - other.x;
        float dy = this.y - other.y;
        return (float) Math.sqrt(dx * dx + dy * dy);
    }
}
