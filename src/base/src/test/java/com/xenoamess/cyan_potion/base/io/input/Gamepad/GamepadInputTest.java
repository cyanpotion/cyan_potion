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

import com.github.strikerx3.jxinput.XInputAxes;
import com.github.strikerx3.jxinput.XInputButtons;
import com.github.strikerx3.jxinput.XInputComponents;
import com.github.strikerx3.jxinput.XInputDevice;
import com.github.strikerx3.jxinput.exceptions.XInputNotLoadedException;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author XenoAmess
 */
public class GamepadInputTest {
    private static final Logger LOGGER =
            LoggerFactory.getLogger(GamepadInputTest.class);

    @Test
    public static void testGamepadInput() {
        XInputDevice[] devices = null;
        try {
            devices = XInputDevice.getAllDevices();
        } catch (XInputNotLoadedException e) {
            LOGGER.error("XInputDevice.getAllDevices() fails", e);
        }
        assert (devices != null);
        LOGGER.debug("devices count : " + devices.length);
        int nowi = 0;
        for (int i = 0; i < devices.length; i++) {
            if (devices[i].isConnected()) {
                nowi = i;
            }
        }

        XInputDevice device = null;

        // Retrieve the device for player 1

        try {
            device = XInputDevice.getDeviceFor(nowi);
        } catch (XInputNotLoadedException e) {
            LOGGER.error("XInputDevice.getDeviceFor(nowi) returns null", nowi, e);
        }
        assert (device != null);

        int nxt = 0;

        // Whenever the device is polled, listener events will be fired as long as
        // there are changes

        while (true) {
            device.setVibration(65535, 65535);
            device.setVibration(0, 0);
            device.poll();

            nxt++;
            if (nxt == 1000000) {
                nxt = 0;
            } else {
                continue;
            }
            // Retrieve the components
            XInputComponents components = device.getComponents();
            XInputButtons buttons = components.getButtons();

            XInputAxes axes = components.getAxes();

            LOGGER.debug("print start");
            LOGGER.debug("" + buttons.a);
            LOGGER.debug("" + buttons.b);
            LOGGER.debug("" + buttons.x);
            LOGGER.debug("" + buttons.y);
            LOGGER.debug("" + buttons.back);
            LOGGER.debug("" + buttons.start);
            LOGGER.debug("" + buttons.lShoulder);
            LOGGER.debug("" + buttons.rShoulder);
            LOGGER.debug("" + buttons.lThumb);
            LOGGER.debug("" + buttons.rThumb);
            LOGGER.debug("" + buttons.up);
            LOGGER.debug("" + buttons.down);
            LOGGER.debug("" + buttons.left);
            LOGGER.debug("" + buttons.right);
            LOGGER.debug("" + buttons.guide);
            LOGGER.debug("" + buttons.unknown);
            LOGGER.debug("");

            LOGGER.debug("" + axes.lxRaw);
            LOGGER.debug("" + axes.lyRaw);
            LOGGER.debug("" + axes.rxRaw);
            LOGGER.debug("" + axes.ryRaw);
            LOGGER.debug("" + axes.ltRaw);
            LOGGER.debug("" + axes.rtRaw);

            LOGGER.debug("");

            LOGGER.debug("" + axes.lx);
            LOGGER.debug("" + axes.ly);
            LOGGER.debug("" + axes.rx);
            LOGGER.debug("" + axes.ry);
            LOGGER.debug("" + axes.lt);
            LOGGER.debug("" + axes.rt);
            LOGGER.debug("" + axes.dpad);

        }
    }
}