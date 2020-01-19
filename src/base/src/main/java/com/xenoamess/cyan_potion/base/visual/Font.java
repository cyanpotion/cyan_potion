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

package com.xenoamess.cyan_potion.base.visual;

import com.xenoamess.commons.as_final_field.AsFinalField;
import com.xenoamess.commons.io.FileUtils;
import com.xenoamess.commons.primitive.collections.lists.array_lists.IntArrayList;
import com.xenoamess.commons.primitive.iterators.IntIterator;
import com.xenoamess.cyan_potion.base.GameManager;
import com.xenoamess.cyan_potion.base.GameWindow;
import com.xenoamess.cyan_potion.base.memory.AbstractResource;
import com.xenoamess.cyan_potion.base.memory.ResourceInfo;
import com.xenoamess.cyan_potion.base.memory.ResourceManager;
import com.xenoamess.cyan_potion.base.render.Shader;
import org.apache.commons.vfs2.FileObject;
import org.joml.Vector4f;
import org.lwjgl.stb.STBTTAlignedQuad;
import org.lwjgl.stb.STBTTPackContext;
import org.lwjgl.stb.STBTTPackedchar;
import org.lwjgl.system.MemoryUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static com.xenoamess.commons.as_final_field.AsFinalFieldUtils.asFinalFieldSet;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.stb.STBImageWrite.stbi_write_bmp;
import static org.lwjgl.stb.STBTruetype.*;

/**
 * <p>Font class.</p>
 *
 * @author XenoAmess
 * @version 0.143.0
 */
public class Font extends AbstractResource {
    private static final Logger LOGGER =
            LoggerFactory.getLogger(Font.class);

    /**
     * the default DEFAULT_FONT_FILE_PATH if you does not set it from setting
     * file.
     *
     * @see com.xenoamess.cyan_potion.base.GameManager
     */
    public static final String DEFAULT_DEFAULT_FONT_RESOURCE_URI =
            "resources/www/fonts/SourceHanSans-Normal.ttc:ttfFile";

    /**
     * Constant <code>TEST_PRINT_FONT_BMP=false</code>
     */
    public static final boolean TEST_PRINT_FONT_BMP = false;

//    /**
//     * Constant <code>MAX_NUM=40960</code>
//     */
//    public static final int MAX_NUM = 40960;
    /**
     * size of each font pic.
     */
    public static final int EACH_SIZE = 1024;
//    public static final int EACH_SIZE = 1024 * 16;
    /**
     * num of characters on each font pic.
     */
    public static final int EACH_CHAR_NUM = 1024;
//    public static final int EACH_CHAR_NUM = 65536;
    /**
     * num of characters on each font pic.
     */
    public static final int PIC_NUM = ((int) (Character.MAX_VALUE) + 1) / EACH_CHAR_NUM;
//    public static final int PIC_NUM = ((int) (100000) / EACH_CHAR_NUM);
    /**
     * Constant <code>BITMAP_W=MAX_SIZE</code>
     */
    public static final int BITMAP_W = EACH_SIZE;
    /**
     * Constant <code>BITMAP_H=MAX_SIZE</code>
     */
    public static final int BITMAP_H = EACH_SIZE;
    /**
     * Constant <code>SCALE=36.0f</code>
     */
    public static final float SCALE = 36.0f;

    private final IntArrayList fontTextures = new IntArrayList();
    private final List<STBTTPackedchar.Buffer> charDatas = new ArrayList<>();

    @AsFinalField
    private static Font defaultFont = null;

    private static Font currentFont;


    private GameWindow gameWindow;

    /**
     * !!!NOTICE!!!
     * <p>
     * This class shall never build from this constructor directly.
     * You shall always use ResourceManager.fetchResource functions to get this instance.
     *
     * @param resourceManager resource Manager
     * @param resourJson      resour Json
     * @see ResourceManager#fetchResource(Class, ResourceInfo)
     */
    public Font(ResourceManager resourceManager, ResourceInfo resourJson) {
        super(resourceManager, resourJson);
    }


