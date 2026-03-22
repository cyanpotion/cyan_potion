/*
 * MIT License
 *
 * Copyright (c) 2020 XenoAmess
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.xenoamess.cyan_potion.base.game_window_components.zsupport;

import com.xenoamess.cyan_potion.base.game_window_components.GameWindowComponentTreeNode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Utility class for sorting components by Z coordinate.
 * <p>
 * This class provides stable sorting of GameWindowComponentTreeNode instances
 * based on their associated component's Z coordinate.
 * </p>
 *
 * <p>
 * Features:
 * <ul>
 *   <li>Stable sort: Components with the same Z coordinate maintain their original order</li>
 *   <li>Performance: Uses TimSort (Java's Collections.sort) for O(n log n) complexity</li>
 *   <li>Thread safety: Sorting is performed on a copy of the input list</li>
 * </ul>
 * </p>
 *
 * @author XenoAmess
 * @version 0.167.4
 * @since 2026-03-22
 */
public final class ZIndexSorter {

    /**
     * Private constructor to prevent instantiation of utility class.
     */
    private ZIndexSorter() {
        // Utility class - prevent instantiation
    }

    /**
     * Comparator for sorting nodes by Z coordinate in ascending order.
     * Nodes with lower Z values come first (rendered behind).
     * Nodes with higher Z values come later (rendered in front).
     */
    private static final Comparator<GameWindowComponentTreeNode> Z_COMPARATOR =
            Comparator.comparing(node -> node.getGameWindowComponent().getZ());

    /**
     * Sort a list of component tree nodes by their Z coordinates.
     * <p>
     * This method creates a new list containing the nodes sorted by Z coordinate
     * in ascending order (low to high). The sort is stable, meaning nodes with
     * equal Z coordinates maintain their relative order from the input list.
     * </p>
     *
     * @param nodes the list of nodes to sort (will not be modified)
     * @return a new list containing the nodes sorted by Z coordinate
     * @throws NullPointerException if nodes is null
     */
    public static List<GameWindowComponentTreeNode> sortByZ(List<GameWindowComponentTreeNode> nodes) {
        if (nodes == null) {
            throw new NullPointerException("nodes list cannot be null");
        }

        if (nodes.size() <= 1) {
            // Return a new list even for 0 or 1 elements to maintain consistency
            return new ArrayList<>(nodes);
        }

        // Create a copy to avoid modifying the original list
        List<GameWindowComponentTreeNode> sorted = new ArrayList<>(nodes);

        // Sort by Z coordinate (ascending)
        // Collections.sort uses TimSort which is stable
        Collections.sort(sorted, Z_COMPARATOR);

        return sorted;
    }

    /**
     * Sort a list of component tree nodes by their Z coordinates in descending order.
     * <p>
     * This is useful for event processing where higher Z components should be checked first.
     * </p>
     *
     * @param nodes the list of nodes to sort (will not be modified)
     * @return a new list containing the nodes sorted by Z coordinate (high to low)
     * @throws NullPointerException if nodes is null
     */
    public static List<GameWindowComponentTreeNode> sortByZDescending(List<GameWindowComponentTreeNode> nodes) {
        if (nodes == null) {
            throw new NullPointerException("nodes list cannot be null");
        }

        if (nodes.size() <= 1) {
            return new ArrayList<>(nodes);
        }

        List<GameWindowComponentTreeNode> sorted = new ArrayList<>(nodes);

        // Sort by Z coordinate (descending)
        Collections.sort(sorted, Z_COMPARATOR.reversed());

        return sorted;
    }
}
