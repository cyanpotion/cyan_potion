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

package com.xenoamess.cyan_potion.base.game_window_components;

import com.xenoamess.cyan_potion.base.GameManagerConfig;
import com.xenoamess.cyan_potion.base.GameWindow;
import com.xenoamess.cyan_potion.base.game_window_components.controllable_game_window_components.AbstractControllableGameWindowComponent;
import com.xenoamess.cyan_potion.base.game_window_components.controllable_game_window_components.InputBox;
import com.xenoamess.cyan_potion.base.game_window_components.controllable_game_window_components.Panel;
import com.xenoamess.cyan_potion.base.io.input.key.Keymap;
import com.xenoamess.cyan_potion.base.io.input.keyboard.KeyboardEvent;
import com.xenoamess.cyan_potion.base.io.input.mouse.MouseButtonEvent;
import com.xenoamess.cyan_potion.base.io.input.mouse.MouseScrollEvent;
import com.xenoamess.cyan_potion.base.memory.ResourceInfo;
import com.xenoamess.cyan_potion.base.render.Texture;
import com.xenoamess.cyan_potion.base.visual.Font;
import com.xenoamess.cyan_potion.base.visual.Picture;
import org.joml.Vector4f;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;

import static org.lwjgl.opengl.GL11.*;

/**
 * <p>TitleExample class.</p>
 *
 * @author XenoAmess
 * @version 0.143.0
 */
public class TitleExample extends AbstractGameWindowComponent {
    private final Texture saveSlotTexture =
            this.getGameWindow().getGameManager().getResourceManager().
                    fetchResource(
                            Texture.class,
                            "picture",
                            "resources/www/img/pictures/saveSlot.png"
                    );

    private final Picture saveSlotPicture = new Picture(saveSlotTexture);

    {
        this.saveSlotPicture.setCenter(this.getGameWindow());
        this.saveSlotPicture.setSize(250, 50);
    }

    private final Texture saveStarTexture =
            this.getGameWindow().getGameManager().getResourceManager().fetchResource(
                    Texture.class,
                    new ResourceInfo(
                            Texture.class,
                            "picture",
                            "resources/www/img/pictures/saveStar.png"
                    )
            );
    private final ArrayList<AbstractControllableGameWindowComponent> controllableGameWindowComponents =
            new ArrayList<>();