    /**
     * !!!NOTICE!!!
     * This function is used by reflection and don't delete it if you don't know about the plugin mechanism here.
     */
    public static final Function<GameManager, Void> PUT_FONT_LOADER_TTF_FILE = (GameManager gameManager) -> {
        gameManager.getResourceManager().putResourceLoader(Font.class, "ttfFile",
                (Font font) -> {
                    font.loadAsTtfFileFont(font.getResourceInfo());
                    return null;
                }
        );
        return null;
    };

    private void loadAsTtfFileFont(ResourceInfo resourceInfo) {
        this.loadBitmap(resourceInfo.fileObject);
    }

    /**
     * this buffer will be freed after init(),
     * so please does never use it after that.
     */
    private List<ByteBuffer> bitmaps = new ArrayList<>(PIC_NUM);

    /**
     * <p>loadBitmap.</p>
     *
     * @param fileObject fileObject
     */
    public void loadBitmap(FileObject fileObject) {
        ByteBuffer ttf = FileUtils.loadBuffer(fileObject, true);
        this.setMemorySize(1L * PIC_NUM * BITMAP_W * BITMAP_H);
        try (STBTTPackContext pc = STBTTPackContext.malloc()) {
//            ResourceSizeLargerThanGlMaxTextureSizeException.check(this);

            for (int i = 0; i < PIC_NUM; i++) {
                ByteBuffer bitmapLocal = MemoryUtil.memAlloc(BITMAP_W * BITMAP_H);
                stbtt_PackBegin(pc, bitmapLocal, BITMAP_W, BITMAP_H, 0, 1, 0);
                STBTTPackedchar.Buffer charDataLocal =
                        STBTTPackedchar.malloc(6 * EACH_CHAR_NUM);
                charDataLocal.position(0);
                charDataLocal.limit(EACH_CHAR_NUM);
                stbtt_PackSetOversampling(pc, 1, 1);
                stbtt_PackFontRange(pc, ttf, 0, SCALE, i * EACH_CHAR_NUM, charDataLocal);

                stbtt_PackEnd(pc);
                if (TEST_PRINT_FONT_BMP) {
                    stbi_write_bmp("font_texture" + i + ".bmp", BITMAP_W, BITMAP_H, 1,
                            bitmapLocal);
                }

                this.bitmaps.add(bitmapLocal);
                this.getCharDatas().add(charDataLocal);
            }
//            this.setMemorySize(charDataLocal.capacity());
        }
    }


