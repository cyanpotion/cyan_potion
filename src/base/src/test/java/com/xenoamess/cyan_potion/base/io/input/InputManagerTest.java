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

package com.xenoamess.cyan_potion.base.io.input;

import com.xenoamess.cyan_potion.base.io.input.gamepad.*;
import com.xenoamess.cyan_potion.base.io.input.key.*;
import com.xenoamess.cyan_potion.base.io.input.keyboard.*;
import com.xenoamess.cyan_potion.base.io.input.mouse.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for input system classes.
 *
 * @author XenoAmess
 * @version 0.167.4
 */
public class InputManagerTest {

    @Test
    public void testKeyClassExists() {
        assertNotNull(Key.class);
    }

    @Test
    public void testKeymapClassExists() {
        assertNotNull(Keymap.class);
    }

    @Test
    public void testKeyActionEnumExists() {
        assertNotNull(KeyActionEnum.class);
    }

    @Test
    public void testKeyModEnumExists() {
        assertNotNull(KeyModEnum.class);
    }

    @Test
    public void testKeyboardKeyEnumExists() {
        assertNotNull(KeyboardKeyEnum.class);
    }

    @Test
    public void testKeyboardEventClassExists() {
        assertNotNull(KeyboardEvent.class);
    }

    @Test
    public void testCharEventClassExists() {
        assertNotNull(CharEvent.class);
    }

    @Test
    public void testTextEventClassExists() {
        assertNotNull(TextEvent.class);
    }

    @Test
    public void testMouseButtonEventClassExists() {
        assertNotNull(MouseButtonEvent.class);
    }

    @Test
    public void testMouseButtonKeyEnumExists() {
        assertNotNull(MouseButtonKeyEnum.class);
    }

    @Test
    public void testMouseScrollEventClassExists() {
        assertNotNull(MouseScrollEvent.class);
    }

    @Test
    public void testGamepadInputManagerClassExists() {
        assertNotNull(GamepadInputManager.class);
    }

    @Test
    public void testAbstractGamepadDataClassExists() {
        assertNotNull(AbstractGamepadData.class);
    }

    @Test
    public void testAbstractGamepadDeviceClassExists() {
        assertNotNull(AbstractGamepadDevice.class);
    }

    @Test
    public void testGamepadButtonEventClassExists() {
        assertNotNull(GamepadButtonEvent.class);
    }

    @Test
    public void testKeyActionEnumHasValues() {
        // Test that KeyActionEnum has values
        KeyActionEnum[] values = KeyActionEnum.values();
        assertTrue(values.length > 0, "Should have at least one value");
    }

    @Test
    public void testKeyboardKeyEnumHasValues() {
        // Test that KeyboardKeyEnum has values
        KeyboardKeyEnum[] values = KeyboardKeyEnum.values();
        assertTrue(values.length > 0, "Should have keyboard key values");
    }

    @Test
    public void testMouseButtonKeyEnumValues() {
        // Test that MouseButtonKeyEnum has the expected values
        MouseButtonKeyEnum[] values = MouseButtonKeyEnum.values();
        assertTrue(values.length >= 1, "Should have at least 1 mouse button");
    }
}
