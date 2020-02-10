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

import com.studiohartman.jamepad.ControllerManager;
import com.studiohartman.jamepad.ControllerState;
import com.xenoamess.cyan_potion.base.GameWindow;

/**
 * this class is used as data class, (struct)
 * thus it will not be fully encapsulated.
 *
 * @author XenoAmess
 * @version 0.156.1-SNAPSHOT
 * @see JamepadGamepadDevice
 */
public class JamepadGamepadData extends AbstractGamepadData {
    /**
     * Constant <code>JAMEPAD_KEY_A=0</code>
     */
    public static final int JAMEPAD_KEY_A = 0;
    /**
     * Constant <code>JAMEPAD_KEY_B=1</code>
     */
    public static final int JAMEPAD_KEY_B = 1;
    /**
     * Constant <code>JAMEPAD_KEY_X=2</code>
     */
    public static final int JAMEPAD_KEY_X = 2;
    /**
     * Constant <code>JAMEPAD_KEY_Y=3</code>
     */
    public static final int JAMEPAD_KEY_Y = 3;
    /**
     * Constant <code>JAMEPAD_KEY_BACK=4</code>
     */
    public static final int JAMEPAD_KEY_BACK = 4;
    /**
     * Constant <code>JAMEPAD_KEY_START=5</code>
     */
    public static final int JAMEPAD_KEY_START = 5;
    /**
     * Constant <code>JAMEPAD_KEY_LB=6</code>
     */
    public static final int JAMEPAD_KEY_LB = 6;
    /**
     * Constant <code>JAMEPAD_KEY_RB=7</code>
     */
    public static final int JAMEPAD_KEY_RB = 7;
    /**
     * Constant <code>JAMEPAD_KEY_L=8</code>
     */
    public static final int JAMEPAD_KEY_L = 8;
    /**
     * Constant <code>JAMEPAD_KEY_R=9</code>
     */
    public static final int JAMEPAD_KEY_R = 9;
    /**
     * Constant <code>JAMEPAD_KEY_UP=10</code>
     */
    public static final int JAMEPAD_KEY_UP = 10;
    /**
     * Constant <code>JAMEPAD_KEY_DOWN=11</code>
     */
    public static final int JAMEPAD_KEY_DOWN = 11;
    /**
     * Constant <code>JAMEPAD_KEY_LEFT=12</code>
     */
    public static final int JAMEPAD_KEY_LEFT = 12;
    /**
     * Constant <code>JAMEPAD_KEY_RIGHT=13</code>
     */
    public static final int JAMEPAD_KEY_RIGHT = 13;
    /**
     * Constant <code>JAMEPAD_KEY_GUIDE=14</code>
     */
    public static final int JAMEPAD_KEY_GUIDE = 14;
    /**
     * Constant <code>JAMEPAD_KEY_UNKNOWN=15</code>
     */
    public static final int JAMEPAD_KEY_UNKNOWN = 15;
    /**
     * Constant <code>JAMEPAD_KEY_LT=16</code>
     */
    public static final int JAMEPAD_KEY_LT = 16;
    /**
     * Constant <code>JAMEPAD_KEY_RT=17</code>
     */
    public static final int JAMEPAD_KEY_RT = 17;
    /**
     * Constant <code>JAMEPAD_KEY_LAST=17</code>
     */
    public static final int JAMEPAD_KEY_LAST = 17;


    /**
     * Constant <code>DPAD_CENTER=-1</code>
     */
    public static final int DPAD_CENTER = -1;
    /**
     * Constant <code>DPAD_UP_LEFT=0</code>
     */
    public static final int DPAD_UP_LEFT = 0;
    /**
     * Constant <code>DPAD_UP=1</code>
     */
    public static final int DPAD_UP = 1;
    /**
     * Constant <code>DPAD_UP_RIGHT=2</code>
     */
    public static final int DPAD_UP_RIGHT = 2;
    /**
     * Constant <code>DPAD_RIGHT=3</code>
     */
    public static final int DPAD_RIGHT = 3;
    /**
     * Constant <code>DPAD_DOWN_RIGHT=4</code>
     */
    public static final int DPAD_DOWN_RIGHT = 4;
    /**
     * Constant <code>DPAD_DOWN=5</code>
     */
    public static final int DPAD_DOWN = 5;
    /**
     * Constant <code>DPAD_DOWN_LEFT=6</code>
     */
    public static final int DPAD_DOWN_LEFT = 6;
    /**
     * Constant <code>DPAD_LEFT=7</code>
     */
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

