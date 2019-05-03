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
import com.xenoamess.cyan_potion.base.GameWindow;
import com.xenoamess.cyan_potion.base.events.GamepadButtonEvent;

/**
 * this class is used as data class, (struct)
 * thus it will not be fully encapsulated.
 *
 * @author XenoAmess
 */
public class JXInputGamepadData extends AbstractGamepadData {
    public static final int JXINPUT_KEY_A = 0;
    public static final int JXINPUT_KEY_B = 1;
    public static final int JXINPUT_KEY_X = 2;
    public static final int JXINPUT_KEY_Y = 3;
    public static final int JXINPUT_KEY_BACK = 4;
    public static final int JXINPUT_KEY_START = 5;
    public static final int JXINPUT_KEY_LB = 6;
    public static final int JXINPUT_KEY_RB = 7;
    public static final int JXINPUT_KEY_L = 8;
    public static final int JXINPUT_KEY_R = 9;
    public static final int JXINPUT_KEY_UP = 10;
    public static final int JXINPUT_KEY_DOWN = 11;
    public static final int JXINPUT_KEY_LEFT = 12;
    public static final int JXINPUT_KEY_RIGHT = 13;
    public static final int JXINPUT_KEY_GUIDE = 14;
    public static final int JXINPUT_KEY_UNKNOWN = 15;
    public static final int JXINPUT_KEY_LT = 16;
    public static final int JXINPUT_KEY_RT = 17;
    public static final int JXINPUT_KEY_LAST = 17;


    public static final int DPAD_CENTER = -1;
    public static final int DPAD_UP_LEFT = 0;
    public static final int DPAD_UP = 1;
    public static final int DPAD_UP_RIGHT = 2;
    public static final int DPAD_RIGHT = 3;
    public static final int DPAD_DOWN_RIGHT = 4;
    public static final int DPAD_DOWN = 5;
    public static final int DPAD_DOWN_LEFT = 6;
    public static final int DPAD_LEFT = 7;


    public boolean a;
    public boolean b;
    public boolean x;
    public boolean y;
    public boolean back;
    public boolean start;
    public boolean lShoulder;
    public boolean rShoulder;
    public boolean lThumb;
    public boolean rThumb;
    public boolean up;
    public boolean down;
    public boolean left;
    public boolean right;
    public boolean guide;
    public boolean unknown;

    public int lxRaw, lyRaw;
    public int rxRaw, ryRaw;
    public int ltRaw, rtRaw;
    public float lx, ly;
    public float rx, ry;
    public float lt, rt;
    public int dpad;


    /**
     * Returns an integer representing the current direction of the D-Pad.
     *
     * @param up    the up button state
     * @param down  the down button state
     * @param left  the left button state
     * @param right the right button state
     * @return one of the <code>DPAD_*</code> values of this class
     */
    public static int dpadFromButtons(final boolean up, final boolean down,
                                      final boolean left, final boolean right) {
        boolean u = up;
        boolean d = down;
        boolean l = left;
        boolean r = right;

        // Fix invalid buttons (cancel up-down and left-right)
        if (u && d) {
            u = d = false;
        }
        if (l && r) {
            l = r = false;
        }

        // Now we have 9 cases:
        //         left             center        right
        // up      DPAD_UP_LEFT     DPAD_UP       DPAD_UP_RIGHT
        // center  DPAD_LEFT        DPAD_CENTER   DPAD_RIGHT
        // down    DPAD_DOWN_LEFT   DPAD_DOWN     DPAD_DOWN_RIGHT

        if (u) {
            if (l) {
                return DPAD_UP_LEFT;
            }
            if (r) {
                return DPAD_UP_RIGHT;
            }
            return DPAD_UP;
        }
        if (d) {
            if (l) {
                return DPAD_DOWN_LEFT;
            }
            if (r) {
                return DPAD_DOWN_RIGHT;
            }
            return DPAD_DOWN;
        }
        // vertical center
        if (l) {
            return DPAD_LEFT;
        }
        if (r) {
            return DPAD_RIGHT;
        }
        return DPAD_CENTER;
    }


    @Override
    public void reset() {
        this.a = this.b = this.x = this.y = false;
        this.back = this.start = false;
        this.lShoulder = this.rShoulder = false;
        this.lThumb = this.rThumb = false;
        this.up = this.down = this.left = this.right = false;
        this.guide = this.unknown = false;
        this.lxRaw = this.lyRaw = 0;
        this.rxRaw = this.ryRaw = 0;
        this.ltRaw = this.rtRaw = 0;
        this.lx = this.ly = 0f;
        this.rx = this.ry = 0f;
        this.lt = this.rt = 0f;
        this.dpad = DPAD_CENTER;
    }

