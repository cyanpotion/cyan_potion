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

import com.xenoamess.cyan_potion.base.GameManager;
import com.xenoamess.cyan_potion.base.memory.ResourceInfo;
import com.xenoamess.cyan_potion.base.memory.ResourceManager;
import com.xenoamess.cyan_potion.base.render.Texture;
import com.xenoamess.cyan_potion.base.visual.AbstractPictureInterface;
import com.xenoamess.cyan_potion.base.visual.Animation;
import com.xenoamess.cyan_potion.base.visual.Picture;
import com.xenoamess.cyan_potion.rpg_module.units.Unit;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.joml.Math;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>WalkingAnimation4Dirs class.</p>
 *
 * @author XenoAmess
 * @version 0.162.3
 */
@EqualsAndHashCode(callSuper = true)
@ToString
public class WalkingAnimation4Dirs extends Animation {

    @Getter
    private final ResourceInfo<WalkingAnimation4DirsResource> resourceInfo;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @Getter
    private final ResourceManager resourceManager;

    public boolean load() {
        if (!this.faceDirFrameMap.isEmpty()) {
            return true;
        }
        WalkingAnimation4DirsResource walkingAnimation4DirsResource =
                this.resourceInfo.fetchResource(this.getResourceManager());
        walkingAnimation4DirsResource.load();
        this.initPictures(this.buildPictures(walkingAnimation4DirsResource.getWalkingTextures()));
        return true;
    }

    public void close() {
        faceDirFrameMap.clear();
    }


    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @Getter
    @Setter
    private Unit unit;

    @Getter
    @Setter
    private final Map<Integer, List<AbstractPictureInterface>> faceDirFrameMap = new ConcurrentHashMap<>();

    @Getter
    @Setter
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
    public WalkingAnimation4Dirs(int fps, Unit unit, ResourceInfo<WalkingAnimation4DirsResource> resourceInfo,
                                 ResourceManager resourceManager) {
        super(fps);
        this.setUnit(unit);
        this.resourceInfo = resourceInfo;
        this.resourceManager = resourceManager;

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
    public AbstractPictureInterface getCurrentPicture(GameManager gameManager) {
        this.load();
        return super.getCurrentPicture(gameManager);
    }

    @Override
    public int getFrameNum() {
        return 4;
    }

    @Override
    public AbstractPictureInterface getFrame(int index) {
        List<AbstractPictureInterface> list = getFaceDirFrameMap().get(getUnit().getFaceDir());
        if (list == null || list.isEmpty()) {
            list = getFaceDirFrameMap().get(this.getDrawFaceDir());
        }
        return list.get(index);
    }

    @Override
    public int getIndex() {
        if (!getUnit().isMoving()) {
            setTexturePointer(1);
            return 1;
        }
        int tmpIndex = (int) Math.floor(getTexturePointer());
        while (tmpIndex >= this.getFrameNum()) {
            tmpIndex -= this.getFrameNum();
        }
        if (tmpIndex == 3) {
            tmpIndex -= 2;
        }
        return tmpIndex;
    }
}
