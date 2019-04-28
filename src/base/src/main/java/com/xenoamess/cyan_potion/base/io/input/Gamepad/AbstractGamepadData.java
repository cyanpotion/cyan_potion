package com.xenoamess.cyan_potion.base.io.input.Gamepad;

import com.xenoamess.cyan_potion.base.GameWindow;

/**
 * @author XenoAmess
 */
public abstract class AbstractGamepadData {

    private final AbstractGamepadDevice gamepadDevice;

    public AbstractGamepadData(AbstractGamepadDevice gamepadDevice) {
        this.gamepadDevice = gamepadDevice;
    }

    public AbstractGamepadDevice getGamepadDevice() {
        return this.gamepadDevice;
    }

    public abstract void updateGamepadStatus(GameWindow gameWindow);

    public abstract void reset();

    public void update(GameWindow gameWindow) {
        AbstractGamepadDevice gamepadDevice = this.getGamepadDevice();
        if (gamepadDevice != null) {
            gamepadDevice.update();
        }

        if (gamepadDevice == null || !gamepadDevice.isConnected()) {
            this.reset();
        } else {
            updateGamepadStatus(gameWindow);
        }
    }


}
