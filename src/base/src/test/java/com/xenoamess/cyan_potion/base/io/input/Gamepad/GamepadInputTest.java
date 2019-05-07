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

/**
 * @author XenoAmess
 */
public class GamepadInputTest {
    @Test
    public static void testGamepadInput() {
        XInputDevice[] devices = null;
        try {
            devices = XInputDevice.getAllDevices();
        } catch (XInputNotLoadedException e) {
            e.printStackTrace();
        }
        assert (devices != null);
        System.out.println("devices count : " + devices.length);
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
            // or devices[0]

        } catch (XInputNotLoadedException e) {
            e.printStackTrace();
        }

        int nxt = 0;

//        device.setVibration(0, 0);
//        device.setVibration(300, 300);

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

            System.out.println("print start");
            System.out.println(buttons.a);
            System.out.println(buttons.b);
            System.out.println(buttons.x);
            System.out.println(buttons.y);
            System.out.println(buttons.back);
            System.out.println(buttons.start);
            System.out.println(buttons.lShoulder);
            System.out.println(buttons.rShoulder);
            System.out.println(buttons.lThumb);
            System.out.println(buttons.rThumb);
            System.out.println(buttons.up);
            System.out.println(buttons.down);
            System.out.println(buttons.left);
            System.out.println(buttons.right);
            System.out.println(buttons.guide);
            System.out.println(buttons.unknown);

            System.out.println();

            System.out.println(axes.lxRaw);
            System.out.println(axes.lyRaw);
            System.out.println(axes.rxRaw);
            System.out.println(axes.ryRaw);
            System.out.println(axes.ltRaw);
            System.out.println(axes.rtRaw);

            System.out.println();

            System.out.println(axes.lx);
            System.out.println(axes.ly);
            System.out.println(axes.rx);
            System.out.println(axes.ry);
            System.out.println(axes.lt);
            System.out.println(axes.rt);
            System.out.println(axes.dpad);

            // Buttons and axes have public fields (although this is not
            // idiomatic Java)

//            // Retrieve button state
//            if (buttons.a) {
//                // The A button is currently pressed
//            }
//
//            // Check if Guide button is supported
//            if (XInputDevice.isGuideButtonSupported()) {
//                // Use it
//                if (buttons.guide) {
//                    // The Guide button is currently pressed
//                }
//            }

        }
    }
}
