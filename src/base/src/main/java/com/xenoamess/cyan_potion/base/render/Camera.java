package com.xenoamess.cyan_potion.base.render;

import org.joml.Vector3f;

/**
 * @author XenoAmess
 */
public class Camera {
    private final Vector3f position;

    public Camera(int initX, int initY) {
        position = new Vector3f(initX, initY, 0);
    }

    public Vector3f getPosition() {
        return position;
    }

}
