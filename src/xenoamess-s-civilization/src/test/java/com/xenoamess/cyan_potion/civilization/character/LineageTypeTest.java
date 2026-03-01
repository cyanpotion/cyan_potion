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

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for LineageType.
 *
 * @author XenoAmess
 * @version 0.167.3-SNAPSHOT
 */
class LineageTypeTest {

    @Test
    void testPatrilineal() {
        LineageType type = LineageType.PATRILINEAL;

        assertEquals("父系", type.getChineseName());
        assertEquals("Patrilineal", type.getEnglishName());
    }

    @Test
    void testMatrilineal() {
        LineageType type = LineageType.MATRILINEAL;

        assertEquals("母系", type.getChineseName());
        assertEquals("Matrilineal", type.getEnglishName());
    }

    @Test
    void testDefault() {
        assertEquals(LineageType.PATRILINEAL, LineageType.getDefault());
    }

    @Test
    void testValues() {
        LineageType[] values = LineageType.values();
        assertEquals(2, values.length);
    }
}
