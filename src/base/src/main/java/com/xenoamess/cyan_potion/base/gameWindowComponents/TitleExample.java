package com.xenoamess.cyan_potion.base.gameWindowComponents;

import com.xenoamess.cyan_potion.base.GameWindow;
import com.xenoamess.cyan_potion.base.events.Event;
import com.xenoamess.cyan_potion.base.events.KeyEvent;
import com.xenoamess.cyan_potion.base.events.MouseButtonEvent;
import com.xenoamess.cyan_potion.base.events.MouseScrollEvent;
import com.xenoamess.cyan_potion.base.gameWindowComponents.ControlableGameWindowComponents.AbstractControlableGameWindowComponent;
import com.xenoamess.cyan_potion.base.gameWindowComponents.ControlableGameWindowComponents.InputBox;
import com.xenoamess.cyan_potion.base.gameWindowComponents.ControlableGameWindowComponents.Panel;
import com.xenoamess.cyan_potion.base.io.input.key.Keymap;
import com.xenoamess.cyan_potion.base.render.Texture;
import com.xenoamess.cyan_potion.base.visual.Font;
import org.joml.Vector4f;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;

import static org.lwjgl.opengl.GL11.*;

/**
 * @author XenoAmess
 */
public class TitleExample extends AbstractGameWindowComponent {
    private Texture saveSlotTexture = this.getGameWindow().getGameManager().getResourceManager().fetchResourceWithShortenURI(Texture.class, "/www/img/pictures/saveSlot.png:picture");
    private Texture saveStarTexture = this.getGameWindow().getGameManager().getResourceManager().fetchResourceWithShortenURI(Texture.class, "/www/img/pictures/saveStar.png:picture");
    private ArrayList<AbstractControlableGameWindowComponent> controlableGameWindowComponents = new ArrayList<AbstractControlableGameWindowComponent>();

    public TitleExample(GameWindow gameWindow) {
        super(gameWindow);
        getControlableGameWindowComponents().add(new AbstractControlableGameWindowComponent(this.getGameWindow()) {
            int index = 1;

            @Override
            public void draw() {
                this.init(-50 + 250 * index, 900, 120, 30);
                if (getState() >= 0 && getState() <= 4) {
                    this.getGameWindow().drawTextFillAreaLeftTop(Font.getCurrentFont(), this.getLeftTopPosX(),
                            this.getLeftTopPosY(), this.getWidth(), this.getHeight(), 0,
                            new Vector4f(1, 1, 1, getState() == index ? 1f : 0.3f), "开始游戏");
                }
            }

            @Override
            public Event onMouseEnterArea() {
                if (getState() >= 0 && getState() <= 4) {
                    setState(index);
                }
                return null;
            }

            @Override
            public Event onMouseLeaveArea() {
                if (getState() >= 0 && getState() <= 4) {
                    setState(0);
                }
                return null;
            }
        });
        getControlableGameWindowComponents().add(new AbstractControlableGameWindowComponent(this.getGameWindow()) {
            int index = 2;

            @Override
            public void draw() {
                this.init(-50 + 250 * index, 900, 120, 30);
                if (getState() >= 0 && getState() <= 4) {
                    this.getGameWindow().drawTextFillAreaLeftTop(Font.getCurrentFont(), this.getLeftTopPosX(),
                            this.getLeftTopPosY(), this.getWidth(), this.getHeight(), 0,
                            new Vector4f(1, 1, 1, getState() == index ? 1f : 0.3f), "设置选项");
                }
            }

            @Override
            public Event onMouseEnterArea() {
                if (getState() >= 0 && getState() <= 4) {
                    setState(index);
                }
                return null;
            }

            @Override
            public Event onMouseLeaveArea() {
                if (getState() >= 0 && getState() <= 4) {
                    setState(0);
                }
                return null;
            }
        });
        getControlableGameWindowComponents().add(new AbstractControlableGameWindowComponent(this.getGameWindow()) {
            int index = 3;

            @Override
            public void draw() {
                this.init(-50 + 250 * index, 900, 120, 30);
                if (getState() >= 0 && getState() <= 4) {
                    this.getGameWindow().drawTextFillAreaLeftTop(Font.getCurrentFont(), this.getLeftTopPosX(),
                            this.getLeftTopPosY(), this.getWidth(), this.getHeight(), 0,
                            new Vector4f(1, 1, 1, getState() == index ? 1f : 0.3f), "制作人员");
                }
            }

            @Override
            public Event onMouseEnterArea() {
                if (getState() >= 0 && getState() <= 4) {
                    setState(index);
                }
                return null;
            }

            @Override
            public Event onMouseLeaveArea() {
                if (getState() >= 0 && getState() <= 4) {
                    setState(0);
                }
                return null;
            }
        });
        getControlableGameWindowComponents().add(new AbstractControlableGameWindowComponent(this.getGameWindow()) {
            int index = 4;

            @Override
            public void draw() {
                this.init(-50 + 250 * index, 900, 120, 30);
                if (getState() >= 0 && getState() <= 4) {
                    this.getGameWindow().drawTextFillAreaLeftTop(Font.getCurrentFont(), this.getLeftTopPosX(),
                            this.getLeftTopPosY(), this.getWidth(), this.getHeight(), 0,
                            new Vector4f(1, 1, 1, getState() == index ? 1f : 0.3f), "退出游戏");
                }
            }

            @Override
            public Event onMouseEnterArea() {
                if (getState() >= 0 && getState() <= 4) {
                    setState(index);
                }
                return null;
            }

            @Override
            public Event onMouseLeaveArea() {
                if (getState() >= 0 && getState() <= 4) {
                    setState(0);
                }
                return null;
            }
        });

        Texture texture = this.getGameWindow().getGameManager().getResourceManager().fetchResourceWithShortenURI(Texture.class, "/www/img/pictures/saveSlot.png:picture");
        Panel panel = new Panel(gameWindow, texture);
        panel.init(100, 100, 600, 600);
        InputBox inputBox = new InputBox(gameWindow);
        inputBox.init(150, 150, 500, 500);
        panel.addContent(inputBox);

        getControlableGameWindowComponents().add(panel);
    }