    /**
     * <p>init.</p>
     *
     * @param gameWindow gameWindow
     */
    public void init(GameWindow gameWindow) {
        this.setGameWindow(gameWindow);

        for (int i = 0; i < PIC_NUM; i++) {
            int nowTextureIndex = glGenTextures();
            this.getFontTextures().addPrimitive(nowTextureIndex);
            glBindTexture(GL_TEXTURE_2D, nowTextureIndex);
            glTexImage2D(GL_TEXTURE_2D, 0, GL_ALPHA, BITMAP_W, BITMAP_H, 0,
                    GL_ALPHA, GL_UNSIGNED_BYTE, this.bitmaps.get(i));
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
            MemoryUtil.memFree(this.bitmaps.get(i));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void bind() {
        super.bind();
        Shader.unbind();
        gameWindow.bindGlViewportToFullWindow();
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        glOrtho(0.0, getGameWindow().getRealWindowWidth(),
                getGameWindow().getRealWindowHeight(), 0.0, -1.0, 1.0);
        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();
    }


    /**
     * <p>drawBoxTC.</p>
     *
     * @param x0 a float.
     * @param y0 a float.
     * @param x1 a float.
     * @param y1 a float.
     * @param s0 a float.
     * @param t0 a float.
     * @param s1 a float.
     * @param t1 a float.
     */
    public static void drawBoxTC(float x0, float y0, float x1, float y1,
                                 float s0, float t0, float s1, float t1) {
        glTexCoord2f(s0, t0);
        glVertex2f(x0, y0);
        glTexCoord2f(s1, t0);
        glVertex2f(x1, y0);
        glTexCoord2f(s1, t1);
        glVertex2f(x1, y1);
        glTexCoord2f(s0, t1);
        glVertex2f(x0, y1);
    }

    private final STBTTAlignedQuad q = STBTTAlignedQuad.malloc();
    private final FloatBuffer xb = MemoryUtil.memAllocFloat(1);
    private final FloatBuffer yb = MemoryUtil.memAllocFloat(1);

//    public void drawText(float x, float y, float scaleX, float scaleY,
//    float characterSpace, Vector4f color, String text) {
//
//        this.bind();
//
//        xb.put(0, x);
//        yb.put(0, y);
//
////        charData.position(font * MAX_NUM);
//        charData.position(0 * MAX_NUM);
//        glEnable(GL_TEXTURE_2D);
//        glBindTexture(GL_TEXTURE_2D, fontTexture);
//
//        if (color != null) {
//            glColor4f(color.x, color.y, color.z, color.w);
//        }
//
//        glBegin(GL_QUADS);
//        float lastX_ = x;
//        float lastY_ = y;
//        float lastX_Should = x;
//        float lastY_Should = y;
//        for (int i = 0; i < text.length(); i++) {
//            stbtt_GetPackedQuad(charData, BITMAP_W, BITMAP_H, text.charAt
//            (i), xb, yb, q, false);
////            LOGGER.debug("x0:" + q.x0() + " x1:" + q.x1() + " y0:"
// + q.y0() + " y1:" + q.y1());
//            float charWidthShould = q.x1() - q.x0();
//            float charHeightShould = q.y1() - q.y0();
//            float spaceLeftToCharShould = q.x0() - lastX_Should;
//            float spaceUpToCharShould = q.y0() - lastY_Should;
//            float nowX0 = lastX_ + spaceLeftToCharShould * scaleX;
////            float nowY0 = lastY_ + spaceUpToCharShould * scaleY;
//            float nowY0 = y;
////            LOGGER.debug(charWidthShould + " " + charHeightShould +
// " " + spaceLeftToCharShould + " " + spaceUpToCharShould + " " + nowX0 + "
// " + nowY0);
//            drawBoxTC(
//                    nowX0, nowY0, nowX0 + charWidthShould * scaleX, nowY0 +
//                    charHeightShould * scaleY,
////                    q.x0(), q.y0(), q.x1(), q.y1(),
//                    q.s0(), q.t0(), q.s1(), q.t1()
//            );
//            lastX_ = nowX0 + charWidthShould * scaleX;
//            lastY_ = y;
//            lastX_Should = q.x1();
//            lastY_Should = y;
//        }
//        glEnd();
//    }

    /**
     * <p>drawText.</p>
     *
     * @param x              a float.
     * @param y              a float.
     * @param scaleX         a float.
     * @param scaleY         a float.
     * @param height         a float.
     * @param characterSpace a float.
     * @param color          a {@link org.joml.Vector4f} object.
     * @param text           a {@link java.lang.String} object.
     */
    public void drawText(float x, float y, float scaleX, float scaleY,
                         float height, float characterSpace, Vector4f color,
                         String text) {

//        STBTTFontinfo fontInfo = STBTTFontinfo.create();
        this.bind();

        getXb().put(0, x);
        getYb().put(0, y);

        glEnable(GL_TEXTURE_2D);

        if (color != null) {
            glColor4f(color.x, color.y, color.z, color.w);
        }

        float lastXReal = x;
        float lastYReal = y;
        float lastXShould = x;
        float lastYShould = y;
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) < 32) {
                continue;
            }
            glBindTexture(GL_TEXTURE_2D, getFontTextures().getPrimitive(text.charAt(i) / EACH_CHAR_NUM));
            glBegin(GL_QUADS);
            stbtt_GetPackedQuad(getCharDatas().get(text.charAt(i) / EACH_CHAR_NUM), BITMAP_W, BITMAP_H,
                    text.charAt(i) % EACH_CHAR_NUM, getXb(), getYb(), getQ(), false);
//            stbtt_GetPackedQuad(getCharDatas().get(0), BITMAP_W, BITMAP_H,
//                    150, getXb(), getYb(), getQ(), false);
//            LOGGER.debug("x0:" + q.x0() + " x1:" + q.x1() + " y0:" +
//            q.y0() + " y1:" + q.y1());
//            LOGGER.debug("s0:" + q.s0() + " s1:" + q.s1() + " t0:" +
//            q.t0() + " t1:" + q.t1());
            float charWidthShould = getQ().x1() - getQ().x0();
            float charHeightShould = getQ().y1() - getQ().y0();
            float spaceLeftToCharShould = getQ().x0() - lastXShould;
            float spaceUpToCharShould = getQ().y0() - lastYShould;
            float nowX0 = lastXReal + spaceLeftToCharShould * scaleX;
            float nowY0 = lastYReal + spaceUpToCharShould * scaleY;
//            float nowY0 = y;
//            LOGGER.debug(charWidthShould + " " + charHeightShould + "
//            " + spaceLeftToCharShould + " " + spaceUpToCharShould + " " +
//            nowX0 + " " + nowY0);

            drawBoxTC(
                    nowX0, nowY0 + height * 0.8f,
                    nowX0 + charWidthShould * scaleX,
                    nowY0 + charHeightShould * scaleY + height * 0.8f,
//                    q.x0(), q.y0(), q.x1(), q.y1(),
                    getQ().s0(), getQ().t0(), getQ().s1(), getQ().t1()
            );
            lastXReal = nowX0 + charWidthShould * scaleX;
            lastYReal = y;
            lastXShould = getQ().x1();
            lastYShould = y;
            glEnd();
        }
    }

    /**
     * <p>drawTextFillAreaLeftTop.</p>
     *
     * @param x1             a float.
     * @param y1             a float.
     * @param width          a float.
     * @param height         a float.
     * @param characterSpace a float.
     * @param color          a {@link org.joml.Vector4f} object.
     * @param text           a {@link java.lang.String} object.
     */
    public void drawTextFillAreaLeftTop(float x1, float y1, float width,
                                        float height, float characterSpace,
                                        Vector4f color, String text) {

        this.bind();
        getXb().put(0, x1);
        getYb().put(0, y1);

        glEnable(GL_TEXTURE_2D);

        if (color != null) {
            glColor4f(color.x, color.y, color.z, color.w);
        }

        float lastXReal = x1;
        float lastYReal = y1;
        float lastXShould = x1;
        float lastYShould = y1;

        float x3 = Float.MIN_VALUE;
        float y3 = Float.MIN_VALUE;

        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) < 32) {
                continue;
            }
            glBindTexture(GL_TEXTURE_2D, getFontTextures().getPrimitive(text.charAt(i) / EACH_CHAR_NUM));
            glBegin(GL_QUADS);
            stbtt_GetPackedQuad(getCharDatas().get(text.charAt(i) / EACH_CHAR_NUM), BITMAP_W, BITMAP_H,
                    text.charAt(i) % EACH_CHAR_NUM, getXb(), getYb(), getQ(), false);
