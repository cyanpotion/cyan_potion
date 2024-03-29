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

package com.xenoamess.cyan_potion.base.io.input.keyboard;

import java.util.Arrays;

/**
 * <p>KeyboardKeyEnum class.</p>
 *
 * @author XenoAmess
 * @version 0.162.3
 */
public enum KeyboardKeyEnum {
    GLFW_KEY_UNKNOWN(-1),
    GLFW_KEY_SPACE(32),
    GLFW_KEY_APOSTROPHE(39),
    GLFW_KEY_COMMA(44),
    GLFW_KEY_MINUS(45),
    GLFW_KEY_PERIOD(46),
    GLFW_KEY_SLASH(47),
    GLFW_KEY_0(48),
    GLFW_KEY_1(49),
    GLFW_KEY_2(50),
    GLFW_KEY_3(51),
    GLFW_KEY_4(52),
    GLFW_KEY_5(53),
    GLFW_KEY_6(54),
    GLFW_KEY_7(55),
    GLFW_KEY_8(56),
    GLFW_KEY_9(57),
    GLFW_KEY_SEMICOLON(59),
    GLFW_KEY_EQUAL(61),
    GLFW_KEY_A(65),
    GLFW_KEY_B(66),
    GLFW_KEY_C(67),
    GLFW_KEY_D(68),
    GLFW_KEY_E(69),
    GLFW_KEY_F(70),
    GLFW_KEY_G(71),
    GLFW_KEY_H(72),
    GLFW_KEY_I(73),
    GLFW_KEY_J(74),
    GLFW_KEY_K(75),
    GLFW_KEY_L(76),
    GLFW_KEY_M(77),
    GLFW_KEY_N(78),
    GLFW_KEY_O(79),
    GLFW_KEY_P(80),
    GLFW_KEY_Q(81),
    GLFW_KEY_R(82),
    GLFW_KEY_S(83),
    GLFW_KEY_T(84),
    GLFW_KEY_U(85),
    GLFW_KEY_V(86),
    GLFW_KEY_W(87),
    GLFW_KEY_X(88),
    GLFW_KEY_Y(89),
    GLFW_KEY_Z(90),
    GLFW_KEY_LEFT_BRACKET(91),
    GLFW_KEY_BACKSLASH(92),
    GLFW_KEY_RIGHT_BRACKET(93),
    GLFW_KEY_GRAVE_ACCENT(96),
    GLFW_KEY_WORLD_1(161),
    GLFW_KEY_WORLD_2(162),
    GLFW_KEY_ESCAPE(256),
    GLFW_KEY_ENTER(257),
    GLFW_KEY_TAB(258),
    GLFW_KEY_BACKSPACE(259),
    GLFW_KEY_INSERT(260),
    GLFW_KEY_DELETE(261),
    GLFW_KEY_RIGHT(262),
    GLFW_KEY_LEFT(263),
    GLFW_KEY_DOWN(264),
    GLFW_KEY_UP(265),
    GLFW_KEY_PAGE_UP(266),
    GLFW_KEY_PAGE_DOWN(267),
    GLFW_KEY_HOME(268),
    GLFW_KEY_END(269),
    GLFW_KEY_CAPS_LOCK(280),
    GLFW_KEY_SCROLL_LOCK(281),
    GLFW_KEY_NUM_LOCK(282),
    GLFW_KEY_PRINT_SCREEN(283),
    GLFW_KEY_PAUSE(284),
    GLFW_KEY_F1(290),
    GLFW_KEY_F2(291),
    GLFW_KEY_F3(292),
    GLFW_KEY_F4(293),
    GLFW_KEY_F5(294),
    GLFW_KEY_F6(295),
    GLFW_KEY_F7(296),
    GLFW_KEY_F8(297),
    GLFW_KEY_F9(298),
    GLFW_KEY_F10(299),
    GLFW_KEY_F11(300),
    GLFW_KEY_F12(301),
    GLFW_KEY_F13(302),
    GLFW_KEY_F14(303),
    GLFW_KEY_F15(304),
    GLFW_KEY_F16(305),
    GLFW_KEY_F17(306),
    GLFW_KEY_F18(307),
    GLFW_KEY_F19(308),
    GLFW_KEY_F20(309),
    GLFW_KEY_F21(310),
    GLFW_KEY_F22(311),
    GLFW_KEY_F23(312),
    GLFW_KEY_F24(313),
    GLFW_KEY_F25(314),
    GLFW_KEY_KP_0(320),
    GLFW_KEY_KP_1(321),
    GLFW_KEY_KP_2(322),
    GLFW_KEY_KP_3(323),
    GLFW_KEY_KP_4(324),
    GLFW_KEY_KP_5(325),
    GLFW_KEY_KP_6(326),
    GLFW_KEY_KP_7(327),
    GLFW_KEY_KP_8(328),
    GLFW_KEY_KP_9(329),
    GLFW_KEY_KP_DECIMAL(330),
    GLFW_KEY_KP_DIVIDE(331),
    GLFW_KEY_KP_MULTIPLY(332),
    GLFW_KEY_KP_SUBTRACT(333),
    GLFW_KEY_KP_ADD(334),
    GLFW_KEY_KP_ENTER(335),
    GLFW_KEY_KP_EQUAL(336),
    GLFW_KEY_LEFT_SHIFT(340),
    GLFW_KEY_LEFT_CONTROL(341),
    GLFW_KEY_LEFT_ALT(342),
    GLFW_KEY_LEFT_SUPER(343),
    GLFW_KEY_RIGHT_SHIFT(344),
    GLFW_KEY_RIGHT_CONTROL(345),
    GLFW_KEY_RIGHT_ALT(346),
    GLFW_KEY_RIGHT_SUPER(347),
    GLFW_KEY_MENU(348);

