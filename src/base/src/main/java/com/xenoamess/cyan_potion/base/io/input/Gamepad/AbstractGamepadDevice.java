package com.xenoamess.cyan_potion.base.io.input.Gamepad;

/**
 * @author XenoAmess
 */
public abstract class AbstractGamepadDevice {
    public static final int MIN_VIBRATION_POWER = 0;
    public static final int MAX_VIBRATION_POWER = 65535;

    public abstract void update();

    public abstract boolean isConnected();

    public abstract void setVibration(int leftVibration, int rightVibration);

    public static int fixVibrationPower(int originalVibrationPower) {
        if (originalVibrationPower < MIN_VIBRATION_POWER) {
            originalVibrationPower = MIN_VIBRATION_POWER;
        }
        if (originalVibrationPower > MAX_VIBRATION_POWER) {
            originalVibrationPower = MAX_VIBRATION_POWER;
        }
        return originalVibrationPower;
    }

}
