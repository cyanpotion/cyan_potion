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

package com.xenoamess.cyan_potion.base.render;

import com.xenoamess.commonx.java.lang.IllegalArgumentExceptionUtilsx;
import com.xenoamess.cyan_potion.base.GameManager;
import com.xenoamess.cyan_potion.base.exceptions.TextureStateDisorderException;
import com.xenoamess.cyan_potion.base.memory.AbstractResource;
import com.xenoamess.cyan_potion.base.memory.ResourceInfo;
import com.xenoamess.cyan_potion.base.memory.ResourceManager;
import org.lwjgl.opengl.GL13;
import org.lwjgl.system.MemoryUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.function.Function;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.glActiveTexture;


/**
 * <p>Texture class.</p>
 *
 * @author XenoAmess
 * @version 0.143.0
 */
public class Texture extends AbstractResource implements Bindable {
    private static final Logger LOGGER =
            LoggerFactory.getLogger(Texture.class);

    /**
     * Constant <code>MIN_SAMPLER=0</code>
     */
    public static final int MIN_SAMPLER = 0;
    /**
     * Constant <code>MAX_SAMPLER=31</code>
     */
    public static final int MAX_SAMPLER = 31;

    private int glTexture2DInt = -1;

    /**
     * the width of the raw texture pic.
     * notice that this value is irrelevant to the display height.
     */
    private int width;

    /**
     * the height of the raw texture pic.
     * notice that this value is irrelevant to the display height.
     */
    private int height;


    /**
     * !!!NOTICE!!!
     * This function is used by reflection and don't delete it if you don't know about the plugin mechanism here.
     */
    public static final Function<GameManager, Void> PUT_TEXTURE_LOADER_PICTURE = (GameManager gameManager) -> {
        gameManager.getResourceManager().putResourceLoader(Texture.class, "picture",
                (Texture texture) -> {
                    texture.loadAsPictureTexture(texture.getResourceInfo());
                    return null;
                }
        );
        return null;
    };

    /**
     * !!!NOTICE!!!
     * <p>
     * This class shall never build from this constructor directly.
     * You shall always use ResourceManager.fetchResource functions to get this instance.
     *
     * @param resourceManager resource Manager
     * @param resourceInfo    resource info
     * @see ResourceManager#fetchResource(Class, ResourceInfo)
     */
    public Texture(ResourceManager resourceManager, ResourceInfo resourceInfo) {
        super(resourceManager, resourceInfo);
    }


    /**
     * <p>Getter for the field <code>glTexture2DInt</code>.</p>
     *
     * @return a int.
     */
    public int getGlTexture2DInt() {
        return this.glTexture2DInt;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void bind(int sampler) {
        super.bind(sampler);
        if ((this.getGlTexture2DInt() == -1) && (this.isInMemory())) {
            throw new TextureStateDisorderException(this);
        }

        if ((this.getGlTexture2DInt() != -1) && (!this.isInMemory())) {
            throw new TextureStateDisorderException(this);
        }

        if (this.getGlTexture2DInt() == -1) {
            throw new TextureStateDisorderException(this);
        }
        if (sampler >= MIN_SAMPLER && sampler <= MAX_SAMPLER) {
            glActiveTexture(GL13.GL_TEXTURE0 + sampler);
            glBindTexture(GL_TEXTURE_2D, this.getGlTexture2DInt());
        }
    }

    /**
     * <p>bake.</p>
     *
     * @param width      a int.
     * @param height     a int.
     * @param byteBuffer byteBuffer
     */
    public void bake(int width, int height, ByteBuffer byteBuffer) {
        this.setWidth(width);
        this.setHeight(height);
        generate(byteBuffer);

        this.setMemorySize(1L * width * height * 4);
        this.getResourceManager().load(this);
    }

    /**
     * <p>bake.</p>
     *
     * @param singleWidth  a int.
     * @param singleHeight a int.
     * @param entireWidth  a int.
     * @param entireHeight a int.
     * @param startWidth   a int.
     * @param startHeight  a int.
     * @param pixelsRaw    an array of {@link int} objects.
     */
    public void bake(int singleWidth, int singleHeight, int entireWidth,
                     int entireHeight, int startWidth, int startHeight,
                     int[] pixelsRaw) {
        this.setWidth(singleWidth);
        this.setHeight(singleHeight);

        final ByteBuffer byteBuffer = MemoryUtil.memAlloc(getWidth() * getHeight() * 4);

        for (int i = 0; i < getHeight(); i++) {
            for (int j = 0; j < getWidth(); j++) {
                int pixel =
                        pixelsRaw[(startHeight + i) * entireWidth + startWidth + j];

                // RED
                byteBuffer.put((byte) ((pixel >> 16) & 0xFF));

                // GREEN
                byteBuffer.put((byte) ((pixel >> 8) & 0xFF));

                // BLUE
                byteBuffer.put((byte) (pixel & 0xFF));

                // ALPHA
                byteBuffer.put((byte) ((pixel >> 24) & 0xFF));
            }
        }
        byteBuffer.flip();
        generate(byteBuffer);
        MemoryUtil.memFree(byteBuffer);

        this.setMemorySize(1L * singleWidth * singleHeight * 4);
        this.getResourceManager().load(this);
    }

    private void generate(ByteBuffer byteBuffer) {
        this.setGlTexture2DInt(glGenTextures());
        glBindTexture(GL_TEXTURE_2D, this.getGlTexture2DInt());
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, getWidth(), getHeight(), 0,
                GL_RGBA, GL_UNSIGNED_BYTE, byteBuffer);
    }

    /**
     * <p>loadAsPictureTexture.</p>
     *
     * @param resourceInfo resourceInfo
     */
    public void loadAsPictureTexture(ResourceInfo resourceInfo) {
        BufferedImage bufferedImage = null;
        try {
            bufferedImage = ImageIO.read(resourceInfo.fileObject.getContent().getInputStream());
        } catch (IOException e) {
            LOGGER.error("Texture.loadAsPictureTexture(String fullResourceURI) fails:{}", resourceInfo, e);
        }


        IllegalArgumentExceptionUtilsx.isAnyNullInParamsThenThrowIllegalArgumentException(bufferedImage);
        final int entireWidth = bufferedImage.getWidth();
        final int entireHeight = bufferedImage.getHeight();
        final int[] pixelsRaw = bufferedImage.getRGB(0, 0, entireWidth,
                entireHeight, null, 0, entireWidth);
        this.bake(entireWidth, entireHeight, entireWidth, entireHeight, 0, 0,
                pixelsRaw);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void forceClose() {
        if (this.getGlTexture2DInt() != -1) {
            glDeleteTextures(this.getGlTexture2DInt());
            this.setGlTexture2DInt(-1);
        }
        this.setMemorySize(0);
    }

    /**
     * <p>Setter for the field <code>glTexture2DInt</code>.</p>
     *
     * @param glTexture2DInt a int.
     */
    public void setGlTexture2DInt(int glTexture2DInt) {
        this.glTexture2DInt = glTexture2DInt;
    }

    /**
     * <p>Getter for the field <code>width</code>.</p>
     *
     * @return a int.
     */
    public int getWidth() {
        return width;
    }

    private void setWidth(int width) {
        this.width = width;
    }

    /**
     * <p>Getter for the field <code>height</code>.</p>
     *
     * @return a int.
     */
    public int getHeight() {
        return height;
    }

    private void setHeight(int height) {
        this.height = height;
    }
}