//            LOGGER.debug("x0:" + q.x0() + " x1:" + q.x1() + " y0:" +
//            q.y0() + " y1:" + q.y1());
            float charWidthShould = getQ().x1() - getQ().x0();
            float charHeightShould = getQ().y1() - getQ().y0();
            float spaceLeftToCharShould = getQ().x0() - lastXShould;
            float spaceUpToCharShould = getQ().y0() - lastYShould;
            float nowX0 = lastXReal + spaceLeftToCharShould;
            float nowY0 = y1;
//            LOGGER.debug(charWidthShould + " " + charHeightShould + "
//            " + spaceLeftToCharShould + " " + spaceUpToCharShould + " " +
//            nowX0 + " " + nowY0);
//            drawBoxTC(
//                    nowX0, nowY0, nowX0 + charWidthShould * 1, nowY0 +
//                    charHeightShould * 1,
//                    q.s0(), q.t0(), q.s1(), q.t1()
//            );
            x3 = Math.max(x3, nowX0 + charWidthShould);
            y3 = Math.max(y3, nowY0 + charHeightShould);
            lastXReal = nowX0 + charWidthShould * 1;
            lastYReal = y1;
            lastXShould = getQ().x1();
            lastYShould = y1;
            glEnd();
        }
        float scaleX = width / (x3 - x1);
        float scaleY = height / (y3 - y1);
        if (width < 0) {
            scaleX = scaleY;
        } else if (height < 0) {
            scaleY = scaleX;
        }
        drawText(x1, y1, scaleX, scaleY, height, characterSpace, color, text);
    }

    private float maxCharHeight = -1;

    /**
     * <p>Getter for the field <code>maxCharHeight</code>.</p>
     *
     * @return a float.
     */
    public float getMaxCharHeight() {
        if (maxCharHeight == -1) {
            this.bind();
            getXb().put(0, 0);
            getYb().put(0, 0);
            glEnable(GL_TEXTURE_2D);
            glBindTexture(GL_TEXTURE_2D, getFontTextures().get(0));

//            glBegin(GL_QUADS);

            float x3 = Float.MIN_VALUE;
            float y3 = Float.MIN_VALUE;
            String text =
                    "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
            float minY = Float.MAX_VALUE;
            float maxY = Float.MIN_VALUE;

            for (char chr : text.toCharArray()) {
                glBindTexture(GL_TEXTURE_2D, getFontTextures().getPrimitive(chr / EACH_CHAR_NUM));
                stbtt_GetPackedQuad(getCharDatas().get(chr / EACH_CHAR_NUM), BITMAP_W, BITMAP_H,
                        chr % EACH_CHAR_NUM, getXb(), getYb(), getQ(), false);
                minY = Math.min(minY, getQ().y0());
                maxY = Math.max(maxY, getQ().y1());
            }
            maxCharHeight = maxY - minY;
        }
        return maxCharHeight;
    }

    /**
     * <p>getScale.</p>
     *
     * @param height a float.
     * @return a float.
     */
    public float getScale(float height) {
        return height / getMaxCharHeight();
    }

    /**
     * <p>drawTextGivenHeightLeftTop.</p>
     *
     * @param x1             a float.
     * @param y1             a float.
     * @param height         a float.
     * @param characterSpace a float.
     * @param color          a {@link org.joml.Vector4f} object.
     * @param text           a {@link java.lang.String} object.
     */
    public void drawTextGivenHeightLeftTop(float x1, float y1, float height,
                                           float characterSpace,
                                           Vector4f color, String text) {
        this.drawTextGivenHeightLeftTop(x1, y1, -1, -1, height,
                characterSpace, color, text);
    }

    /**
     * <p>drawTextGivenHeightLeftTop.</p>
     *
     * @param x1             a float.
     * @param y1             a float.
     * @param xMax           a float.
     * @param yMax           a float.
     * @param height         a float.
     * @param characterSpace a float.
     * @param color          a {@link org.joml.Vector4f} object.
     * @param text           a {@link java.lang.String} object.
     */
    public void drawTextGivenHeightLeftTop(float x1, float y1, float xMax,
                                           float yMax, float height,
                                           float characterSpace,
                                           Vector4f color, String text) {

        this.bind();
        float scaleY = this.getScale(height);
        float scaleX = scaleY;

//        drawText(x1, y1, scaleX, scaleY, characterSpace, color, text);

        float x = x1;
        float y = y1;

        getXb().put(0, x);
        getYb().put(0, y);

//        charData.position(font * MAX_NUM);
        glEnable(GL_TEXTURE_2D);

        if (color != null) {
            glColor4f(color.x, color.y, color.z, color.w);
        }

        float lastXReal = x;
        float lastYReal = y;
        float lastXShould = x;
        float lastYShould = y;
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) < 32) {
                continue;
            }
            glBindTexture(GL_TEXTURE_2D, getFontTextures().getPrimitive(text.charAt(i) / EACH_CHAR_NUM));
            glBegin(GL_QUADS);
            stbtt_GetPackedQuad(getCharDatas().get(text.charAt(i) / EACH_CHAR_NUM), BITMAP_W, BITMAP_H,
                    text.charAt(i) % EACH_CHAR_NUM, getXb(), getYb(), getQ(), false);
