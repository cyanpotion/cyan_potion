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
import com.studiohartman.jamepad.ControllerManager;
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
public class GamepadInputManager implements AutoCloseable {
    private static final Logger LOGGER =
            LoggerFactory.getLogger(GamepadInputManager.class);
    private final ArrayList<AbstractGamepadData> gamepadDatas = new ArrayList<>();

    private ControllerManager jamepadControllerManager = null;

    /**
     * <p>init.</p>
     *
     * @param gameManager gameManager
     */
    public void init(GameManager gameManager) {
        if (gameManager.getDataCenter().isUsingJXInput()) {
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
        } else {
            this.jamepadControllerManager = new ControllerManager();
            jamepadControllerManager.initSDLGamepad();

            int jamepadDeviceNum = jamepadControllerManager.getNumControllers();
            for (int i = 0; i < jamepadDeviceNum; i++) {
                AbstractGamepadDevice jamepadGamepadDevice =
                        new JamepadGamepadDevice(this.jamepadControllerManager, i);
                getGamepadDatas().add(new JamepadGamepadData(jamepadGamepadDevice));
            }
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

    @Override
    public void close() {
        if (jamepadControllerManager != null) {
            jamepadControllerManager.quitSDLGamepad();
        }
    }

    /**
     * <p>update.</p>
     *
     * @param gameWindow gameWindow
     */
    public void update(GameWindow gameWindow) {
        for (AbstractGamepadData gamepadData : this.getGamepadDatas()) {
            gamepadData.update(gameWindow);
        }
        if (jamepadControllerManager != null) {
            jamepadControllerManager.update();
        }
    }

    /**
     * <p>Getter for the field <code>gamepadDatas</code>.</p>
     *
     * @return return
     */
    public ArrayList<AbstractGamepadData> getGamepadDatas() {
        return gamepadDatas;
    }

}