    public float lx, ly;
    public float rx, ry;
    public float lt, rt;
    public int dpad;

    /**
     * <p>Constructor for JXInputGamepadData.</p>
     *
     * @param gamepadDevice gamepadDevice
     */
    public JamepadGamepadData(AbstractGamepadDevice gamepadDevice) {
        super(gamepadDevice);
    }

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


    /**
     * {@inheritDoc}
     */
    @Override
    public void reset() {
        this.a = this.b = this.x = this.y = false;
        this.back = this.start = false;
        this.lShoulder = this.rShoulder = false;
        this.lThumb = this.rThumb = false;
        this.up = this.down = this.left = this.right = false;
        this.guide = this.unknown = false;
        this.lx = this.ly = 0f;
        this.rx = this.ry = 0f;
        this.lt = this.rt = 0f;
        this.dpad = DPAD_CENTER;
    }

    /**
     * <p>copy.</p>
     *
     * @param jamepadControllerState jamepadControllerState
     */
    protected void copy(final ControllerState jamepadControllerState) {
        this.a = jamepadControllerState.a;
        this.b = jamepadControllerState.b;
        this.x = jamepadControllerState.x;
        this.y = jamepadControllerState.y;
        this.lShoulder = jamepadControllerState.lb;
        this.rShoulder = jamepadControllerState.rb;
        this.start = jamepadControllerState.start;
        this.back = jamepadControllerState.back;
        this.guide = jamepadControllerState.guide;

        this.up = jamepadControllerState.dpadUp;
        this.down = jamepadControllerState.dpadDown;
        this.left = jamepadControllerState.dpadLeft;
        this.right = jamepadControllerState.dpadRight;

        this.lThumb = jamepadControllerState.leftStickClick;
        this.rThumb = jamepadControllerState.rightStickClick;

        this.lx = jamepadControllerState.leftStickX;
        this.ly = jamepadControllerState.leftStickY;
        this.rx = jamepadControllerState.rightStickX;
        this.ry = jamepadControllerState.rightStickY;

        this.lt = jamepadControllerState.leftTrigger;
        this.rt = jamepadControllerState.rightTrigger;
        this.dpad = dpadFromButtons(this.up, this.down, this.left, this.right);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void updateGamepadStatus(GameWindow gameWindow) {
        JamepadGamepadDevice jamepadGamepadDevice = ((JamepadGamepadDevice) this.getGamepadDevice());
        ControllerManager jamepadControllerManager = jamepadGamepadDevice.getJamepadControllerManager();
        ControllerState jamepadControllerState =
                jamepadControllerManager.getState(jamepadGamepadDevice.getJamepadGamepadDeviceIndex());

        JamepadGamepadData tempJamepadGamepadData = new JamepadGamepadData(jamepadGamepadDevice);
        tempJamepadGamepadData.copy(jamepadControllerState);

        //        axes.
        long window = gameWindow.getWindow();
        int action;

        if (this.a != tempJamepadGamepadData.a) {
            if (tempJamepadGamepadData.a) {
                action = 1;
                gameWindow.getGameManager().eventListAdd(new GamepadButtonEvent(window, JAMEPAD_KEY_A, action,
                        this.getGamepadDevice()));
            } else {
                action = 0;
                gameWindow.getGameManager().eventListAdd(new GamepadButtonEvent(window, JAMEPAD_KEY_A, action,
                        this.getGamepadDevice()));
            }
        } else if (tempJamepadGamepadData.a) {
            action = 2;
            gameWindow.getGameManager().eventListAdd(new GamepadButtonEvent(window, JAMEPAD_KEY_A, action,
                    this.getGamepadDevice()));
        }


        if (this.b != tempJamepadGamepadData.b) {
            if (tempJamepadGamepadData.b) {
                action = 1;
                gameWindow.getGameManager().eventListAdd(new GamepadButtonEvent(window, JAMEPAD_KEY_B, action,
                        this.getGamepadDevice()));
            } else {
                action = 0;
                gameWindow.getGameManager().eventListAdd(new GamepadButtonEvent(window, JAMEPAD_KEY_B, action,
                        this.getGamepadDevice()));
            }
        } else if (tempJamepadGamepadData.b) {
            action = 2;
            gameWindow.getGameManager().eventListAdd(new GamepadButtonEvent(window, JAMEPAD_KEY_B, action,
                    this.getGamepadDevice()));
        }

        if (this.x != tempJamepadGamepadData.x) {
            if (tempJamepadGamepadData.x) {
                action = 1;
                gameWindow.getGameManager().eventListAdd(new GamepadButtonEvent(window, JAMEPAD_KEY_X, action,
                        this.getGamepadDevice()));
            } else {
                action = 0;
                gameWindow.getGameManager().eventListAdd(new GamepadButtonEvent(window, JAMEPAD_KEY_X, action,
                        this.getGamepadDevice()));
            }
        } else if (tempJamepadGamepadData.x) {
            action = 2;
            gameWindow.getGameManager().eventListAdd(new GamepadButtonEvent(window, JAMEPAD_KEY_X, action,
                    this.getGamepadDevice()));
        }

        if (this.y != tempJamepadGamepadData.y) {
            if (tempJamepadGamepadData.y) {
                action = 1;
                gameWindow.getGameManager().eventListAdd(new GamepadButtonEvent(window, JAMEPAD_KEY_Y, action,
                        this.getGamepadDevice()));
            } else {
                action = 0;
                gameWindow.getGameManager().eventListAdd(new GamepadButtonEvent(window, JAMEPAD_KEY_Y, action,
                        this.getGamepadDevice()));
            }
        } else if (tempJamepadGamepadData.y) {
            action = 2;
            gameWindow.getGameManager().eventListAdd(new GamepadButtonEvent(window, JAMEPAD_KEY_Y, action,
                    this.getGamepadDevice()));
        }

        if (this.back != tempJamepadGamepadData.back) {
            if (tempJamepadGamepadData.back) {
                action = 1;
                gameWindow.getGameManager().eventListAdd(new GamepadButtonEvent(window, JAMEPAD_KEY_BACK, action,
                        this.getGamepadDevice()));
            } else {
                action = 0;
                gameWindow.getGameManager().eventListAdd(new GamepadButtonEvent(window, JAMEPAD_KEY_BACK, action,
                        this.getGamepadDevice()));
            }
        } else if (tempJamepadGamepadData.back) {
            action = 2;
            gameWindow.getGameManager().eventListAdd(new GamepadButtonEvent(window, JAMEPAD_KEY_BACK, action,
                    this.getGamepadDevice()));
        }

        if (this.start != tempJamepadGamepadData.start) {
            if (tempJamepadGamepadData.start) {
                action = 1;
                gameWindow.getGameManager().eventListAdd(new GamepadButtonEvent(window, JAMEPAD_KEY_START, action,
                        this.getGamepadDevice()));
            } else {
                action = 0;
                gameWindow.getGameManager().eventListAdd(new GamepadButtonEvent(window, JAMEPAD_KEY_START, action,
                        this.getGamepadDevice()));
            }
        } else if (tempJamepadGamepadData.start) {
            action = 2;
            gameWindow.getGameManager().eventListAdd(new GamepadButtonEvent(window, JAMEPAD_KEY_START, action,
                    this.getGamepadDevice()));
        }

        if (this.lShoulder != tempJamepadGamepadData.lShoulder) {
            if (tempJamepadGamepadData.lShoulder) {
                action = 1;
                gameWindow.getGameManager().eventListAdd(new GamepadButtonEvent(window, JAMEPAD_KEY_LB, action,
                        this.getGamepadDevice()));
            } else {
                action = 0;
                gameWindow.getGameManager().eventListAdd(new GamepadButtonEvent(window, JAMEPAD_KEY_LB, action,
                        this.getGamepadDevice()));
            }
        } else if (tempJamepadGamepadData.lShoulder) {
            action = 2;
            gameWindow.getGameManager().eventListAdd(new GamepadButtonEvent(window, JAMEPAD_KEY_LB, action,
                    this.getGamepadDevice()));
        }

        if (this.rShoulder != tempJamepadGamepadData.rShoulder) {
            if (tempJamepadGamepadData.rShoulder) {
                action = 1;
                gameWindow.getGameManager().eventListAdd(new GamepadButtonEvent(window, JAMEPAD_KEY_RB, action,
                        this.getGamepadDevice()));
            } else {
                action = 0;
                gameWindow.getGameManager().eventListAdd(new GamepadButtonEvent(window, JAMEPAD_KEY_RB, action,
                        this.getGamepadDevice()));
            }
        } else if (tempJamepadGamepadData.rShoulder) {
            action = 2;
            gameWindow.getGameManager().eventListAdd(new GamepadButtonEvent(window, JAMEPAD_KEY_RB, action,
                    this.getGamepadDevice()));
        }

        if (this.lThumb != tempJamepadGamepadData.lThumb) {
            if (tempJamepadGamepadData.lThumb) {
                action = 1;
                gameWindow.getGameManager().eventListAdd(new GamepadButtonEvent(window, JAMEPAD_KEY_L, action,
                        this.getGamepadDevice()));
            } else {
                action = 0;
                gameWindow.getGameManager().eventListAdd(new GamepadButtonEvent(window, JAMEPAD_KEY_L, action,
                        this.getGamepadDevice()));
            }
        } else if (tempJamepadGamepadData.lThumb) {
            action = 2;
            gameWindow.getGameManager().eventListAdd(new GamepadButtonEvent(window, JAMEPAD_KEY_L, action,
                    this.getGamepadDevice()));
        }

        if (this.rThumb != tempJamepadGamepadData.rThumb) {
            if (tempJamepadGamepadData.rThumb) {
                action = 1;
                gameWindow.getGameManager().eventListAdd(new GamepadButtonEvent(window, JAMEPAD_KEY_R, action,
                        this.getGamepadDevice()));
            } else {
                action = 0;
                gameWindow.getGameManager().eventListAdd(new GamepadButtonEvent(window, JAMEPAD_KEY_R, action,
                        this.getGamepadDevice()));
            }
        } else if (tempJamepadGamepadData.rThumb) {
            action = 2;
            gameWindow.getGameManager().eventListAdd(new GamepadButtonEvent(window, JAMEPAD_KEY_R, action,
                    this.getGamepadDevice()));
        }

        if (this.up != tempJamepadGamepadData.up) {
            if (tempJamepadGamepadData.up) {
                action = 1;
                gameWindow.getGameManager().eventListAdd(new GamepadButtonEvent(window, JAMEPAD_KEY_UP, action,
                        this.getGamepadDevice()));
            } else {
                action = 0;
                gameWindow.getGameManager().eventListAdd(new GamepadButtonEvent(window, JAMEPAD_KEY_UP, action,
                        this.getGamepadDevice()));
            }
        } else if (tempJamepadGamepadData.up) {
            action = 2;
            gameWindow.getGameManager().eventListAdd(new GamepadButtonEvent(window, JAMEPAD_KEY_UP, action,
                    this.getGamepadDevice()));
        }

        if (this.down != tempJamepadGamepadData.down) {
            if (tempJamepadGamepadData.down) {
                action = 1;
                gameWindow.getGameManager().eventListAdd(new GamepadButtonEvent(window, JAMEPAD_KEY_DOWN, action,
                        this.getGamepadDevice()));
            } else {
                action = 0;
                gameWindow.getGameManager().eventListAdd(new GamepadButtonEvent(window, JAMEPAD_KEY_DOWN, action,
                        this.getGamepadDevice()));
            }
        } else if (tempJamepadGamepadData.down) {
            action = 2;
            gameWindow.getGameManager().eventListAdd(new GamepadButtonEvent(window, JAMEPAD_KEY_DOWN, action,
                    this.getGamepadDevice()));
        }

        if (this.left != tempJamepadGamepadData.left) {
            if (tempJamepadGamepadData.left) {
                action = 1;
                gameWindow.getGameManager().eventListAdd(new GamepadButtonEvent(window, JAMEPAD_KEY_LEFT, action,
                        this.getGamepadDevice()));
            } else {
                action = 0;
                gameWindow.getGameManager().eventListAdd(new GamepadButtonEvent(window, JAMEPAD_KEY_LEFT, action,
                        this.getGamepadDevice()));
            }
        } else if (tempJamepadGamepadData.left) {
            action = 2;
            gameWindow.getGameManager().eventListAdd(new GamepadButtonEvent(window, JAMEPAD_KEY_LEFT, action,
                    this.getGamepadDevice()));
        }

        if (this.right != tempJamepadGamepadData.right) {
            if (tempJamepadGamepadData.right) {
                action = 1;
                gameWindow.getGameManager().eventListAdd(new GamepadButtonEvent(window, JAMEPAD_KEY_RIGHT, action,
                        this.getGamepadDevice()));
            } else {
                action = 0;
                gameWindow.getGameManager().eventListAdd(new GamepadButtonEvent(window, JAMEPAD_KEY_RIGHT, action,
                        this.getGamepadDevice()));
            }
        } else if (tempJamepadGamepadData.right) {
            action = 2;
            gameWindow.getGameManager().eventListAdd(new GamepadButtonEvent(window, JAMEPAD_KEY_RIGHT, action,
                    this.getGamepadDevice()));
        }

        if (this.guide != tempJamepadGamepadData.guide) {
            if (tempJamepadGamepadData.guide) {
                action = 1;
                gameWindow.getGameManager().eventListAdd(new GamepadButtonEvent(window, JAMEPAD_KEY_GUIDE, action,
                        this.getGamepadDevice()));
            } else {
                action = 0;
                gameWindow.getGameManager().eventListAdd(new GamepadButtonEvent(window, JAMEPAD_KEY_GUIDE, action,
                        this.getGamepadDevice()));
            }
        } else if (tempJamepadGamepadData.guide) {
            action = 2;
            gameWindow.getGameManager().eventListAdd(new GamepadButtonEvent(window, JAMEPAD_KEY_GUIDE, action,
                    this.getGamepadDevice()));
        }

        if (this.unknown != tempJamepadGamepadData.unknown) {
            if (tempJamepadGamepadData.unknown) {
                action = 1;
                gameWindow.getGameManager().eventListAdd(new GamepadButtonEvent(window, JAMEPAD_KEY_UNKNOWN, action,
                        this.getGamepadDevice()));
            } else {
                action = 0;
                gameWindow.getGameManager().eventListAdd(new GamepadButtonEvent(window, JAMEPAD_KEY_UNKNOWN, action,
                        this.getGamepadDevice()));
            }
        } else if (tempJamepadGamepadData.unknown) {
            action = 2;
            gameWindow.getGameManager().eventListAdd(new GamepadButtonEvent(window, JAMEPAD_KEY_UNKNOWN, action,
                    this.getGamepadDevice()));
        }

        boolean ifLtNow = (this.lt > 0.5f);
        boolean ifLtNext = (tempJamepadGamepadData.lt > 0.5f);

        if (ifLtNow != ifLtNext) {
            if (ifLtNext) {
                action = 1;
                gameWindow.getGameManager().eventListAdd(new GamepadButtonEvent(window, JAMEPAD_KEY_LT, action,
                        this.getGamepadDevice()));
            } else {
                action = 0;
                gameWindow.getGameManager().eventListAdd(new GamepadButtonEvent(window, JAMEPAD_KEY_LT, action,
                        this.getGamepadDevice()));
            }
        } else if (ifLtNext) {
            action = 2;
            gameWindow.getGameManager().eventListAdd(new GamepadButtonEvent(window, JAMEPAD_KEY_LT, action,
                    this.getGamepadDevice()));
        }

        boolean ifRtNow = (this.rt > 0.5f);
        boolean ifRtNext = (tempJamepadGamepadData.rt > 0.5f);

        if (ifRtNow != ifRtNext) {
            if (ifRtNext) {
                action = 1;
                gameWindow.getGameManager().eventListAdd(new GamepadButtonEvent(window, JAMEPAD_KEY_RT, action,
                        this.getGamepadDevice()));
            } else {
                action = 0;
                gameWindow.getGameManager().eventListAdd(new GamepadButtonEvent(window, JAMEPAD_KEY_RT, action,
                        this.getGamepadDevice()));
            }
        } else if (ifRtNext) {
            action = 2;
            gameWindow.getGameManager().eventListAdd(new GamepadButtonEvent(window, JAMEPAD_KEY_RT, action,
                    this.getGamepadDevice()));
        }

        this.copy(jamepadControllerState);
    }
}