//            LOGGER.debug("x0:" + q.x0() + " x1:" + q.x1() + " y0:" +
//            q.y0() + " y1:" + q.y1());
            float charWidthShould = getQ().x1() - getQ().x0();
            float charHeightShould = getQ().y1() - getQ().y0();
            float spaceLeftToCharShould = getQ().x0() - lastXShould;
            float spaceUpToCharShould = getQ().y0() - lastYShould;
            float nowX0 = lastXReal + spaceLeftToCharShould * scaleX;
            float nowY0 = y + spaceUpToCharShould * scaleY;

            if (xMax > 0 && nowX0 > xMax) {
                break;
            }
            if (yMax > 0 && nowY0 > yMax) {
                break;
            }
//            LOGGER.debug(charWidthShould + " " + charHeightShould + "
//            " + spaceLeftToCharShould + " " + spaceUpToCharShould + " " +
//            nowX0 + " " + nowY0);

            drawBoxTC(
                    nowX0, nowY0 + height * 0.8f,
                    nowX0 + charWidthShould * scaleX,
                    nowY0 + charHeightShould * scaleY + height * 0.8f,
                    getQ().s0(), getQ().t0(), getQ().s1(), getQ().t1()
            );

//            drawBoxTC(
//                    q.x0(), q.y0(), q.x1(), q.y1(),
//                    q.s0(), q.t0(), q.s1(), q.t1()
//            );
            lastXReal = nowX0 + charWidthShould * scaleX;
            lastYReal = y;
            lastXShould = getQ().x1();
            lastYShould = y;
            glEnd();
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void forceClose() {
        for (STBTTPackedchar.Buffer au : this.getCharDatas()) {
            au.close();
        }
        MemoryUtil.memFree(getXb());
        MemoryUtil.memFree(getYb());
        MemoryUtil.memFree(getXb());
        getQ().free();

        IntIterator it = this.getFontTextures().iterator();
        while (it.hasNext()) {
            glDeleteTextures(it.next());
        }
        this.getFontTextures().clear();
    }

