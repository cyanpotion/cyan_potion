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

package com.xenoamess.cyan_potion.base.tools;

import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.glfw.GLFW.glfwSetWindowIcon;
import static org.lwjgl.stb.STBImage.stbi_load;

/**
 * @author ArtemisHD
 * @author XenoAmess
 */
public class ImageParser implements AutoCloseable {
    private static final Logger LOGGER = LoggerFactory.getLogger(ImageParser.class);

    private ByteBuffer image;
    private int width;
    private int height;

    private ImageParser(int width, int height, ByteBuffer image) {
        this.setImage(image);
        this.setHeight(height);
        this.setWidth(width);
    }

    @Override
    public void close() {
        MemoryUtil.memFree(this.getImage());
    }

    public static ImageParser loadImage(String path) {
        ByteBuffer image;
        int width, heigh;
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer comp = stack.mallocInt(1);
            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);

            image = stbi_load(path, w, h, comp, 4);
            if (image == null) {
                LOGGER.error("Could not load image resources : path = {}", path);
            }
            width = w.get();
            heigh = h.get();
        }
        return new ImageParser(width, heigh, image);
    }

    public static GLFWImage getGLFWImage(String path) {
        ImageParser imageparser = loadImage(path);
        GLFWImage res = GLFWImage.malloc().set(imageparser.getWidth(), imageparser.getHeight(), imageparser.getImage());
        imageparser.close();
        return res;
    }

    public static void setWindowIcon(long window, String path) {
        try (
                GLFWImage gameWindowIcon = getGLFWImage(path);
                GLFWImage.Buffer gameWindowIconBuffer = GLFWImage.malloc(1).put(0, gameWindowIcon);
        ) {
            glfwSetWindowIcon(window, gameWindowIconBuffer);
        }
    }

    public ByteBuffer getImage() {
        return image;
    }

    public void setImage(ByteBuffer image) {
        this.image = image;
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