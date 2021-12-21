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

package com.xenoamess.cyan_potion.base.visual;

import com.xenoamess.cyan_potion.base.GameManager;
import com.xenoamess.cyan_potion.base.GameWindow;
import com.xenoamess.cyan_potion.base.game_window_components.AbstractScene;
import com.xenoamess.cyan_potion.base.math.FrameFloat;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.joml.Math;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>Animation class.</p>
 *
 * @author XenoAmess
 * @version 0.162.3
 */
@EqualsAndHashCode(callSuper = true)
@ToString
public class Animation extends AbstractPicture {

    @Getter
    @Setter
    private float texturePointer;

    @Getter
    @Setter
    private float fps;

    @Getter
    @Setter
    private FrameFloat fpsFrameFloat;

    @Getter(AccessLevel.PROTECTED)
    private final List<AbstractPictureInterface> frames = new ArrayList<>();


    /**
     * <p>Constructor for Animation.</p>
     *
     * @param fps a int.
     */
    public Animation(int fps) {
        this.setTexturePointer(0);
        this.setFps(fps);
    }


    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unused")
    @Override
    public void draw(GameWindow gameWindow) {
        AbstractPictureInterface pictureInterface = this.getCurrentPicture(gameWindow.getGameManager());
        pictureInterface.cover(this);
        pictureInterface.draw(gameWindow);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void draw(AbstractScene scene) {
        AbstractPictureInterface pictureInterface = this.getCurrentPicture(scene.getGameManager());
        pictureInterface.cover(this);
        pictureInterface.draw(scene);
    }

    /**
     * <p>getCurrentPicture.</p>
     *
     * @param gameManager
     * @return return
     */
    public AbstractPictureInterface getCurrentPicture(GameManager gameManager) {
        if (this.getFpsFrameFloat() == null || this.getFpsFrameFloat().getGameManager() != gameManager) {
            this.setFpsFrameFloat(new FrameFloat(gameManager, this.getFps()));
        }
        if (this.getFpsFrameFloat().getValueFor1Second() != this.getFps()) {
            this.getFpsFrameFloat().setValueFor1Second(this.getFps());
        }
        float texturePointer = getTexturePointer();
        float textureAddNum = this.getFpsFrameFloat().getValue();
        texturePointer += textureAddNum;
        texturePointer %= this.getFrameNum();
        setTexturePointer(texturePointer);
        final int index = getIndex();
        return this.getFrame(index);
    }

    public int getFrameNum() {
        return this.getFrames().size();
    }

    public AbstractPictureInterface getFrame(int index) {
        return this.getFrames().get(index);
    }

    public int getIndex() {
        int tmpIndex = (int) Math.floor(getTexturePointer());
        while (tmpIndex >= this.getFrameNum()) {
            tmpIndex -= this.getFrameNum();
        }
        return tmpIndex;
    }
}
