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

import com.codedisaster.steamworks.SteamController;
import com.codedisaster.steamworks.SteamControllerHandle;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.strikerx3.jxinput.XInputDevice;
import com.github.strikerx3.jxinput.exceptions.XInputNotLoadedException;
import com.studiohartman.jamepad.ControllerManager;
import com.xenoamess.cyan_potion.base.GameManager;
import com.xenoamess.cyan_potion.base.SubManager;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

/**
 * <p>GamepadInput class.</p>
 *
 * @author XenoAmess
 * @version 0.161.2-SNAPSHOT
 */
@EqualsAndHashCode(callSuper = true)
@ToString
public class GamepadInputManager extends SubManager {
    @JsonIgnore
    private static final transient Logger LOGGER =
            LoggerFactory.getLogger(GamepadInputManager.class);
    @Getter
    @Setter
    private final ArrayList<AbstractGamepadData> gamepadDatas = new ArrayList<>();
    @Getter
    @Setter
    private ControllerManager jamepadControllerManager = null;

    /**
     * <p>Constructor for GamepadInputManager.</p>
     *
     * @param gameManager a {@link com.xenoamess.cyan_potion.base.GameManager} object.
     */
    public GamepadInputManager(GameManager gameManager) {
        super(gameManager);
    }

    /**
     * {@inheritDoc}
     *
     * <p>init.</p>
     */
    @Override
    public void init() {
        if (this.getGameManager().getDataCenter().isUsingJXInput()) {
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
            this.setJamepadControllerManager(new ControllerManager());
            getJamepadControllerManager().initSDLGamepad();

            int jamepadDeviceNum = getJamepadControllerManager().getNumControllers();
            for (int i = 0; i < jamepadDeviceNum; i++) {
                AbstractGamepadDevice jamepadGamepadDevice =
                        new JamepadGamepadDevice(this.getJamepadControllerManager(), i);
                getGamepadDatas().add(new JamepadGamepadData(jamepadGamepadDevice));
            }
        }

        if (this.getGameManager().getSteamManager().isRunWithSteam()) {
            SteamControllerHandle[] steamControllerHandles =
                    new SteamControllerHandle[SteamController.STEAM_CONTROLLER_MAX_COUNT];
            SteamController steamController = new SteamController();
            int steamControllerNum =
                    steamController.getConnectedControllers(steamControllerHandles);
            LOGGER.debug("steamControllerNum : {}", steamControllerNum);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() {
        if (getJamepadControllerManager() != null) {
            getJamepadControllerManager().quitSDLGamepad();
        }
    }

    /**
     * <p>update.</p>
     */
    public void update() {
        for (AbstractGamepadData gamepadData : this.getGamepadDatas()) {
            gamepadData.update(this.getGameManager().getGameWindow());
        }
        if (getJamepadControllerManager() != null) {
            getJamepadControllerManager().update();
        }
    }
}