    /**
     * <p>Constructor for TitleExample.</p>
     *
     * @param gameWindow gameWindow
     */
    public TitleExample(GameWindow gameWindow) {
        super(gameWindow);
        getControllableGameWindowComponents().add(new AbstractControllableGameWindowComponent(this.getGameWindow()) {
            {
                this.registerOnMouseEnterAreaCallback(
                        event -> {
                            if (getState() >= 0 && getState() <= 4) {
                                setState(index);
                            }
                            return null;
                        }
                );

                this.registerOnMouseLeaveAreaCallback(
                        event -> {
                            if (getState() >= 0 && getState() <= 4) {
                                setState(0);
                            }
                            return null;
                        }
                );
            }


            static final int index = 1;

            @Override
            public void draw() {
                this.init(-50 + 250 * index, 900, 120, 30);
                if (getState() >= 0 && getState() <= 4) {
                    this.getGameWindow().drawTextFillAreaLeftTop(Font.getCurrentFont(), this.getLeftTopPosX(),
                            this.getLeftTopPosY(), this.getWidth(),
                            this.getHeight(), 0,
                            new Vector4f(1, 1, 1, getState() == index ? 1f :
                                    0.3f), "开始游戏");
                }
            }

        });

        getControllableGameWindowComponents().

                add(new AbstractControllableGameWindowComponent(this.getGameWindow()) {
                    {
                        this.registerOnMouseEnterAreaCallback(
                                event -> {
                                    if (getState() >= 0 && getState() <= 4) {
                                        setState(index);
                                    }
                                    return null;
                                }
                        );

                        this.registerOnMouseLeaveAreaCallback(
                                event -> {
                                    if (getState() >= 0 && getState() <= 4) {
                                        setState(0);
                                    }
                                    return null;
                                }
                        );
                    }

                    static final int index = 2;

                    @Override
                    public void draw() {
                        this.init(-50 + 250 * index, 900, 120, 30);
                        if (getState() >= 0 && getState() <= 4) {
                            this.getGameWindow().drawTextFillAreaLeftTop(Font.getCurrentFont(), this.getLeftTopPosX(),
                                    this.getLeftTopPosY(), this.getWidth(),
                                    this.getHeight(), 0,
                                    new Vector4f(1, 1, 1, getState() == index ? 1f :
                                            0.3f), "设置选项");
                        }
                    }
                });

        getControllableGameWindowComponents().

                add(new AbstractControllableGameWindowComponent(this.getGameWindow()) {
                    {
                        this.registerOnMouseEnterAreaCallback(
                                event -> {
                                    if (getState() >= 0 && getState() <= 4) {
                                        setState(index);
                                    }
                                    return null;
                                }
                        );

                        this.registerOnMouseLeaveAreaCallback(
                                event -> {
                                    if (getState() >= 0 && getState() <= 4) {
                                        setState(0);
                                    }
                                    return null;
                                }
                        );
                    }

                    static final int index = 3;

                    @Override
                    public void draw() {
                        this.init(-50 + 250 * index, 900, 120, 30);
                        if (getState() >= 0 && getState() <= 4) {
                            this.getGameWindow().drawTextFillAreaLeftTop(Font.getCurrentFont(), this.getLeftTopPosX(),
                                    this.getLeftTopPosY(), this.getWidth(),
                                    this.getHeight(), 0,
                                    new Vector4f(1, 1, 1, getState() == index ? 1f :
                                            0.3f), "制作人员");
                        }
                    }
                });

        getControllableGameWindowComponents().

                add(new AbstractControllableGameWindowComponent(this.getGameWindow()) {
                    {
                        this.registerOnMouseEnterAreaCallback(
                                event -> {
                                    if (getState() >= 0 && getState() <= 4) {
                                        setState(index);
                                    }
                                    return null;
                                }
                        );

                        this.registerOnMouseLeaveAreaCallback(
                                event -> {
                                    if (getState() >= 0 && getState() <= 4) {
                                        setState(0);
                                    }
                                    return null;
                                }
                        );
                    }

                    static final int index = 4;

                    @Override
                    public void draw() {
                        this.init(-50 + 250 * index, 900, 120, 30);
                        if (getState() >= 0 && getState() <= 4) {
                            this.getGameWindow().drawTextFillAreaLeftTop(Font.getCurrentFont(), this.getLeftTopPosX(),
                                    this.getLeftTopPosY(), this.getWidth(),
                                    this.getHeight(), 0,
                                    new Vector4f(1, 1, 1, getState() == index ? 1f :
                                            0.3f), "退出游戏");
                        }
                    }
                });

        Texture texture =
                this.getGameWindow().getGameManager().getResourceManager().fetchResource(
                        Texture.class,
                        "picture",
                        "resources/www/img/pictures/saveSlot.png"
                );
        Panel panel = new Panel(gameWindow, texture);
        panel.init(100, 100, 600, 600);
        InputBox inputBox = new InputBox(gameWindow);
        inputBox.init(150, 150, 500, 500);
        panel.addContent(inputBox);

        getControllableGameWindowComponents().

                add(panel);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void initProcessors() {
        this.registerProcessor(KeyboardEvent.class.getCanonicalName(), event -> {
            KeyboardEvent keyboardEvent = (KeyboardEvent) event;
            if (keyboardEvent.getAction() != GLFW.GLFW_PRESS) {
                return event;
            }
            switch (keyboardEvent.getKeyTranslated(this.getGameWindow().getGameManager().getKeymap()).getKey()) {
                case Keymap.XENOAMESS_KEY_ESCAPE:
                    if (keyboardEvent.getAction() == GLFW.GLFW_PRESS) {
                        if (getState() >= 0 && getState() <= 4) {
                            setState(4);
                        } else if (getState() == -101) {
                            setState(1);
                        } else if (getState() == -102) {
                            setState(2);
                        } else if (getState() == -103) {
                            setState(3);
                        } else if (getState() == -104) {
                            setState(4);
                        }
                    }
                    break;
                case Keymap.XENOAMESS_KEY_ENTER:
                    if (getState() >= 0 && getState() <= 4) {
                        this.setState(-this.getState());
                    } else if (getState() == -101) {
                        setState(-102);
                    }
                    break;
                case Keymap.XENOAMESS_KEY_UP:
                case Keymap.XENOAMESS_KEY_LEFT:
                    if (keyboardEvent.getAction() == GLFW.GLFW_PRESS) {
                        lastState();
                    }
                    break;
                case Keymap.XENOAMESS_KEY_DOWN:
                case Keymap.XENOAMESS_KEY_RIGHT:
                    if (keyboardEvent.getAction() == GLFW.GLFW_PRESS) {
                        nextState();
                    }
                    break;
                default:
                    return event;
            }
            return null;
        });

        this.registerProcessor(MouseButtonEvent.class.getCanonicalName(),
                event -> {
                    MouseButtonEvent mouseButtonEvent =
                            (MouseButtonEvent) event;
                    if (mouseButtonEvent.getAction() != GLFW.GLFW_PRESS) {
                        return event;
                    }
                    switch (mouseButtonEvent.getKeyTranslated(this.getGameWindow().getGameManager().getKeymap()).getKey()) {
                        case Keymap.XENOAMESS_MOUSE_BUTTON_LEFT:
                            if (mouseButtonEvent.getAction() == GLFW.GLFW_PRESS) {
                                if (getState() >= 0 && getState() <= 4) {
                                    this.setState(-this.getState());
                                } else if (getState() == -101) {
                                    setState(-102);
                                }
                            }
                            break;
                        case Keymap.XENOAMESS_MOUSE_BUTTON_RIGHT:
                            if (mouseButtonEvent.getAction() == GLFW.GLFW_PRESS) {
                                if (getState() >= 0 && getState() <= 4) {
                                    this.setState(4);
                                } else if (getState() == -101) {
                                    setState(1);
                                }
                            }
                            break;
                        default:
                            return event;
                    }
                    return null;
                });

        this.registerProcessor(MouseScrollEvent.class.getCanonicalName(),
                event -> {
                    MouseScrollEvent mouseScrollEvent =
                            (MouseScrollEvent) event;
                    if (mouseScrollEvent.getYoffset() < 0) {
                        nextState();
                    } else {
                        lastState();
                    }
                    this.setAlive(false);
                    return null;
                });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addToGameWindowComponentTree(
            GameWindowComponentTreeNode gameWindowComponentTreeNode
    ) {
        super.addToGameWindowComponentTree(gameWindowComponentTreeNode);
        getControllableGameWindowComponents().forEach(
                (AbstractControllableGameWindowComponent controllableGameWindowComponent)
                        -> controllableGameWindowComponent.addToGameWindowComponentTree(
                        TitleExample.this.getGameWindowComponentTreeNode()
                )
        );
    }

    private int state = 0;
    private int time = 0;

    void nextState() {
        if (getState() < 0) {
            return;
        }
        if (getState() > 10) {
            return;
        }
        if (getState() == 4) {
            setState(1);
            return;
        }
        setState(getState() + 1);
    }

    void lastState() {
        if (getState() < 0) {
            return;
        }
        if (getState() > 10) {
            return;
        }
        if (getState() <= 1) {
            setState(4);
            return;
        }
        setState(getState() - 1);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void update() {
        switch (getState()) {
            case -1:
                setState(-101);
                break;
            case -2:
            case -3:
                setState(-201);
                break;
            case -4:
                this.getGameWindow().getGameManager().shutdown();
                break;
            case -102:
                startGame();
                break;
            default:
                break;
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void draw() {

        glClearColor(0, 0, 0, 0);
        glClear(GL_COLOR_BUFFER_BIT);

        if (!(this.getState() >= 0 && this.getState() <= 4) && getState() == -101) {
            this.saveSlotPicture.draw(this.getGameWindow());
            this.getGameWindow().drawTextFillArea(Font.getCurrentFont(),
                    this.getGameWindow().getLogicWindowWidth() / 2F,
                    this.getGameWindow().getLogicWindowHeight() / 2F, 250, 50,
                    0, new Vector4f(1, 1, 1, 1F), "校准文本BeEf");
        }
    }

    private void startGame() {
        this.getGameWindowComponentTreeNode().close();
        AbstractGameWindowComponent world =
                AbstractGameWindowComponent.createGameWindowComponentFromClassName(this.getGameWindow(),
                        GameManagerConfig.getString(this.getGameWindow().getGameManager().getDataCenter().getCommonSettings(),
                                "worldClassName",
                                "com.xenoamess.cyan_potion.rpg_module.world.World"));
        world.addToGameWindowComponentTree(null);
        world.enlargeAsFullWindow();
    }

    /**
     * <p>Getter for the field <code>saveSlotTexture</code>.</p>
     *
     * @return return
     */
    public Texture getSaveSlotTexture() {
        return saveSlotTexture;
    }

    /**
     * <p>Getter for the field <code>saveStarTexture</code>.</p>
     *
     * @return return
     */
    public Texture getSaveStarTexture() {
        return saveStarTexture;
    }

    /**
     * <p>Getter for the field <code>controllableGameWindowComponents</code>.</p>
     *
     * @return return
     */
    public ArrayList<AbstractControllableGameWindowComponent> getControllableGameWindowComponents() {
        return controllableGameWindowComponents;
    }

    /**
     * <p>Getter for the field <code>state</code>.</p>
     *
     * @return a int.
     */
    public int getState() {
        return state;
    }

    /**
     * <p>Setter for the field <code>state</code>.</p>
     *
     * @param state a int.
     */
    public void setState(int state) {
        this.state = state;
    }

    /**
     * <p>Getter for the field <code>time</code>.</p>
     *
     * @return a int.
     */
    public int getTime() {
        return time;
    }

    /**
     * <p>Setter for the field <code>time</code>.</p>
     *
     * @param time a int.
     */
    public void setTime(int time) {
        this.time = time;
    }
}
