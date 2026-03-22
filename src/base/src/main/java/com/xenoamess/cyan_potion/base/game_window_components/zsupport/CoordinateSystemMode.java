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

/**
 * Coordinate system mode enumeration for UI components.
 * <p>
 * This enum defines how a component handles Z-coordinate for rendering and event processing.
 * </p>
 *
 * <ul>
 *   <li>{@link #LEGACY_MODE} - Traditional mode that ignores Z coordinate, uses tree traversal order</li>
 *   <li>{@link #Z_AXIS_MODE} - Z-axis mode that uses Z coordinate for layering and event handling</li>
 * </ul>
 *
 * <p>
 * When a component is in Z_AXIS_MODE:
 * <ul>
 *   <li>Rendering: Children are sorted by Z coordinate (low to high) before drawing</li>
 *   <li>Events: Components with higher Z receive events first (top-most)</li>
 * </ul>
 * </p>
 *
 * <p>
 * When a component is in LEGACY_MODE:
 * <ul>
 *   <li>Rendering: Children are rendered in tree traversal order (original behavior)</li>
 *   <li>Events: First hit component receives event (original behavior)</li>
 * </ul>
 * </p>
 *
 * @author XenoAmess
 * @version 0.167.4
 * @see com.xenoamess.cyan_potion.base.game_window_components.AbstractGameWindowComponent
 * @since 2026-03-22
 */
public enum CoordinateSystemMode {
    /**
     * Legacy mode that ignores Z coordinate.
     * <p>
     * In this mode:
     * <ul>
     *   <li>Rendering uses tree traversal order (children list order)</li>
     *   <li>Event processing uses original hit-test order</li>
     *   <li>Z coordinate is completely ignored</li>
     * </ul>
     * </p>
     * <p>
     * This is the default mode for backward compatibility.
     * </p>
     */
    LEGACY_MODE,

    /**
     * Z-axis mode that uses Z coordinate for layering.
     * <p>
     * In this mode:
     * <ul>
     *   <li>Rendering: Children are sorted by Z coordinate from low to high</li>
     *   <li>Events: Components with higher Z are checked first (top-most receives event)</li>
     *   <li>Same Z values maintain tree traversal stability</li>
     * </ul>
     * </p>
     * <p>
     * Components with positive Z values appear in front of those with negative values.
     * Higher Z = closer to viewer.
     * </p>
     */
    Z_AXIS_MODE
}