    /**
     * Constant <code>values</code>
     */
    public final int value;
    private static final KeyboardKeyEnum[] VALUES = generateValues();

    KeyboardKeyEnum(int value) {
        this.value = value;
    }

    /**
     * <p>Getter for the field <code>values</code>.</p>
     *
     * @return an array of {@link com.xenoamess.cyan_potion.base.io.input.keyboard.KeyboardKeyEnum} objects.
     */
    public static KeyboardKeyEnum[] getValues() {
        return VALUES;
    }

    /**
     * <p>Getter for the field <code>value</code>.</p>
     *
     * @return a int.
     */
    @SuppressWarnings("unused")
    public int getValue() {
        return value;
    }


    /**
     * <p>getByValue.</p>
     *
     * @param value a int.
     * @return a {@link com.xenoamess.cyan_potion.base.io.input.keyboard.KeyboardKeyEnum} object.
     */
    public static KeyboardKeyEnum getByValue(int value) {
        if (value >= 0 && value < getValues().length) {
            return getValues()[value];
        }
        return GLFW_KEY_UNKNOWN;
    }

    /**
     * <p>getStringByValue.</p>
     *
     * @param value a int.
     * @return a {@link java.lang.String} object.
     */
    @SuppressWarnings("unused")
    public static String getStringByValue(int value) {
        return getByValue(value).name();
    }

    private static int maxValue() {
        KeyboardKeyEnum[] rawValues = values();
        int maxValue = rawValues[0].value;
        for (KeyboardKeyEnum au : rawValues) {
            if (maxValue < au.value) {
                maxValue = au.value;
            }
        }
        return maxValue;
    }

    private static KeyboardKeyEnum[] generateValues() {
        KeyboardKeyEnum[] res = new KeyboardKeyEnum[maxValue() + 1];
        Arrays.fill(res, GLFW_KEY_UNKNOWN);

        for (KeyboardKeyEnum au : values()) {
            if (au.value >= 0) {
                res[au.value] = au;
            }
        }
        return res;
    }
}
