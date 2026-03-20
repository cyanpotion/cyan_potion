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

import com.xenoamess.cyan_potion.civilization.character.Person;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * Generates a world map with cities and roads.
 * Ensures cities don't overlap, roads don't cross, and road lengths are reasonable.
 *
 * @author XenoAmess
 * @version 0.167.3-SNAPSHOT
 */
@Slf4j
public class WorldMapGenerator {

    private static final float MIN_CITY_DISTANCE = 0.12f;
    private static final float MAX_ROAD_DISTANCE = 0.35f;
    private static final float MAP_MARGIN = 0.08f;

    private final Random random;

    public WorldMapGenerator() {
        this.random = new Random();
    }

    public WorldMapGenerator(long seed) {
        this.random = new Random(seed);
    }

    /**
     * Generates a world map with the specified number of cities.
     *
     * @param cityCount number of cities to generate
     * @return list of generated cities
     */
    @NotNull
    public List<City> generateMap(int cityCount) {
        CityCache.clear();

        // Generate city positions
        List<City> cities = generateCities(cityCount);

        // Generate roads between nearby cities
        generateRoads(cities);

        // Add to cache
        cities.forEach(CityCache::addCity);

        log.info("Generated world map with {} cities and {} roads",
            cities.size(), cities.stream().mapToInt(c -> c.getConnectedCities().size()).sum() / 2);

        return cities;
    }

    @NotNull
    private List<City> generateCities(int count) {
        List<City> cities = new ArrayList<>();
        Set<String> usedNames = new HashSet<>();

        // City name templates
        String[] prefixes = {"东", "西", "南", "北", "中", "上", "下", "新", "旧", "古", "云", "风", "山", "水", "林", "石"};
        String[] suffixes = {"京", "阳", "州", "城", "都", "郡", "县", "镇", "村", "堡", "寨", "关", "口", "湾", "岛", "谷"};

        int attempts = 0;
        int maxAttempts = count * 100;

        while (cities.size() < count && attempts < maxAttempts) {
            attempts++;

            // Generate position with margin
            float x = MAP_MARGIN + random.nextFloat() * (1.0f - 2 * MAP_MARGIN);
            float y = MAP_MARGIN + random.nextFloat() * (1.0f - 2 * MAP_MARGIN);

            // Check minimum distance from existing cities
            boolean tooClose = false;
            for (City existing : cities) {
                float dx = x - existing.getX();
                float dy = y - existing.getY();
                float dist = (float) Math.sqrt(dx * dx + dy * dy);
                if (dist < MIN_CITY_DISTANCE) {
                    tooClose = true;
                    break;
                }
            }

            if (tooClose) {
                continue;
            }

            // Generate unique name
            String name = generateCityName(prefixes, suffixes, usedNames);
            usedNames.add(name);

            // Determine city type based on generation order (earlier = larger)
            City.CityType type;
            if (cities.isEmpty()) {
                type = City.CityType.CAPITAL;
            } else if (cities.size() < count * 0.2) {
                type = City.CityType.CITY;
            } else if (cities.size() < count * 0.6) {
                type = City.CityType.TOWN;
            } else {
                type = City.CityType.VILLAGE;
            }

            City city = new City(
                "city-" + cities.size(),
                name,
                x,
                y,
                type
            );
            cities.add(city);
        }

        return cities;
    }

    @NotNull
    private String generateCityName(String[] prefixes, String[] suffixes, Set<String> used) {
        String name;
        int attempts = 0;
        do {
            String prefix = prefixes[random.nextInt(prefixes.length)];
            String suffix = suffixes[random.nextInt(suffixes.length)];
            name = prefix + suffix;
            attempts++;
        } while (used.contains(name) && attempts < 100);

        // If duplicate, add a number
        if (used.contains(name)) {
            int num = 2;
            while (used.contains(name + num)) {
                num++;
            }
            name = name + num;
        }

        return name;
    }

    private void generateRoads(@NotNull List<City> cities) {
        if (cities.size() < 2) {
            return;
        }

        // Sort cities by x coordinate for more predictable road generation
        List<City> sortedCities = new ArrayList<>(cities);
        sortedCities.sort(Comparator.comparingDouble(City::getX));

        Set<String> existingRoads = new HashSet<>();

        for (int i = 0; i < sortedCities.size(); i++) {
            City cityA = sortedCities.get(i);

            // Find nearby cities sorted by distance
            List<CityDistance> distances = new ArrayList<>();
            for (int j = 0; j < sortedCities.size(); j++) {
                if (i == j) continue;
                City cityB = sortedCities.get(j);
                float dist = cityA.distanceTo(cityB);
                if (dist <= MAX_ROAD_DISTANCE) {
                    distances.add(new CityDistance(cityB, dist));
                }
            }

            // Sort by distance (closest first)
            distances.sort(Comparator.comparingDouble(cd -> cd.distance));

            // Connect to 1-3 closest cities
            int connectionsNeeded = 1 + random.nextInt(3);
            int connectionsMade = 0;

            for (CityDistance cd : distances) {
                if (connectionsMade >= connectionsNeeded) {
                    break;
                }

                City cityB = cd.city;

                // Check if road already exists
                String roadKey = getRoadKey(cityA, cityB);
                if (existingRoads.contains(roadKey)) {
                    continue;
                }

                // Check if road would cross existing roads
                if (wouldRoadCrossExisting(cityA, cityB, existingRoads, sortedCities)) {
                    continue;
                }

                // Create the connection
                cityA.connectTo(cityB);
                existingRoads.add(roadKey);
                connectionsMade++;
            }
        }

        // Ensure all cities are connected (minimum spanning tree for disconnected components)
        ensureConnectivity(sortedCities, existingRoads);
    }

