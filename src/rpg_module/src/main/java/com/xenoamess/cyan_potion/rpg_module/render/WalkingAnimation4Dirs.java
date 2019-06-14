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

package com.xenoamess.cyan_potion.rpg_module.render;

import com.xenoamess.cyan_potion.base.exceptions.URITypeNotDefinedException;
import com.xenoamess.cyan_potion.base.memory.ResourceManager;
import com.xenoamess.cyan_potion.base.render.Animation;
import com.xenoamess.cyan_potion.base.render.Bindable;
import com.xenoamess.cyan_potion.base.render.Texture;
import com.xenoamess.cyan_potion.rpg_module.units.Unit;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>WalkingAnimation4Dirs class.</p>
 *
 * @author XenoAmess
 * @version 0.143.0
 */
public class WalkingAnimation4Dirs extends Animation {
    private Map<Integer, List<Texture>> faceDirFrameMap = new ConcurrentHashMap<>();
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
     * @param resourceURI     a {@link java.lang.String} object.
     * @param resourceManager a {@link com.xenoamess.cyan_potion.base.memory.ResourceManager} object.
     */
    public WalkingAnimation4Dirs(int fps, Unit unit, String resourceURI,
                                 ResourceManager resourceManager) {
        super(fps);
        this.setUnit(unit);

        String[] resourceFileURIStrings = resourceURI.split(":");
        String resourceFilePath = resourceFileURIStrings[0];
        String resourceType = resourceFileURIStrings[1];
        switch (resourceType) {
            case "characters":
                int peopleIndex = Integer.parseInt(resourceFileURIStrings[2]);
                List<Texture> walkingTextures =
                        TextureUtils.getWalkingTextures(resourceManager,
                                resourceFilePath).get(peopleIndex);
                initTextures(walkingTextures);
                break;
            default:
                throw new URITypeNotDefinedException(resourceURI);
        }
    }

    private void initTextures(List<Texture> walkingTextures) {
        int ti = 0;
        getFaceDirFrameMap().put(180, new ArrayList<>());
        for (int i = 0; i < 3; i++, ti++) {
            getFaceDirFrameMap().get(180).add(walkingTextures.get(ti));
        }
        getFaceDirFrameMap().put(270, new ArrayList<>());
        for (int i = 0; i < 3; i++, ti++) {
            getFaceDirFrameMap().get(270).add(walkingTextures.get(ti));
        }
        getFaceDirFrameMap().put(90, new ArrayList<>());
        for (int i = 0; i < 3; i++, ti++) {
            getFaceDirFrameMap().get(90).add(walkingTextures.get(ti));
        }
        getFaceDirFrameMap().put(0, new ArrayList<>());
        for (int i = 0; i < 3; i++, ti++) {
            getFaceDirFrameMap().get(0).add(walkingTextures.get(ti));
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public Bindable getCurrentBindable() {
        long currentTime = System.currentTimeMillis();
        float elapsedTime = currentTime - getLastTime();
        elapsedTime /= 1000;

        if (elapsedTime >= getFps()) {
            this.setTexturePointer(this.getTexturePointer() + 1);
            this.setLastTime(currentTime);
        }

        if (getTexturePointer() >= 4) {
            setTexturePointer(0);
        }

        if (!getUnit().isMoving()) {
            setTexturePointer(1);
        }

        int tmpTexturePointer = getTexturePointer();

        if (getTexturePointer() == 3) {
            tmpTexturePointer -= 2;
        }

        List<Texture> list = getFaceDirFrameMap().get(getUnit().getFaceDir());
        if (list == null || list.isEmpty()) {
            list = getFaceDirFrameMap().get(this.getDrawFaceDir());
        }
        return list.get(tmpTexturePointer);
    }

    /**
     * <p>Getter for the field <code>faceDirFrameMap</code>.</p>
     *
     * @return a {@link java.util.Map} object.
     */
    public Map<Integer, List<Texture>> getFaceDirFrameMap() {
        return faceDirFrameMap;
    }

    /**
     * <p>Setter for the field <code>faceDirFrameMap</code>.</p>
     *
     * @param faceDirFrameMap a {@link java.util.Map} object.
     */
    public void setFaceDirFrameMap(Map<Integer, List<Texture>> faceDirFrameMap) {
        this.faceDirFrameMap = faceDirFrameMap;
    }

    /**
     * <p>Getter for the field <code>unit</code>.</p>
     *
     * @return a {@link com.xenoamess.cyan_potion.rpg_module.units.Unit} object.
     */
    public Unit getUnit() {
        return unit;
    }

    /**
     * <p>Setter for the field <code>unit</code>.</p>
     *
     * @param unit a {@link com.xenoamess.cyan_potion.rpg_module.units.Unit} object.
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
