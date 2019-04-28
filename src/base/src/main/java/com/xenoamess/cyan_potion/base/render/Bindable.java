package com.xenoamess.cyan_potion.base.render;

import org.lwjgl.opengl.GL11;

import static org.lwjgl.opengl.GL11.glBindTexture;

/**
 * @author XenoAmess
 */
public interface Bindable {

    void bind(int sampler);

    default void unbind() {
        glBindTexture(GL11.GL_TEXTURE_2D, 0);
    }

    default void bind() {
        bind(0);
    }

}
