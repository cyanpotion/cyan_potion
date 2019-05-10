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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author XenoAmess
 */
public class WalkingAnimation4Dirs extends Animation {
    private Map<Integer, List<Texture>> faceDirFrameMap = new HashMap<>();
    private Unit unit;
    private int drawFaceDir;

    public void setFaceDir(int faceDir) {
        if (this.getFaceDirFrameMap().containsKey(faceDir)) {
            this.setDrawFaceDir(faceDir);
        }
    }

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

    public Map<Integer, List<Texture>> getFaceDirFrameMap() {
        return faceDirFrameMap;
    }

    public void setFaceDirFrameMap(Map<Integer, List<Texture>> faceDirFrameMap) {
        this.faceDirFrameMap = faceDirFrameMap;
    }

    public Unit getUnit() {
        return unit;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    public int getDrawFaceDir() {
        return drawFaceDir;
    }

    public void setDrawFaceDir(int drawFaceDir) {
        this.drawFaceDir = drawFaceDir;
    }
}