    protected void copy(final XInputButtons buttons) {
        this.a = buttons.a;
        this.b = buttons.b;
        this.x = buttons.x;
        this.y = buttons.y;
        this.back = buttons.back;
        this.start = buttons.start;
        this.lShoulder = buttons.lShoulder;
        this.rShoulder = buttons.rShoulder;
        this.lThumb = buttons.lThumb;
        this.rThumb = buttons.rThumb;
        this.up = buttons.up;
        this.down = buttons.down;
        this.left = buttons.left;
        this.right = buttons.right;
        this.guide = buttons.guide;
        this.unknown = buttons.unknown;
    }

    protected void copy(final XInputAxes axes) {
        this.lxRaw = axes.lxRaw;
        this.lyRaw = axes.lyRaw;
        this.rxRaw = axes.rxRaw;
        this.ryRaw = axes.ryRaw;
        this.ltRaw = axes.ltRaw;
        this.rtRaw = axes.rtRaw;
        this.lx = axes.lx;
        this.ly = axes.ly;
        this.rx = axes.rx;
        this.ry = axes.ry;
        this.lt = axes.lt;
        this.rt = axes.rt;
        this.dpad = axes.dpad;
    }


    public JXInputGamepadData(AbstractGamepadDevice gamepadDevice) {
        super(gamepadDevice);
    }