//    /**
//     * <p>Getter for the field <code>fontTexture</code>.</p>
//     *
//     * @return a int.
//     */
//    public int getFontTexture() {
//        return fontTexture;
//    }

    /**
     * <p>Getter for the field <code>defaultFont</code>.</p>
     *
     * @return return
     */
    public static Font getDefaultFont() {
        return defaultFont;
    }

    /**
     * <p>Setter for the field <code>defaultFont</code>.</p>
     *
     * @param defaultFont defaultFont
     */
    public static void setDefaultFont(Font defaultFont) {
        asFinalFieldSet(Font.class, "defaultFont", defaultFont);
    }


    /**
     * <p>Getter for the field <code>currentFont</code>.</p>
     *
     * @return return
     */
    public static synchronized Font getCurrentFont() {
        return currentFont;
    }

    /**
     * <p>Setter for the field <code>currentFont</code>.</p>
     *
     * @param currentFont currentFont
     */
    public static synchronized void setCurrentFont(Font currentFont) {
        Font.currentFont = currentFont;
    }

    /**
     * <p>Getter for the field <code>gameWindow</code>.</p>
     *
     * @return return
     */
    public GameWindow getGameWindow() {
        return gameWindow;
    }

    /**
     * <p>Setter for the field <code>gameWindow</code>.</p>
     *
     * @param gameWindow gameWindow
     */
    public void setGameWindow(GameWindow gameWindow) {
        this.gameWindow = gameWindow;
    }

    /**
     * <p>Setter for the field <code>maxCharHeight</code>.</p>
     *
     * @param maxCharHeight a float.
     */
    public void setMaxCharHeight(float maxCharHeight) {
        this.maxCharHeight = maxCharHeight;
    }

    /**
     * <p>Getter for the field <code>q</code>.</p>
     *
     * @return return
     */
    public STBTTAlignedQuad getQ() {
        return q;
    }

    /**
     * <p>Getter for the field <code>xb</code>.</p>
     *
     * @return return
     */
    public FloatBuffer getXb() {
        return xb;
    }

    /**
     * <p>Getter for the field <code>yb</code>.</p>
     *
     * @return return
     */
    public FloatBuffer getYb() {
        return yb;
    }

//    /**
//     * <p>Setter for the field <code>fontTexture</code>.</p>
//     *
//     * @param fontTexture a int.
//     */
//    public void setFontTexture(int fontTexture) {
//        this.fontTexture = fontTexture;
//    }

    public IntArrayList getFontTextures() {
        return fontTextures;
    }

    public List<STBTTPackedchar.Buffer> getCharDatas() {
        return charDatas;
    }
}
