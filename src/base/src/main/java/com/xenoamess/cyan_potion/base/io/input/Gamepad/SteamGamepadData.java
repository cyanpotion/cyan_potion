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

    }

    @Override
    public void reset() {

    }

    @Override
    public void update(GameWindow gameWindow) {

    }
}
