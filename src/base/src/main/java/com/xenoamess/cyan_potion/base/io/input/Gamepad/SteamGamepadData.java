/*
 * MIT License
 *
 * Copyright (c) 2019 XenoAmess
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

package com.xenoamess.cyan_potion.base.io.input.Gamepad;

import com.xenoamess.cyan_potion.base.GameWindow;

/**
 * the class will be to deal with Steam Gamepad libs
 * not implemented yet.
 *
 * @author XenoAmess
 */
public class SteamGamepadData extends AbstractGamepadData {
    public static final int STEAM_GAMEPAD_KEY_A = 0;
    public static final int STEAM_GAMEPAD_KEY_B = 1;
    public static final int STEAM_GAMEPAD_KEY_X = 2;
    public static final int STEAM_GAMEPAD_KEY_Y = 3;
    public static final int STEAM_GAMEPAD_KEY_BACK = 4;
    public static final int STEAM_GAMEPAD_KEY_START = 5;
    public static final int STEAM_GAMEPAD_KEY_LB = 6;
    public static final int STEAM_GAMEPAD_KEY_RB = 7;
    public static final int STEAM_GAMEPAD_KEY_L = 8;
    public static final int STEAM_GAMEPAD_KEY_R = 9;
    public static final int STEAM_GAMEPAD_KEY_UP = 10;
    public static final int STEAM_GAMEPAD_KEY_DOWN = 11;
    public static final int STEAM_GAMEPAD_KEY_LEFT = 12;
    public static final int STEAM_GAMEPAD_KEY_RIGHT = 13;
    public static final int STEAM_GAMEPAD_KEY_GUIDE = 14;
    public static final int STEAM_GAMEPAD_KEY_UNKNOWN = 15;
    public static final int STEAM_GAMEPAD_KEY_LT = 16;
    public static final int STEAM_GAMEPAD_KEY_RT = 17;
    public static final int STEAM_GAMEPAD_KEY_LAST = 17;

    public SteamGamepadData(AbstractGamepadDevice gamepadDevice) {
        super(gamepadDevice);
    }

    @Override
    public void updateGamepadStatus(GameWindow gameWindow) {
        //TODO
    }

    @Override
    public void reset() {
        //TODO
    }

    @Override
    public void update(GameWindow gameWindow) {
        //TODO
    }
}
