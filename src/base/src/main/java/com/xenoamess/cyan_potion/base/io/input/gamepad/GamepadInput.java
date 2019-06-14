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

package com.xenoamess.cyan_potion.base.io.input.gamepad;

import com.codedisaster.steamworks.SteamController;
import com.codedisaster.steamworks.SteamControllerHandle;
import com.github.strikerx3.jxinput.XInputDevice;
import com.github.strikerx3.jxinput.exceptions.XInputNotLoadedException;
import com.xenoamess.cyan_potion.base.GameManager;
import com.xenoamess.cyan_potion.base.GameWindow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

/**
 * <p>GamepadInput class.</p>
 *
 * @author XenoAmess
 * @version 0.143.0
 */
public class GamepadInput {
    private static final Logger LOGGER =
            LoggerFactory.getLogger(GamepadInput.class);
    private final ArrayList<AbstractGamepadData> gamepadDatas = new ArrayList<>();

    /**
     * <p>init.</p>
     *
     * @param gameManager a {@link com.xenoamess.cyan_potion.base.GameManager} object.
     */
    public void init(GameManager gameManager) {
        try {
            int jXInputDeviceNum = XInputDevice.getAllDevices().length;
            for (int i = 0; i < jXInputDeviceNum; i++) {
                AbstractGamepadDevice jXInputGamepadDevice =
                        new JXInputGamepadDevice(i);
                getGamepadDatas().add(new JXInputGamepadData(jXInputGamepadDevice));
            }
        } catch (XInputNotLoadedException e) {
            LOGGER.debug("XInputNotLoadedException", e);
        }

        if (gameManager.getDataCenter().isRunWithSteam()) {
            SteamControllerHandle[] steamControllerHandles =
                    new SteamControllerHandle[SteamController.STEAM_CONTROLLER_MAX_COUNT];
            SteamController steamController = new SteamController();
            int steamControllerNum =
                    steamController.getConnectedControllers(steamControllerHandles);
            LOGGER.debug("steamControllerNum : {}", steamControllerNum);
        }
    }

    /**
     * <p>update.</p>
     *
     * @param gameWindow a {@link com.xenoamess.cyan_potion.base.GameWindow} object.
     */
    public void update(GameWindow gameWindow) {
        for (AbstractGamepadData gamepadData : this.getGamepadDatas()) {
            gamepadData.update(gameWindow);
        }
    }

    /**
     * <p>Getter for the field <code>gamepadDatas</code>.</p>
     *
     * @return a {@link java.util.ArrayList} object.
     */
    public ArrayList<AbstractGamepadData> getGamepadDatas() {
        return gamepadDatas;
    }

}
