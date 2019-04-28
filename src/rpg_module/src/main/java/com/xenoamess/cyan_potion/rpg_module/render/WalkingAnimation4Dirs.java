package com.xenoamess.cyan_potion.rpg_module.render;

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

    public WalkingAnimation4Dirs(int fps, Unit unit, String resourceURI, ResourceManager resourceManager) {
        super(fps);
        this.setUnit(unit);

        String[] resourceFileURIStrings = resourceURI.split(":");
        String resourceFilePath = resourceFileURIStrings[0];
        String resourceType = resourceFileURIStrings[1];
        switch (resourceType) {
            case "characters":
                int peopleIndex = Integer.parseInt(resourceFileURIStrings[2]);
                List<Texture> walkingTextures = Texture.getWalkingTextures(resourceManager, resourceFilePath).get(peopleIndex);
                initTextures(walkingTextures);
                break;
            default:
                throw new Error("textureType not defined : " + resourceType);
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

        if (getUnit().isMoving() == false) {
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

    //no this logic is wrong.
    //should not have this function in this class.
    //SORRY.
    //    public static WalkingAnimation4Dirs GetResourceFromURI(DataCenter dataCenter, String fullResourceURI) {
    //        WalkingAnimation4Dirs res;
    //        String[] resourceFileURIStrings = fullResourceURI.split(":");
    //        String resourceFilePath = resourceFileURIStrings[0];
    //        String resourceType = resourceFileURIStrings[1];
    //        switch (resourceType) {
    //            case "characters":
    //                int peopleIndex = Integer.parseInt(resourceFileURIStrings[2]);
    //                int textureIndex = Integer.parseInt(resourceFileURIStrings[3]);
    //                Texture.GetWalkingTextures(dataCenter, resourceFilePath);
    //                res = new WalkingAnimation4Dirs();
    //                break;
    //            default:
    //                throw new Error("textureType not defined : " + resourceType);
    //        }
    //        return res;
    //
    //        return res;
    //    }


}