    private boolean wouldRoadCrossExisting(@NotNull City a, @NotNull City b,
                                           @NotNull Set<String> existingRoads,
                                           @NotNull List<City> allCities) {
        for (String roadKey : existingRoads) {
            String[] parts = roadKey.split("-");
            City c = findCityById(parts[0], allCities);
            City d = findCityById(parts[1], allCities);

            if (c == null || d == null) continue;

            // Skip if sharing an endpoint
            if (c == a || c == b || d == a || d == b) {
                continue;
            }

            // Check for intersection
            if (linesIntersect(a.getX(), a.getY(), b.getX(), b.getY(),
                               c.getX(), c.getY(), d.getX(), d.getY())) {
                return true;
            }
        }
        return false;
    }

    @Nullable
    private City findCityById(@NotNull String id, @NotNull List<City> cities) {
        return cities.stream().filter(c -> c.getId().equals(id)).findFirst().orElse(null);
    }

    private boolean linesIntersect(float x1, float y1, float x2, float y2,
                                   float x3, float y3, float x4, float y4) {
        // Calculate denominator
        float denom = (y4 - y3) * (x2 - x1) - (x4 - x3) * (y2 - y1);

        // Lines are parallel
        if (Math.abs(denom) < 0.0001f) {
            return false;
        }

        float ua = ((x4 - x3) * (y1 - y3) - (y4 - y3) * (x1 - x3)) / denom;
        float ub = ((x2 - x1) * (y1 - y3) - (y2 - y1) * (x1 - x3)) / denom;

        // Check if intersection point lies within both line segments
        return ua > 0.001f && ua < 0.999f && ub > 0.001f && ub < 0.999f;
    }

    private void ensureConnectivity(@NotNull List<City> cities, @NotNull Set<String> existingRoads) {
        // Group cities into connected components
        List<Set<City>> components = new ArrayList<>();
        Set<City> visited = new HashSet<>();

        for (City city : cities) {
            if (visited.contains(city)) {
                continue;
            }

            Set<City> component = new HashSet<>();
            collectConnected(city, component, new HashSet<>());
            components.add(component);
            visited.addAll(component);
        }

        // Connect components
        for (int i = 1; i < components.size(); i++) {
            // Find closest pair between component 0 and component i
            City closestA = null;
            City closestB = null;
            float minDist = Float.MAX_VALUE;

            for (City a : components.get(0)) {
                for (City b : components.get(i)) {
                    float dist = a.distanceTo(b);
                    if (dist < minDist) {
                        minDist = dist;
                        closestA = a;
                        closestB = b;
                    }
                }
            }

            if (closestA != null && closestB != null) {
                closestA.connectTo(closestB);
                existingRoads.add(getRoadKey(closestA, closestB));
                components.get(0).addAll(components.get(i));
            }
        }
    }

    private void collectConnected(@NotNull City city, @NotNull Set<City> component, @NotNull Set<City> visited) {
        if (visited.contains(city)) {
            return;
        }
        visited.add(city);
        component.add(city);

        for (City neighbor : city.getConnectedCities()) {
            collectConnected(neighbor, component, visited);
        }
    }

    @NotNull
    private String getRoadKey(@NotNull City a, @NotNull City b) {
        if (a.getId().compareTo(b.getId()) < 0) {
            return a.getId() + "-" + b.getId();
        } else {
            return b.getId() + "-" + a.getId();
        }
    }

    /**
     * Assigns persons to cities randomly.
     *
     * @param persons the persons to assign
     */
    public void assignPersonsToCities(@NotNull List<Person> persons) {
        List<City> cities = new ArrayList<>(CityCache.getAllCities());
        if (cities.isEmpty()) {
            log.warn("No cities available to assign persons");
            return;
        }

        for (Person person : persons) {
            // Prefer cities with more capacity (larger cities)
            City city = selectCityWeighted(cities);
            city.addResident(person);
            person.setCityId(city.getId());
        }

        log.info("Assigned {} persons to {} cities", persons.size(), cities.size());
    }

    @NotNull
    private City selectCityWeighted(@NotNull List<City> cities) {
        // Weight by city type (larger cities get more residents)
        int totalWeight = cities.stream().mapToInt(c -> {
            return switch (c.getType()) {
                case CAPITAL -> 8;
                case CITY -> 5;
                case TOWN -> 3;
                case VILLAGE -> 1;
            };
        }).sum();

        int roll = random.nextInt(totalWeight);
        int cumulative = 0;

        for (City city : cities) {
            int weight = switch (city.getType()) {
                case CAPITAL -> 8;
                case CITY -> 5;
                case TOWN -> 3;
                case VILLAGE -> 1;
            };
            cumulative += weight;
            if (roll < cumulative) {
                return city;
            }
        }

        return cities.get(cities.size() - 1);
    }

    private record CityDistance(City city, float distance) {}
}