    @Override
    public void updateGamepadStatus(GameWindow gameWindow) {
        XInputComponents components =
                ((JXInputGamepadDevice) this.getGamepadDevice()).getRawXInputDevice().getComponents();
        XInputButtons buttons = components.getButtons();
        XInputAxes axes = components.getAxes();
//        axes.
        long window = gameWindow.getWindow();
        int key;
        int action;

//        System.out.println("this.a : " + this.a);
//        System.out.println("buttons.a : " + buttons.a);

        if (this.a != buttons.a) {
            if (buttons.a) {
                action = 1;
                gameWindow.getGameManager().eventListAdd(new GamepadButtonEvent(window, JXINPUT_KEY_A, action,
                        this.getGamepadDevice()));
            } else {
                action = 0;
                gameWindow.getGameManager().eventListAdd(new GamepadButtonEvent(window, JXINPUT_KEY_A, action,
                        this.getGamepadDevice()));
            }
        } else if (buttons.a) {
            action = 2;
            gameWindow.getGameManager().eventListAdd(new GamepadButtonEvent(window, JXINPUT_KEY_A, action,
                    this.getGamepadDevice()));
        }


        if (this.b != buttons.b) {
            if (buttons.b) {
                action = 1;
                gameWindow.getGameManager().eventListAdd(new GamepadButtonEvent(window, JXINPUT_KEY_B, action,
                        this.getGamepadDevice()));
            } else {
                action = 0;
                gameWindow.getGameManager().eventListAdd(new GamepadButtonEvent(window, JXINPUT_KEY_B, action,
                        this.getGamepadDevice()));
            }
        } else if (buttons.b) {
            action = 2;
            gameWindow.getGameManager().eventListAdd(new GamepadButtonEvent(window, JXINPUT_KEY_B, action,
                    this.getGamepadDevice()));
        }

        if (this.x != buttons.x) {
            if (buttons.x) {
                action = 1;
                gameWindow.getGameManager().eventListAdd(new GamepadButtonEvent(window, JXINPUT_KEY_X, action,
                        this.getGamepadDevice()));
            } else {
                action = 0;
                gameWindow.getGameManager().eventListAdd(new GamepadButtonEvent(window, JXINPUT_KEY_X, action,
                        this.getGamepadDevice()));
            }
        } else if (buttons.x) {
            action = 2;
            gameWindow.getGameManager().eventListAdd(new GamepadButtonEvent(window, JXINPUT_KEY_X, action,
                    this.getGamepadDevice()));
        }

        if (this.y != buttons.y) {
            if (buttons.y) {
                action = 1;
                gameWindow.getGameManager().eventListAdd(new GamepadButtonEvent(window, JXINPUT_KEY_Y, action,
                        this.getGamepadDevice()));
            } else {
                action = 0;
                gameWindow.getGameManager().eventListAdd(new GamepadButtonEvent(window, JXINPUT_KEY_Y, action,
                        this.getGamepadDevice()));
            }
        } else if (buttons.y) {
            action = 2;
            gameWindow.getGameManager().eventListAdd(new GamepadButtonEvent(window, JXINPUT_KEY_Y, action,
                    this.getGamepadDevice()));
        }

        if (this.back != buttons.back) {
            if (buttons.back) {
                action = 1;
                gameWindow.getGameManager().eventListAdd(new GamepadButtonEvent(window, JXINPUT_KEY_BACK, action,
                        this.getGamepadDevice()));
            } else {
                action = 0;
                gameWindow.getGameManager().eventListAdd(new GamepadButtonEvent(window, JXINPUT_KEY_BACK, action,
                        this.getGamepadDevice()));
            }
        } else if (buttons.back) {
            action = 2;
            gameWindow.getGameManager().eventListAdd(new GamepadButtonEvent(window, JXINPUT_KEY_BACK, action,
                    this.getGamepadDevice()));
        }

        if (this.start != buttons.start) {
            if (buttons.start) {
                action = 1;
                gameWindow.getGameManager().eventListAdd(new GamepadButtonEvent(window, JXINPUT_KEY_START, action,
                        this.getGamepadDevice()));
            } else {
                action = 0;
                gameWindow.getGameManager().eventListAdd(new GamepadButtonEvent(window, JXINPUT_KEY_START, action,
                        this.getGamepadDevice()));
            }
        } else if (buttons.start) {
            action = 2;
            gameWindow.getGameManager().eventListAdd(new GamepadButtonEvent(window, JXINPUT_KEY_START, action,
                    this.getGamepadDevice()));
        }

        if (this.lShoulder != buttons.lShoulder) {
            if (buttons.lShoulder) {
                action = 1;
                gameWindow.getGameManager().eventListAdd(new GamepadButtonEvent(window, JXINPUT_KEY_LB, action,
                        this.getGamepadDevice()));
            } else {
                action = 0;
                gameWindow.getGameManager().eventListAdd(new GamepadButtonEvent(window, JXINPUT_KEY_LB, action,
                        this.getGamepadDevice()));
            }
        } else if (buttons.lShoulder) {
            action = 2;
            gameWindow.getGameManager().eventListAdd(new GamepadButtonEvent(window, JXINPUT_KEY_LB, action,
                    this.getGamepadDevice()));
        }

        if (this.rShoulder != buttons.rShoulder) {
            if (buttons.rShoulder) {
                action = 1;
                gameWindow.getGameManager().eventListAdd(new GamepadButtonEvent(window, JXINPUT_KEY_RB, action,
                        this.getGamepadDevice()));
            } else {
                action = 0;
                gameWindow.getGameManager().eventListAdd(new GamepadButtonEvent(window, JXINPUT_KEY_RB, action,
                        this.getGamepadDevice()));
            }
        } else if (buttons.rShoulder) {
            action = 2;
            gameWindow.getGameManager().eventListAdd(new GamepadButtonEvent(window, JXINPUT_KEY_RB, action,
                    this.getGamepadDevice()));
        }

        if (this.lThumb != buttons.lThumb) {
            if (buttons.lThumb) {
                action = 1;
                gameWindow.getGameManager().eventListAdd(new GamepadButtonEvent(window, JXINPUT_KEY_L, action,
                        this.getGamepadDevice()));
            } else {
                action = 0;
                gameWindow.getGameManager().eventListAdd(new GamepadButtonEvent(window, JXINPUT_KEY_L, action,
                        this.getGamepadDevice()));
            }
        } else if (buttons.lThumb) {
            action = 2;
            gameWindow.getGameManager().eventListAdd(new GamepadButtonEvent(window, JXINPUT_KEY_L, action,
                    this.getGamepadDevice()));
        }

        if (this.rThumb != buttons.rThumb) {
            if (buttons.rThumb) {
                action = 1;
                gameWindow.getGameManager().eventListAdd(new GamepadButtonEvent(window, JXINPUT_KEY_R, action,
                        this.getGamepadDevice()));
            } else {
                action = 0;
                gameWindow.getGameManager().eventListAdd(new GamepadButtonEvent(window, JXINPUT_KEY_R, action,
                        this.getGamepadDevice()));
            }
        } else if (buttons.rThumb) {
            action = 2;
            gameWindow.getGameManager().eventListAdd(new GamepadButtonEvent(window, JXINPUT_KEY_R, action,
                    this.getGamepadDevice()));
        }

        if (this.up != buttons.up) {
            if (buttons.up) {
                action = 1;
                gameWindow.getGameManager().eventListAdd(new GamepadButtonEvent(window, JXINPUT_KEY_UP, action,
                        this.getGamepadDevice()));
            } else {
                action = 0;
                gameWindow.getGameManager().eventListAdd(new GamepadButtonEvent(window, JXINPUT_KEY_UP, action,
                        this.getGamepadDevice()));
            }
        } else if (buttons.up) {
            action = 2;
            gameWindow.getGameManager().eventListAdd(new GamepadButtonEvent(window, JXINPUT_KEY_UP, action,
                    this.getGamepadDevice()));
        }

        if (this.down != buttons.down) {
            if (buttons.down) {
                action = 1;
                gameWindow.getGameManager().eventListAdd(new GamepadButtonEvent(window, JXINPUT_KEY_DOWN, action,
                        this.getGamepadDevice()));
            } else {
                action = 0;
                gameWindow.getGameManager().eventListAdd(new GamepadButtonEvent(window, JXINPUT_KEY_DOWN, action,
                        this.getGamepadDevice()));
            }
        } else if (buttons.down) {
            action = 2;
            gameWindow.getGameManager().eventListAdd(new GamepadButtonEvent(window, JXINPUT_KEY_DOWN, action,
                    this.getGamepadDevice()));
        }

        if (this.left != buttons.left) {
            if (buttons.left) {
                action = 1;
                gameWindow.getGameManager().eventListAdd(new GamepadButtonEvent(window, JXINPUT_KEY_LEFT, action,
                        this.getGamepadDevice()));
            } else {
                action = 0;
                gameWindow.getGameManager().eventListAdd(new GamepadButtonEvent(window, JXINPUT_KEY_LEFT, action,
                        this.getGamepadDevice()));
            }
        } else if (buttons.left) {
            action = 2;
            gameWindow.getGameManager().eventListAdd(new GamepadButtonEvent(window, JXINPUT_KEY_LEFT, action,
                    this.getGamepadDevice()));
        }

        if (this.right != buttons.right) {
            if (buttons.right) {
                action = 1;
                gameWindow.getGameManager().eventListAdd(new GamepadButtonEvent(window, JXINPUT_KEY_RIGHT, action,
                        this.getGamepadDevice()));
            } else {
                action = 0;
                gameWindow.getGameManager().eventListAdd(new GamepadButtonEvent(window, JXINPUT_KEY_RIGHT, action,
                        this.getGamepadDevice()));
            }
        } else if (buttons.right) {
            action = 2;
            gameWindow.getGameManager().eventListAdd(new GamepadButtonEvent(window, JXINPUT_KEY_RIGHT, action,
                    this.getGamepadDevice()));
        }

        if (this.guide != buttons.guide) {
            if (buttons.guide) {
                action = 1;
                gameWindow.getGameManager().eventListAdd(new GamepadButtonEvent(window, JXINPUT_KEY_GUIDE, action,
                        this.getGamepadDevice()));
            } else {
                action = 0;
                gameWindow.getGameManager().eventListAdd(new GamepadButtonEvent(window, JXINPUT_KEY_GUIDE, action,
                        this.getGamepadDevice()));
            }
        } else if (buttons.guide) {
            action = 2;
            gameWindow.getGameManager().eventListAdd(new GamepadButtonEvent(window, JXINPUT_KEY_GUIDE, action,
                    this.getGamepadDevice()));
        }

        if (this.unknown != buttons.unknown) {
            if (buttons.unknown) {
                action = 1;
                gameWindow.getGameManager().eventListAdd(new GamepadButtonEvent(window, JXINPUT_KEY_UNKNOWN, action,
                        this.getGamepadDevice()));
            } else {
                action = 0;
                gameWindow.getGameManager().eventListAdd(new GamepadButtonEvent(window, JXINPUT_KEY_UNKNOWN, action,
                        this.getGamepadDevice()));
            }
        } else if (buttons.unknown) {
            action = 2;
            gameWindow.getGameManager().eventListAdd(new GamepadButtonEvent(window, JXINPUT_KEY_UNKNOWN, action,
                    this.getGamepadDevice()));
        }

        boolean ifltNow = (this.lt > 0.5f);
        boolean ifltNext = (axes.lt > 0.5f);

        if (ifltNow != ifltNext) {
            if (ifltNext) {
                action = 1;
                gameWindow.getGameManager().eventListAdd(new GamepadButtonEvent(window, JXINPUT_KEY_LT, action,
                        this.getGamepadDevice()));
            } else {
                action = 0;
                gameWindow.getGameManager().eventListAdd(new GamepadButtonEvent(window, JXINPUT_KEY_LT, action,
                        this.getGamepadDevice()));
            }
        } else if (ifltNext) {
            action = 2;
            gameWindow.getGameManager().eventListAdd(new GamepadButtonEvent(window, JXINPUT_KEY_LT, action,
                    this.getGamepadDevice()));
        }

        boolean ifrtNow = (this.rt > 0.5f);
        boolean ifrtNext = (axes.rt > 0.5f);

        if (ifrtNow != ifrtNext) {
            if (ifrtNext) {
                action = 1;
                gameWindow.getGameManager().eventListAdd(new GamepadButtonEvent(window, JXINPUT_KEY_RT, action,
                        this.getGamepadDevice()));
            } else {
                action = 0;
                gameWindow.getGameManager().eventListAdd(new GamepadButtonEvent(window, JXINPUT_KEY_RT, action,
                        this.getGamepadDevice()));
            }
        } else if (ifrtNext) {
            action = 2;
            gameWindow.getGameManager().eventListAdd(new GamepadButtonEvent(window, JXINPUT_KEY_RT, action,
                    this.getGamepadDevice()));
        }

        this.copy(buttons);
        this.copy(axes);
    }
}