    @Override
    public void initProcessors() {
        this.registerProcessor(KeyEvent.class.getCanonicalName(), event -> {
            KeyEvent keyEvent = (KeyEvent) event;
            if (keyEvent.getAction() != GLFW.GLFW_PRESS) {
                return event;
            }
            switch (keyEvent.getKeyTranslated().getKey()) {
                case Keymap.XENOAMESS_KEY_ESCAPE:
                    if (keyEvent.getAction() == GLFW.GLFW_PRESS) {
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
                    if (keyEvent.getAction() == GLFW.GLFW_PRESS) {
                        lastState();
                    }
                    break;
                case Keymap.XENOAMESS_KEY_DOWN:
                    if (keyEvent.getAction() == GLFW.GLFW_PRESS) {
                        nextState();
                    }
                    break;
                case Keymap.XENOAMESS_KEY_LEFT:
                    if (keyEvent.getAction() == GLFW.GLFW_PRESS) {
                        lastState();
                    }
                    break;
                case Keymap.XENOAMESS_KEY_RIGHT:
                    if (keyEvent.getAction() == GLFW.GLFW_PRESS) {
                        nextState();
                    }
                    break;
                default:
                    return event;
            }
            return null;
        });

        this.registerProcessor(MouseButtonEvent.class.getCanonicalName(), event -> {
            MouseButtonEvent mouseButtonEvent = (MouseButtonEvent) event;
            if (mouseButtonEvent.getAction() != GLFW.GLFW_PRESS) {
                return event;
            }
            switch (mouseButtonEvent.getKeyTranslated().getKey()) {
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

        this.registerProcessor(MouseScrollEvent.class.getCanonicalName(), event -> {
            MouseScrollEvent mouseScrollEvent = (MouseScrollEvent) event;
            if (mouseScrollEvent.getYoffset() < 0) {
                nextState();
            } else {
                lastState();
            }
            this.getAlive().set(false);
            return null;
        });
    }

    @Override
    public void addToGameWindowComponentTree(GameWindowComponentTreeNode gameWindowComponentTreeNode) {
        super.addToGameWindowComponentTree(gameWindowComponentTreeNode);
        for (AbstractControlableGameWindowComponent au : getControlableGameWindowComponents()) {
            au.addToGameWindowComponentTree(this.getGameWindowComponentTreeNode());
        }
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
        return;
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
        return;
    }

    @Override
    public void update() {
        switch (getState()) {
            case -1:
                setState(-101);
                break;
            case -2:
                setState(-201);
                break;
            case -3:
                setState(-201);
                break;
            case -4:
                this.getGameWindow().getGameManager().shutdown();
                break;
            case -102:
                startGame();
            default:
                break;
        }

    }

    @Override
    public void draw() {
//        System.out.println(state);

        glClearColor(0, 0, 0, 0);
        glClear(GL_COLOR_BUFFER_BIT);

        if (this.getState() >= 0 && this.getState() <= 4) {
//            int nowIndex = 1;
//            this.getGameWindow().drawText(Font.defaultFont, -50 + 250 * nowIndex, 900, 1, new Vector4f(1, 1, 1, state == nowIndex ? 1f : 0.3f), "开始游戏");
//            nowIndex++;
//            this.getGameWindow().drawText(Font.defaultFont, -50 + 250 * nowIndex, 900, 1, new Vector4f(1, 1, 1, state == nowIndex ? 1f : 0.3f), "设置选项");
//            nowIndex++;
//            this.getGameWindow().drawText(Font.defaultFont, -50 + 250 * nowIndex, 900, 1, new Vector4f(1, 1, 1, state == nowIndex ? 1f : 0.3f), "制作人员");
//            nowIndex++;
//            this.getGameWindow().drawText(Font.defaultFont, -50 + 250 * nowIndex, 900, 1, new Vector4f(1, 1, 1, state == nowIndex ? 1f : 0.3f), "退出游戲");
        } else if (getState() == -101) {
            this.getGameWindow().drawBindableRelative(this.getSaveSlotTexture(), this.getGameWindow().getLogicWindowWidth() / 2, this.getGameWindow().getLogicWindowHeight() / 2, 250, 50);
            this.getGameWindow().drawTextFillArea(Font.getCurrentFont(), this.getGameWindow().getLogicWindowWidth() / 2, this.getGameWindow().getLogicWindowHeight() / 2, 250, 50, 0, new Vector4f(1, 1, 1, 1f), "校准文本Ugna");

//            for (int i = 0; i < 20; i++) {
//                this.getGameWindow().drawBindableRelativeLeftTop(this.saveSlotTexture, i * 50, i * 50, 250, 50);
//                this.getGameWindow().drawText(Font.defaultFont, i * 50, i * 50, 1, new Vector4f(1, 1, 1, 1f), "校准文本");
//            }

//            this.getGameWindow().drawBindableRelativeLeftTop(this.saveSlotTexture, 0, 0, 1280, 1024);


        }

        //        this.
    }

    private void startGame() {
        this.getGameWindowComponentTreeNode().close();
        AbstractGameWindowComponent world = this.getGameWindow().getGameManager().getDataCenter().fetchGameWindowComponentFromCommonSetting(this.getGameWindow(), "worldClassName", "com.xenoamess.cyan_potion.rpg_module.world.World");
        world.addToGameWindowComponentTree(null);
        world.enlargeAsFullWindow();
    }

    @Override
    public void close() {

    }

    public Texture getSaveSlotTexture() {
        return saveSlotTexture;
    }

    public void setSaveSlotTexture(Texture saveSlotTexture) {
        this.saveSlotTexture = saveSlotTexture;
    }

    public Texture getSaveStarTexture() {
        return saveStarTexture;
    }

    public void setSaveStarTexture(Texture saveStarTexture) {
        this.saveStarTexture = saveStarTexture;
    }

    public ArrayList<AbstractControlableGameWindowComponent> getControlableGameWindowComponents() {
        return controlableGameWindowComponents;
    }

    public void setControlableGameWindowComponents(ArrayList<AbstractControlableGameWindowComponent> controlableGameWindowComponents) {
        this.controlableGameWindowComponents = controlableGameWindowComponents;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }
}
