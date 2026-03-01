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
 * Unit tests for Clan.
 *
 * @author XenoAmess
 * @version 0.167.3-SNAPSHOT
 */
class ClanTest {

    @Test
    void testClanCreation() {
        Clan clan = new Clan("C001", "李");

        assertEquals("C001", clan.getId());
        assertEquals("李", clan.getName());
        assertNull(clan.getDescription());
    }

    @Test
    void testClanWithDescription() {
        Clan clan = new Clan("C002", "王", "太原王氏");

        assertEquals("C002", clan.getId());
        assertEquals("王", clan.getName());
        assertEquals("太原王氏", clan.getDescription());
    }

    @Test
    void testClanInvalidId() {
        assertThrows(IllegalArgumentException.class, () -> new Clan(null, "李"));
        assertThrows(IllegalArgumentException.class, () -> new Clan("", "李"));
    }

    @Test
    void testClanInvalidName() {
        assertThrows(IllegalArgumentException.class, () -> new Clan("C001", null));
        assertThrows(IllegalArgumentException.class, () -> new Clan("C001", ""));
    }

    @Test
    void testClanEquality() {
        Clan clan1 = new Clan("C001", "李");
        Clan clan2 = new Clan("C001", "李");
        Clan clan3 = new Clan("C002", "王");

        assertEquals(clan1, clan2);
        assertEquals(clan1.hashCode(), clan2.hashCode());
        assertNotEquals(clan1, clan3);
    }

    @Test
    void testClanToString() {
        Clan clan = new Clan("C001", "李");
        String str = clan.toString();

        assertTrue(str.contains("C001"));
        assertTrue(str.contains("李"));
    }
}
