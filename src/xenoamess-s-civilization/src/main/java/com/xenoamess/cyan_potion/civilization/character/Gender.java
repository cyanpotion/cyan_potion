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

/**
 * Gender enum for characters.
 *
 * @author XenoAmess
 * @version 0.167.3-SNAPSHOT
 */
public enum Gender {
    /**
     * Male gender.
     * Base health: 70
     */
    MALE(70.0),
    
    /**
     * Female gender.
     * Base health: 100
     */
    FEMALE(100.0);
    
    private final double baseHealth;
    
    Gender(double baseHealth) {
        this.baseHealth = baseHealth;
    }
    
    /**
     * Gets the base health for this gender.
     *
     * @return base health value
     */
    public double getBaseHealth() {
        return baseHealth;
    }
}
