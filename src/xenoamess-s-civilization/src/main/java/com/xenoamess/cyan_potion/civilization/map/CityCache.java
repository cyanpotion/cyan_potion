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

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Cache for managing all cities in the world.
 *
 * @author XenoAmess
 * @version 0.167.3-SNAPSHOT
 */
@Slf4j
public class CityCache {

    /**
     * All cities indexed by ID.
     */
    public static final Map<String, City> CITY_CACHE = new ConcurrentHashMap<>();

    /**
     * Gets a city by ID.
     *
     * @param id the city ID
     * @return the city or null if not found
     */
    @Nullable
    public static City getCity(@NotNull String id) {
        return CITY_CACHE.get(id);
    }

    /**
     * Adds a city to the cache.
     *
     * @param city the city to add
     */
    public static void addCity(@NotNull City city) {
        CITY_CACHE.put(city.getId(), city);
        log.debug("Added city to cache: {} ({})", city.getName(), city.getId());
    }

    /**
     * Removes a city from the cache.
     *
     * @param id the city ID
     * @return the removed city or null
     */
    @Nullable
    public static City removeCity(@NotNull String id) {
        City removed = CITY_CACHE.remove(id);
        if (removed != null) {
            // Remove connections
            removed.getConnectedCities().forEach(other -> other.getConnectedCities().remove(removed));
            log.debug("Removed city from cache: {} ({})", removed.getName(), id);
        }
        return removed;
    }

    /**
     * Gets all cities.
     *
     * @return collection of all cities
     */
    @NotNull
    public static Collection<City> getAllCities() {
        return CITY_CACHE.values();
    }

    /**
     * Gets all cities as a stream.
     *
     * @return stream of all cities
     */
    @NotNull
    public static java.util.stream.Stream<City> getAllCityStream() {
        return CITY_CACHE.values().stream();
    }

    /**
     * Clears all cities.
     */
    public static void clear() {
        CITY_CACHE.clear();
        log.info("Cleared all cities from cache");
    }

    /**
     * Gets the total resident count across all cities.
     *
     * @return total residents
     */
    public static int getTotalResidentCount() {
        return CITY_CACHE.values().stream()
            .mapToInt(City::getResidentCount)
            .sum();
    }

    /**
     * Gets cities sorted by resident count.
     *
     * @return list of cities sorted by population
     */
    @NotNull
    public static List<City> getCitiesByPopulation() {
        return CITY_CACHE.values().stream()
            .sorted((a, b) -> Integer.compare(b.getResidentCount(), a.getResidentCount()))
            .collect(Collectors.toList());
    }

    /**
     * Finds the city containing a person.
     *
     * @param personId the person ID
     * @return the city or null if person is not in any city
     */
    @Nullable
    public static City findCityOfPerson(@NotNull String personId) {
        return CITY_CACHE.values().stream()
            .filter(city -> city.getResidents().stream()
                .anyMatch(p -> p.getId().equals(personId)))
            .findFirst()
            .orElse(null);
    }
}
