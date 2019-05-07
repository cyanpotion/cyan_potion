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

import com.codedisaster.steamworks.SteamController;
import com.codedisaster.steamworks.SteamControllerHandle;
import com.github.strikerx3.jxinput.XInputAxes;
import com.github.strikerx3.jxinput.XInputButtons;
import com.github.strikerx3.jxinput.XInputComponents;
import com.github.strikerx3.jxinput.XInputDevice;
import com.github.strikerx3.jxinput.exceptions.XInputNotLoadedException;
import com.xenoamess.cyan_potion.base.DataCenter;
import com.xenoamess.cyan_potion.base.GameWindow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

/**
 * @author XenoAmess
 */
public class GamepadInput {
    private static final Logger LOGGER =
            LoggerFactory.getLogger(GamepadInput.class);
    private ArrayList<AbstractGamepadData> gamepadDatas;

    public GamepadInput() {
//        this.gamepadDevice = new ArrayList<>();
        this.setGamepadDatas(new ArrayList<>());
        try {
            int jXInputDeviceNum = XInputDevice.getAllDevices().length;
            for (int i = 0; i < jXInputDeviceNum; i++) {
                AbstractGamepadDevice jXInputGamepadDevice =
                        new JXInputGamepadDevice(i);
//                gamepadDevice.add(jXInputGamepadDevice);
                getGamepadDatas().add(new JXInputGamepadData(jXInputGamepadDevice));
            }
        } catch (XInputNotLoadedException e) {
            e.printStackTrace();
        }

        if (DataCenter.RUN_WITH_STEAM == true) {
            SteamControllerHandle[] steamControllerHandles =
                    new SteamControllerHandle[SteamController.STEAM_CONTROLLER_MAX_COUNT];
            SteamController steamController = new SteamController();
            int steamControllerNum =
                    steamController.getConnectedControllers(steamControllerHandles);
//        System.out.println("steamControllerNum : " + steamControllerNum);
            LOGGER.debug("steamControllerNum : {}", steamControllerNum);

//        for (int i = 0; i < steamControllerNum; i++) {
////            steamControllerHandles[i];
//        }

//        SteamController steamController = ;
        }

    }

    public void update(GameWindow gameWindow) {
        for (AbstractGamepadData gamepadData : this.getGamepadDatas()) {
            gamepadData.update(gameWindow);
        }
    }

    public ArrayList<AbstractGamepadData> getGamepadDatas() {
        return gamepadDatas;
    }

    public void setGamepadDatas(ArrayList<AbstractGamepadData> gamepadDatas) {
        this.gamepadDatas = gamepadDatas;
    }
}
