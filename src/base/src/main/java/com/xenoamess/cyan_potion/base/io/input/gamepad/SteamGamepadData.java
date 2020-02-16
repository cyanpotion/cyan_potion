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

package com.xenoamess.cyan_potion.base.io.input.gamepad;

import com.xenoamess.cyan_potion.base.GameWindow;

/**
 * TODO the class will be to deal with Steam Gamepad libs
 * TODO !not implemented yet.!
 * TODO But really it is not at high priority, as steam controller is not well accepted.
 *
 * @author XenoAmess
 * @version 0.159.0-SNAPSHOT
 */
public class SteamGamepadData extends AbstractGamepadData {
    /**
     * Constant <code>STEAM_GAMEPAD_KEY_A=0</code>
     */
    public static final int STEAM_GAMEPAD_KEY_A = 0;
    /**
     * Constant <code>STEAM_GAMEPAD_KEY_B=1</code>
     */
    public static final int STEAM_GAMEPAD_KEY_B = 1;
    /**
     * Constant <code>STEAM_GAMEPAD_KEY_X=2</code>
     */
    public static final int STEAM_GAMEPAD_KEY_X = 2;
    /**
     * Constant <code>STEAM_GAMEPAD_KEY_Y=3</code>
     */
    public static final int STEAM_GAMEPAD_KEY_Y = 3;
    /**
     * Constant <code>STEAM_GAMEPAD_KEY_BACK=4</code>
     */
    public static final int STEAM_GAMEPAD_KEY_BACK = 4;
    /**
     * Constant <code>STEAM_GAMEPAD_KEY_START=5</code>
     */
    public static final int STEAM_GAMEPAD_KEY_START = 5;
    /**
     * Constant <code>STEAM_GAMEPAD_KEY_LB=6</code>
     */
    public static final int STEAM_GAMEPAD_KEY_LB = 6;
    /**
     * Constant <code>STEAM_GAMEPAD_KEY_RB=7</code>
     */
    public static final int STEAM_GAMEPAD_KEY_RB = 7;
    /**
     * Constant <code>STEAM_GAMEPAD_KEY_L=8</code>
     */
    public static final int STEAM_GAMEPAD_KEY_L = 8;
    /**
     * Constant <code>STEAM_GAMEPAD_KEY_R=9</code>
     */
    public static final int STEAM_GAMEPAD_KEY_R = 9;
    /**
     * Constant <code>STEAM_GAMEPAD_KEY_UP=10</code>
     */
    public static final int STEAM_GAMEPAD_KEY_UP = 10;
    /**
     * Constant <code>STEAM_GAMEPAD_KEY_DOWN=11</code>
     */
    public static final int STEAM_GAMEPAD_KEY_DOWN = 11;
    /**
     * Constant <code>STEAM_GAMEPAD_KEY_LEFT=12</code>
     */
    public static final int STEAM_GAMEPAD_KEY_LEFT = 12;
    /**
     * Constant <code>STEAM_GAMEPAD_KEY_RIGHT=13</code>
     */
    public static final int STEAM_GAMEPAD_KEY_RIGHT = 13;
    /**
     * Constant <code>STEAM_GAMEPAD_KEY_GUIDE=14</code>
     */
    public static final int STEAM_GAMEPAD_KEY_GUIDE = 14;
    /**
     * Constant <code>STEAM_GAMEPAD_KEY_UNKNOWN=15</code>
     */
    public static final int STEAM_GAMEPAD_KEY_UNKNOWN = 15;
    /**
     * Constant <code>STEAM_GAMEPAD_KEY_LT=16</code>
     */
    public static final int STEAM_GAMEPAD_KEY_LT = 16;
    /**
     * Constant <code>STEAM_GAMEPAD_KEY_RT=17</code>
     */
    public static final int STEAM_GAMEPAD_KEY_RT = 17;
    /**
     * Constant <code>STEAM_GAMEPAD_KEY_LAST=17</code>
     */
    public static final int STEAM_GAMEPAD_KEY_LAST = 17;

    /**
     * <p>Constructor for SteamGamepadData.</p>
     *
     * @param gamepadDevice gamepadDevice
     */
    public SteamGamepadData(AbstractGamepadDevice gamepadDevice) {
        super(gamepadDevice);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateGamepadStatus(GameWindow gameWindow) {
        //TODO
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void reset() {
        //TODO
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void update(GameWindow gameWindow) {
        //TODO
    }
}
