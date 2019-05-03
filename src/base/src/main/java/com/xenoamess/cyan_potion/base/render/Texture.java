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

import com.xenoamess.cyan_potion.base.GameManager;
import com.xenoamess.cyan_potion.base.URITypeNotDefinedException;
import com.xenoamess.cyan_potion.base.io.FileUtil;
import com.xenoamess.cyan_potion.base.memory.AbstractResource;
import com.xenoamess.cyan_potion.base.memory.ResourceManager;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL13;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.function.BiFunction;
import java.util.function.Function;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.glActiveTexture;

/**
 * @author XenoAmess
 */
public class Texture extends AbstractResource implements Bindable {
    public static final int MIN_SAMPLER = 0;
    public static final int MAX_SAMPLER = 31;

    private int glTexture2DInt = -1;
    private int width;
    private int height;


    /**
     * This is used by reflection and don't delete it if you don't know about the plugin mechanism here.
     */
    public static final Function<GameManager, Void> PUT_TEXTURE_LOADER_PICTURE = (GameManager gameManager) -> {
        gameManager.getResourceManager().putResourceLoader(Texture.class, "picture",
                (Texture texture, String resourceFileURIStrings) -> {
                    texture.loadAsPictureTexture(resourceFileURIStrings);
                    return null;
                }
        );
        return null;
    };


    public Texture(ResourceManager resourceManager, String resourceURI) {
        super(resourceManager, resourceURI);
    }


    public int getGlTexture2DInt() {
        return this.glTexture2DInt;
    }

    @Override
    public void bind(int sampler) {
        this.load();
        if ((this.getGlTexture2DInt() == -1) != (!this.isInMemory())) {
            throw new Error("Texture state chaos : " + this.getGlTexture2DInt() + " , " + this.isInMemory() + " , " + this.getFullResourceURI());
        }

        if (this.getGlTexture2DInt() == -1) {
            throw new Error("Binding non-ready texture : " + this.getFullResourceURI());
        }
        if (sampler >= MIN_SAMPLER && sampler <= MAX_SAMPLER) {
            glActiveTexture(GL13.GL_TEXTURE0 + sampler);
            glBindTexture(GL_TEXTURE_2D, this.getGlTexture2DInt());
        }
    }

    public void bake(int width, int height, ByteBuffer byteBuffer) {
        this.setWidth(width);
        this.setHeight(height);
        generate(byteBuffer);

        this.setMemorySize(width * height * 4);
        this.getResourceManager().load(this);
    }

    public void bake(int singleWidth, int singleHeight, int entireWidth,
                     int entireHeight, int startWidth, int startHeight,
                     int[] pixelsRaw) {
        this.setWidth(singleWidth);
        this.setHeight(singleHeight);

        final ByteBuffer byteBuffer =
                BufferUtils.createByteBuffer(getWidth() * getHeight() * 4);
        for (int i = 0; i < getHeight(); i++) {
            for (int j = 0; j < getWidth(); j++) {
                int pixel =
                        pixelsRaw[(startHeight + i) * entireWidth + startWidth + j];
                //                if (pixel < 0) {
                //                    pixel += Integer.MAX_VALUE;
                //                    pixel += Integer.MAX_VALUE;
                //                }

                //                System.out.println((byte) ((pixel >> 16) &
                //                0xFF));
                //                System.out.println((byte) ((pixel >> 8) &
                //                0xFF));
                //                System.out.println((byte) ((pixel >> 0) &
                //                0xFF));
                //                System.out.println((byte) ((pixel >> 24) &
                //                0xFF));
                //                System.out.println(i + " " + j + " " + pixel);

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
        byteBuffer.clear();

        this.setMemorySize(singleWidth * singleHeight * 4);
        this.getResourceManager().load(this);
    }

    void generate(ByteBuffer byteBuffer) {
        this.setGlTexture2DInt(glGenTextures());
        glBindTexture(GL_TEXTURE_2D, this.getGlTexture2DInt());
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, getWidth(), getHeight(), 0,
                GL_RGBA, GL_UNSIGNED_BYTE, byteBuffer);
        byteBuffer.clear();
    }

    public void loadAsPictureTexture(String resourceFileURIString) {
        String[] resourceFileURIStrings = resourceFileURIString.split(":");
        final String resourceFilePath = resourceFileURIStrings[1];
        BufferedImage bufferedImage = null;

        final URI fileURI = FileUtil.getURI(resourceFilePath);
        try {
            bufferedImage = ImageIO.read(new File(fileURI));
        } catch (IOException e) {
            e.printStackTrace();
        }
        final int entireWidth = bufferedImage.getWidth();
        final int entireHeight = bufferedImage.getHeight();
        final int[] pixelsRaw = bufferedImage.getRGB(0, 0, entireWidth,
                entireHeight, null, 0, entireWidth);
        this.bake(entireWidth, entireHeight, entireWidth, entireHeight, 0, 0,
                pixelsRaw);
    }

    @Override
    public void forceLoad() {
        /**
         * example       com.xenoamess.gearbar.render
         * .WalkingAnimation4Dirs:/www/img/characters/Actor1.png:4:0
         */
        final String[] resourceFileURIStrings =
                this.getFullResourceURI().split(":");
        final String resourceFilePath = resourceFileURIStrings[1];
        final String resourceType = resourceFileURIStrings[2];

        BiFunction<Texture, String, Void> loader = this.getResourceManager().getResourceLoader(Texture.class,
                resourceType);
        if (loader == null) {
            throw new URITypeNotDefinedException(this.getFullResourceURI());
        }
        loader.apply(this, this.getFullResourceURI());
    }

    @Override
    public void forceClose() {
        if (this.getGlTexture2DInt() != -1) {
            glDeleteTextures(this.getGlTexture2DInt());
            this.setGlTexture2DInt(-1);
        }
        this.setMemorySize(0);
    }

    public void setGlTexture2DInt(int glTexture2DInt) {
        this.glTexture2DInt = glTexture2DInt;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}
