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

package com.xenoamess.cyan_potion.rpg_module.render;

import com.xenoamess.cyan_potion.base.exceptions.URITypeNotDefinedException;
import com.xenoamess.cyan_potion.base.memory.ResourceInfo;
import com.xenoamess.cyan_potion.base.memory.ResourceManager;
import com.xenoamess.cyan_potion.base.render.Texture;
import com.xenoamess.cyan_potion.base.visual.AbstractPictureInterface;
import com.xenoamess.cyan_potion.base.visual.Animation;
import com.xenoamess.cyan_potion.base.visual.Picture;
import com.xenoamess.cyan_potion.rpg_module.units.Unit;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.xenoamess.cyan_potion.rpg_module.render.TextureUtils.STRING_CHARACTER;

/**
 * <p>WalkingAnimation4Dirs class.</p>
 *
 * @author XenoAmess
 * @version 0.160.0-SNAPSHOT
 */
public class WalkingAnimation4Dirs extends Animation {
    private Map<Integer, List<AbstractPictureInterface>> faceDirFrameMap = new ConcurrentHashMap<>();
    private Unit unit;
    private int drawFaceDir;

    /**
     * <p>setFaceDir.</p>
     *
     * @param faceDir a int.
     */
    public void setFaceDir(int faceDir) {
        if (this.getFaceDirFrameMap().containsKey(faceDir)) {
            this.setDrawFaceDir(faceDir);
        }
    }

    /**
     * <p>Constructor for WalkingAnimation4Dirs.</p>
     *
     * @param fps             a int.
     * @param unit            a {@link com.xenoamess.cyan_potion.rpg_module.units.Unit} object.
     * @param resourceInfo    resourceInfo.
     * @param resourceManager resourceManager
     */
    public WalkingAnimation4Dirs(int fps, Unit unit, ResourceInfo resourceInfo,
                                 ResourceManager resourceManager) {
        super(fps);
        this.setUnit(unit);
        String resourceFilePath = resourceInfo.getFileString();
        switch (resourceInfo.getType()) {
            case STRING_CHARACTER:
                int peopleIndex = Integer.parseInt(resourceInfo.getValues()[0]);
                List<Texture> walkingTextures =
                        TextureUtils.getWalkingTextures(resourceManager,
                                resourceFilePath).get(peopleIndex);
                this.initPictures(this.buildPictures(walkingTextures));
                break;
            default:
                throw new URITypeNotDefinedException(resourceInfo);
        }
    }

    private List<AbstractPictureInterface> buildPictures(List<Texture> textures) {
        List<AbstractPictureInterface> res = new ArrayList<>(textures.size());
        for (Texture texture : textures) {
            Picture picture = new Picture(texture);
            picture.cover(this.getUnit());
            res.add(picture);
        }
        return res;
    }

    private void initPictures(List<AbstractPictureInterface> walkingPictures) {
        int ti = 0;
        getFaceDirFrameMap().put(180, new ArrayList<>());
        for (int i = 0; i < 3; i++, ti++) {
            getFaceDirFrameMap().get(180).add(walkingPictures.get(ti));
        }
        getFaceDirFrameMap().put(270, new ArrayList<>());
        for (int i = 0; i < 3; i++, ti++) {
            getFaceDirFrameMap().get(270).add(walkingPictures.get(ti));
        }
        getFaceDirFrameMap().put(90, new ArrayList<>());
        for (int i = 0; i < 3; i++, ti++) {
            getFaceDirFrameMap().get(90).add(walkingPictures.get(ti));
        }
        getFaceDirFrameMap().put(0, new ArrayList<>());
        for (int i = 0; i < 3; i++, ti++) {
            getFaceDirFrameMap().get(0).add(walkingPictures.get(ti));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AbstractPictureInterface getCurrentPicture() {
        long currentTime = System.currentTimeMillis();
        float elapsedTime = currentTime - getLastTime();

        int texturePointer = getTexturePointer();
        int textureAddNum = (int) Math.floor(elapsedTime / 1000.0 * getFps());
        texturePointer += textureAddNum;
        texturePointer %= 4;
        setTexturePointer(texturePointer);
        this.setLastTime(getLastTime() + (long) (textureAddNum * 1000.0 / getFps()));

        if (!getUnit().isMoving()) {
            setTexturePointer(1);
        }

        int tmpTexturePointer = getTexturePointer();

        if (getTexturePointer() == 3) {
            tmpTexturePointer -= 2;
        }

        List<AbstractPictureInterface> list = getFaceDirFrameMap().get(getUnit().getFaceDir());
        if (list == null || list.isEmpty()) {
            list = getFaceDirFrameMap().get(this.getDrawFaceDir());
        }
        return list.get(tmpTexturePointer);
    }


    /**
     * <p>Getter for the field <code>faceDirFrameMap</code>.</p>
     *
     * @return return
     */
    public Map<Integer, List<AbstractPictureInterface>> getFaceDirFrameMap() {
        return faceDirFrameMap;
    }

    /**
     * <p>Setter for the field <code>faceDirFrameMap</code>.</p>
     *
     * @param faceDirFrameMap faceDirFrameMap
     */
    public void setFaceDirFrameMap(Map<Integer, List<AbstractPictureInterface>> faceDirFrameMap) {
        this.faceDirFrameMap = faceDirFrameMap;
    }

    /**
     * <p>Getter for the field <code>unit</code>.</p>
     *
     * @return return
     */
    public Unit getUnit() {
        return unit;
    }

    /**
     * <p>Setter for the field <code>unit</code>.</p>
     *
     * @param unit unit
     */
    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    /**
     * <p>Getter for the field <code>drawFaceDir</code>.</p>
     *
     * @return a int.
     */
    public int getDrawFaceDir() {
        return drawFaceDir;
    }

    /**
     * <p>Setter for the field <code>drawFaceDir</code>.</p>
     *
     * @param drawFaceDir a int.
     */
    public void setDrawFaceDir(int drawFaceDir) {
        this.drawFaceDir = drawFaceDir;
    }
}
