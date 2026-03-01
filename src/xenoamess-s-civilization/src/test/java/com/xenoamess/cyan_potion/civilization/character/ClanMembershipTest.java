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
 * Unit tests for ClanMembership.
 *
 * @author XenoAmess
 * @version 0.167.3-SNAPSHOT
 */
class ClanMembershipTest {

    @Test
    void testPrimaryMembership() {
        Clan clan = new Clan("C001", "李");
        ClanMembership membership = ClanMembership.primary(clan);

        assertEquals(clan, membership.getClan());
        assertTrue(membership.isPrimary());
    }

    @Test
    void testSecondaryMembership() {
        Clan clan = new Clan("C002", "王");
        ClanMembership membership = ClanMembership.secondary(clan);

        assertEquals(clan, membership.getClan());
        assertFalse(membership.isPrimary());
    }

    @Test
    void testConstructor() {
        Clan clan = new Clan("C003", "张");
        ClanMembership membership = new ClanMembership(clan, true);

        assertEquals(clan, membership.getClan());
        assertTrue(membership.isPrimary());
    }

    @Test
    void testNullClan() {
        assertThrows(IllegalArgumentException.class, () -> new ClanMembership(null, true));
    }

    @Test
    void testEquality() {
        Clan clan1 = new Clan("C001", "李");
        Clan clan2 = new Clan("C001", "李");

        ClanMembership m1 = new ClanMembership(clan1, true);
        ClanMembership m2 = new ClanMembership(clan2, true);
        ClanMembership m3 = new ClanMembership(clan1, false);

        assertEquals(m1, m2);
        assertNotEquals(m1, m3);
    }

    @Test
    void testToString() {
        Clan clan = new Clan("C001", "李");
        ClanMembership membership = ClanMembership.primary(clan);

        String str = membership.toString();
        assertTrue(str.contains("Primary"));
        assertTrue(str.contains("李"));
    }
}
