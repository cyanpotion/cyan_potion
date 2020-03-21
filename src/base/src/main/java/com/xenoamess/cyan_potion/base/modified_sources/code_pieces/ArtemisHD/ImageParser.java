/*
 * MIT License
 *
 * Copyright (c) 2019 ArtemisHD
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

/*
URL:http://discourse.glfw.org/t/set-window-icon-in-lwjgl-3-1/863/4
Its done and its working,
thanks for helping me out,
I am posting my full code for others to freely use.
This work is licensed under MIT.
ArtemisHD Feb '172

----------

I improved it and it looks better now.
(in my own opinion).
MIT also.
Fell free to use.
XenoAmess 2018/01/29

*/

package com.xenoamess.cyan_potion.base.modified_sources.code_pieces.ArtemisHD;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.glfw.GLFW.glfwSetWindowIcon;
import static org.lwjgl.stb.STBImage.stbi_load;

/**
 * <p>ImageParser class.</p>
 *
 * @author ArtemisHD
 * @author XenoAmess
 * @version 0.162.1-SNAPSHOT
 */
public class ImageParser implements Closeable {
    @JsonIgnore
    private static final transient Logger LOGGER =
            LoggerFactory.getLogger(ImageParser.class);

    private ByteBuffer image;
    private int width;
    private int height;

    private ImageParser(int width, int height, ByteBuffer image) {
        this.setImage(image);
        this.setHeight(height);
        this.setWidth(width);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() {
        MemoryUtil.memFree(this.getImage());
    }

    /**
     * <p>loadImage.</p>
     *
     * @param path path
     * @return return
     */
    public static ImageParser loadImage(String path) {
        ByteBuffer image;
        int width;
        int height;

        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer comp = stack.mallocInt(1);
            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);

            image = stbi_load(path, w, h, comp, 4);
            if (image == null) {
                LOGGER.error("Could not load image resources : path = {}",
                        path);
            }
            width = w.get();
            height = h.get();
        }
        return new ImageParser(width, height, image);
    }

    /**
     * <p>getGLFWImage.</p>
     *
     * @param path path
     * @return return
     */
    public static GLFWImage getGLFWImage(String path) {
        ImageParser imageparser = loadImage(path);
        GLFWImage res = GLFWImage.malloc().set(imageparser.getWidth(),
                imageparser.getHeight(), imageparser.getImage());
        imageparser.close();
        return res;
    }

    /**
     * <p>setWindowIcon.</p>
     *
     * @param window a long.
     * @param path   a {@link java.lang.String} object.
     */
    public static void setWindowIcon(long window, String path) {
        try (
                GLFWImage gameWindowIcon = getGLFWImage(path);
                GLFWImage.Buffer gameWindowIconBuffer =
                        GLFWImage.malloc(1).put(0, gameWindowIcon)
        ) {
            glfwSetWindowIcon(window, gameWindowIconBuffer);
        }
    }

    /**
     * <p>Getter for the field <code>image</code>.</p>
     *
     * @return return
     */
    public ByteBuffer getImage() {
        return image;
    }

    /**
     * <p>Setter for the field <code>image</code>.</p>
     *
     * @param image image
     */
    public void setImage(ByteBuffer image) {
        this.image = image;
    }

    /**
     * <p>Getter for the field <code>width</code>.</p>
     *
     * @return a int.
     */
    public int getWidth() {
        return width;
    }

    /**
     * <p>Setter for the field <code>width</code>.</p>
     *
     * @param width a int.
     */
    public void setWidth(int width) {
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

    /**
     * <p>Setter for the field <code>height</code>.</p>
     *
     * @param height a int.
     */
    public void setHeight(int height) {
        this.height = height;
    }
}
