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

package com.xenoamess.cyan_potion.demo.world;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.xenoamess.cyan_potion.base.GameWindow;
import com.xenoamess.cyan_potion.base.game_window_components.GameWindowComponentTreeNode;
import com.xenoamess.cyan_potion.base.game_window_components.controllable_game_window_components.Button;
import com.xenoamess.cyan_potion.base.game_window_components.controllable_game_window_components.GlRectfRectangleBox;
import com.xenoamess.cyan_potion.base.game_window_components.controllable_game_window_components.InputBox;
import com.xenoamess.cyan_potion.base.game_window_components.controllable_game_window_components.PictureBox;
import com.xenoamess.cyan_potion.base.io.input.key.Keymap;
import com.xenoamess.cyan_potion.base.io.input.keyboard.KeyboardEvent;
import com.xenoamess.cyan_potion.base.io.input.mouse.MouseScrollEvent;
import com.xenoamess.cyan_potion.base.memory.ResourceInfo;
import com.xenoamess.cyan_potion.base.render.Texture;
import com.xenoamess.cyan_potion.base.steam.SteamManager;
import com.xenoamess.cyan_potion.rpg_module.RpgModuleDataCenter;
import com.xenoamess.cyan_potion.rpg_module.world.World;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.xenoamess.cyan_potion.base.render.Texture.STRING_PICTURE;
import static com.xenoamess.cyan_potion.base.render.Texture.STRING_PURE_COLOR;

/**
 * <p>World class.</p>
 *
 * @author XenoAmess
 * @version 0.162.2-SNAPSHOT
 */
@EqualsAndHashCode(callSuper = true)
@ToString
public final class WorldForDemo extends World {
    @JsonIgnore
    private static final transient Logger LOGGER = LoggerFactory.getLogger(WorldForDemo.class);

    /**
     * 10F
     */
    public static final float MAX_SCALE = 10F;

    /**
     * 0.01F
     */
    public static final float MIN_SCALE = 0.01F;

    @Getter
    @Setter
    private Menu menu;

    @Getter
    @Setter
    private Matrix4f scaleMatrix4f;

    @Getter
    @Setter
    private RpgModuleDataCenter rpgModuleDataCenter;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private final Texture avatarTexture = this.getGameManager().getSteamManager().getPlayerAvatarTextureLarge();

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private final PictureBox pictureBox = new PictureBox(this.getGameWindow(), avatarTexture);

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private final GlRectfRectangleBox glRectfRectangleBox = new GlRectfRectangleBox(
            this.getGameWindow(),
            new Vector4f(0, 1, 1, 1)
    );

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private final InputBox inputBox = new InputBox(this.getGameWindow());

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private final Texture iconTexture = new ResourceInfo<>(
            Texture.class,
            STRING_PICTURE,
            this.getGameManager().getDataCenter().getGameSettings().getIconFilePath()
    ).fetchResource(this.getResourceManager());

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private final Texture pureColorTexture = new ResourceInfo<>(
            Texture.class,
            STRING_PURE_COLOR,
            "",
            "0.5,0.5,0.5,1"
    ).fetchResource(this.getResourceManager());

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private final PictureBox pureColorBox = new PictureBox(this.getGameWindow(), pureColorTexture);

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private final Button demoButton = new Button(this.getGameWindow(), iconTexture, "DEMO");

    {
        demoButton.registerOnMouseEnterAreaCallback(
                mouseButtonEvent -> {
                    switch (demoButton.getButtonText()) {
                        case "DEMO":
                            demoButton.setButtonText("");
                            break;
                        case "":
                            demoButton.getButtonPicture().setBindable(null);
                            demoButton.setButtonText("LOL");
                            break;
                        case "LOL":
                            demoButton.setButtonText("DEMO");
                            demoButton.getButtonPicture().setBindable(iconTexture);
                            break;
                    }
                    return null;
                }
        );
    }

    /**
     * <p>Constructor for World.</p>
     *
     * @param gameWindow gameWindow
     */
    public WorldForDemo(GameWindow gameWindow) {
        super(gameWindow);
        this.setMenu(new Menu(this));
        this.fix();
        inputBox.gainFocus();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void initProcessors() {
        super.initProcessors();
        this.registerProcessor(
                KeyboardEvent.class,
                (KeyboardEvent keyboardEvent) -> {
                    switch (keyboardEvent.getKeyTranslated(this.getGameWindow().getGameManager().getKeymap()).getKey()) {
                        case Keymap.XENOAMESS_KEY_ESCAPE:
                            if (keyboardEvent.getAction() == GLFW.GLFW_PRESS && keyboardEvent.getMods() == 0) {
                                this.getMenu().setShow(true);
                            }
                            return null;
                        default:
                            return keyboardEvent;
                    }
                }
        );
        this.registerProcessor(
                MouseScrollEvent.class,
                (MouseScrollEvent mouseScrollEvent) -> {
                    float newScale = this.getScale();
                    if (mouseScrollEvent.getYoffset() > 0) {
                        newScale += 0.1;
                    } else if (mouseScrollEvent.getYoffset() < 0) {
                        newScale -= 0.1;
                    }

                    if (newScale > MAX_SCALE) {
                        newScale = MAX_SCALE;
                    } else if (newScale < MIN_SCALE) {
                        newScale = MIN_SCALE;
                    }
                    this.changeScale(newScale);
                    return null;
                }
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addToGameWindowComponentTree(GameWindowComponentTreeNode gameWindowComponentTreeNode) {
        super.addToGameWindowComponentTree(gameWindowComponentTreeNode);
        this.getMenu().addToGameWindowComponentTree(this.getGameWindowComponentTreeNode());
        this.pictureBox.addToGameWindowComponentTree(this.getGameWindowComponentTreeNode());
        this.glRectfRectangleBox.addToGameWindowComponentTree(this.getGameWindowComponentTreeNode());
        this.inputBox.addToGameWindowComponentTree(this.getGameWindowComponentTreeNode());
        this.demoButton.addToGameWindowComponentTree(this.getGameWindowComponentTreeNode());
        this.pureColorBox.addToGameWindowComponentTree(this.getGameWindowComponentTreeNode());
    }

    @Override
    public boolean update() {
        boolean res = super.update();
        fix();
        return res;
    }

    /**
     * <p>fix.</p>
     */
    protected void fix() {
        final int avatarPictureSize = 200;
        this.pictureBox.setSize(avatarPictureSize);
        this.pictureBox.moveToRightTopOf(this.getGameWindow());
        this.glRectfRectangleBox.setSize(avatarPictureSize);
        this.glRectfRectangleBox.moveToLeftBottomOf(this.getGameWindow());
        this.inputBox.setSize(avatarPictureSize);
        this.inputBox.moveToRightBottomOf(this.getGameWindow());
        this.demoButton.setSize(avatarPictureSize);
        this.demoButton.moveToLeftTopOf(this.getGameWindow());
        this.pureColorBox.setSize(avatarPictureSize);
        this.pureColorBox.setRotateRadius((float) Math.toRadians(45));
        this.pureColorBox.moveToCenterBottomOf(this.getGameWindow());
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public boolean draw() {
        super.draw();
        SteamManager steamManager = this.getGameManager().getSteamManager();
        String personName;
        if (steamManager.isRunWithSteam()) {
            personName = steamManager.getSteamFriends().getPersonaName();
        } else {
            personName = "RunSteamFirst";
        }
        this.getGameWindow().drawTextCenter(
                null,
                pictureBox.getCenterBottomPosX(),
                pictureBox.getCenterBottomPosY() + 12.5F,
                25,
                personName
        );
        return true;
    }
}
