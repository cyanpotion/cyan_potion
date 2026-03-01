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

/**
 * Lineage type determines clan inheritance priority.
 *
 * @author XenoAmess
 * @version 0.167.3-SNAPSHOT
 */
public enum LineageType {
    /**
     * Patrilineal - father's clan is primary (most common).
     */
    PATRILINEAL("父系", "Patrilineal"),

    /**
     * Matrilineal - mother's clan is primary (rare).
     */
    MATRILINEAL("母系", "Matrilineal");

    private final String chineseName;
    private final String englishName;

    LineageType(String chineseName, String englishName) {
        this.chineseName = chineseName;
        this.englishName = englishName;
    }

    /**
     * Gets the Chinese name.
     *
     * @return Chinese name
     */
    public String getChineseName() {
        return chineseName;
    }

    /**
     * Gets the English name.
     *
     * @return English name
     */
    public String getEnglishName() {
        return englishName;
    }

    /**
     * Gets the default lineage type (patrilineal).
     *
     * @return PATRILINEAL
     */
    public static LineageType getDefault() {
        return PATRILINEAL;
    }
}
