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

//deleted due to https://stackoverflow.com/questions/6195234/pick-up-native-jni-files-in-maven-test-lwjgl

//package com.xenoamess.cyan_potion.base.io.input.gamepad;
//
//import com.fasterxml.jackson.annotation.JsonIgnore;
//import com.xenoamess.cyan_potion.SDL_GameControllerDB_Util;
//import org.junit.jupiter.api.Test;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.nio.ByteBuffer;
//import java.nio.FloatBuffer;
//
//import static org.lwjgl.glfw.GLFW.*;
//
//public class GamepadTest {
//    @JsonIgnore
//    private static final transient Logger LOGGER =
//            LoggerFactory.getLogger(GamepadTest.class);
//
//    /**
//     * glfw joysticks can work but can only accept buttons
//     * but cannot set vibrations.
//     * thus this is not used and become deprecated.
//     *
//     * @since 0.142.7
//     */
//    @Test
//    public void testGlfwJoyStick() {
//        glfwInit();
//        glfwUpdateGamepadMappings(SDL_GameControllerDB_Util.getSDL_GameControllerDB_ByteBuffer());
//        boolean present = glfwJoystickPresent(GLFW_JOYSTICK_1);
//        if (present) {
//            LOGGER.debug("GLFW_JOYSTICK_1 present : {}", present);
//            LOGGER.debug("GLFW_JOYSTICK_1 is gamepad : {}",
//                    glfwJoystickIsGamepad(GLFW_JOYSTICK_1));
//            FloatBuffer axes = glfwGetJoystickAxes(GLFW_JOYSTICK_1);
//            LOGGER.debug("axes : ");
//            LOGGER.debug("0 : {}", axes.get(0));
//            LOGGER.debug("1 : {}", axes.get(1));
//            LOGGER.debug("2 : {}", axes.get(2));
//            LOGGER.debug("3 : {}", axes.get(3));
//
//            ByteBuffer buttons = glfwGetJoystickButtons(GLFW_JOYSTICK_1);
//            LOGGER.debug("buttons : ");
//            for (int i = 0; i < GLFW_JOYSTICK_LAST; i++) {
//                LOGGER.debug("{} : {}", i, buttons.get(i));
//            }
//            String name = glfwGetJoystickName(GLFW_JOYSTICK_1);
//            LOGGER.debug("GLFW_JOYSTICK_1 name : {}", name);
//            ByteBuffer hats = glfwGetJoystickHats(GLFW_JOYSTICK_1);
//            LOGGER.debug("hats : {}", hats.get(0));
//        }
//    }
//}
