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
    private static final Logger LOGGER = LoggerFactory.getLogger(GamepadInput.class);
    private ArrayList<AbstractGamepadData> gamepadDatas;

    public GamepadInput() {
//        this.gamepadDevice = new ArrayList<>();
        this.setGamepadDatas(new ArrayList<>());
        try {
            int jXInputDeviceNum = XInputDevice.getAllDevices().length;
            for (int i = 0; i < jXInputDeviceNum; i++) {
                AbstractGamepadDevice jXInputGamepadDevice = new JXInputGamepadDevice(i);
//                gamepadDevice.add(jXInputGamepadDevice);
                getGamepadDatas().add(new JXInputGamepadData(jXInputGamepadDevice));
            }
        } catch (XInputNotLoadedException e) {
            e.printStackTrace();
        }

        if (DataCenter.RUN_WITH_STEAM == true) {
            SteamControllerHandle[] steamControllerHandles = new SteamControllerHandle[SteamController.STEAM_CONTROLLER_MAX_COUNT];
            SteamController steamController = new SteamController();
            int steamControllerNum = steamController.getConnectedControllers(steamControllerHandles);
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


    public static void main(String[] args) {
        XInputDevice[] devices = null;
        try {
            devices = XInputDevice.getAllDevices();
        } catch (XInputNotLoadedException e) {
            e.printStackTrace();
        }
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

// Whenever the device is polled, listener events will be fired as long as there are changes

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

            // Buttons and axes have public fields (although this is not idiomatic Java)

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


    public ArrayList<AbstractGamepadData> getGamepadDatas() {
        return gamepadDatas;
    }

    public void setGamepadDatas(ArrayList<AbstractGamepadData> gamepadDatas) {
        this.gamepadDatas = gamepadDatas;
    }
}
