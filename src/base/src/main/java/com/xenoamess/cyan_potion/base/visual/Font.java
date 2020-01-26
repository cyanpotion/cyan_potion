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
    private static Font defaultFont;

    private static Font currentFont;


    private GameWindow gameWindow;

    public static class DrawTextStruct {
        private Font font = Font.currentFont;
        private float leftTopPosX = -1;
        private float leftTopPosY = -1;
        private float centerPosX = -1;
        private float centerPosY = -1;
        private float width = -1;
        private float height = -1;
        private float scaleX = -1;
        private float scaleY = -1;
        private float characterSpace = 0;
        private Vector4f color = new Vector4f(1, 1, 1, 1);
        private String text = "";

        public DrawTextStruct() {

        }

        public DrawTextStruct(DrawTextStruct drawTextStruct) {
            this.font = drawTextStruct.font;
            this.leftTopPosX = drawTextStruct.leftTopPosX;
            this.leftTopPosY = drawTextStruct.leftTopPosY;
            this.centerPosX = drawTextStruct.centerPosX;
            this.centerPosY = drawTextStruct.centerPosY;
            this.width = drawTextStruct.width;
            this.height = drawTextStruct.height;
            this.scaleX = drawTextStruct.scaleX;
            this.scaleY = drawTextStruct.scaleY;
            this.characterSpace = drawTextStruct.characterSpace;
            if (drawTextStruct.color != null) {
                this.color.set(drawTextStruct.color);
            } else {
                this.color = null;
            }
            this.text = drawTextStruct.text;
        }

        public void setLeftTopPosXY(float leftTopPosX, float leftTopPosY) {
            this.setLeftTopPosX(leftTopPosX);
            this.setLeftTopPosY(leftTopPosY);
        }

        public void setCenterPosXY(float centerPosX, float centerPosY) {
            this.setCenterPosX(centerPosX);
            this.setCenterPosY(centerPosY);
        }

        public void setWidthHeight(float width, float height) {
            this.setWidth(width);
            this.setHeight(height);
        }

        public void setScaleXY(float scaleX, float scaleY) {
            this.setScaleX(scaleX);
            this.setScaleY(scaleY);
        }

//        public void bakeScaleXY() {
//            if (getScaleX() < 0) {
//                setScaleX(getScaleY());
//            } else if (getScaleY() < 0) {
//                setScaleY(getScaleX());
//            }
//        }

        public void bakePosXY() {
            if (getLeftTopPosX() >= 0 && getLeftTopPosY() >= 0) {
                setCenterPosX(getLeftTopPosX() + getWidth() / 2f);
                setCenterPosY(getLeftTopPosY() + getHeight() / 2f);
            } else if (getCenterPosX() >= 0 && getCenterPosY() >= 0) {
                setLeftTopPosX(getCenterPosX() - getWidth() / 2f);
                setLeftTopPosY(getCenterPosY() - getHeight() / 2f);
            } else {
                throw new RuntimeException("all pos <0");
            }
        }

        public void calculateScaleXYFromWidthHeight() {
            assert (this.width >= 0 || this.height >= 0);
            assert (this.text != null);

            this.getFont().bind();
            font.getXb().put(0, 0);
            font.getYb().put(0, 0);

            glEnable(GL_TEXTURE_2D);

            float lastXReal = 0;
            float lastYReal = 0;
            float lastXShould = 0;
            float lastYShould = 0;

            float x3 = Float.MIN_VALUE;
            float y3 = Float.MIN_VALUE;

            for (int i = 0; i < this.text.length(); i++) {
                if (this.text.charAt(i) < 32) {
                    continue;
                }
                glBindTexture(GL_TEXTURE_2D, font.getFontTextures().getPrimitive(this.text.charAt(i) / EACH_CHAR_NUM));
                glBegin(GL_QUADS);
                stbtt_GetPackedQuad(font.getCharDatas().get(this.text.charAt(i) / EACH_CHAR_NUM), BITMAP_W, BITMAP_H,
                        this.text.charAt(i) % EACH_CHAR_NUM, font.getXb(), font.getYb(), font.getQ(), false);
//            LOGGER.debug("x0:" + q.x0() + " x1:" + q.x1() + " y0:" +
//            q.y0() + " y1:" + q.y1());
                float charWidthShould = font.getQ().x1() - font.getQ().x0();
                float charHeightShould = font.getQ().y1() - font.getQ().y0();
                float spaceLeftToCharShould = font.getQ().x0() - lastXShould;
                float spaceUpToCharShould = font.getQ().y0() - lastYShould;
                float nowX0 = lastXReal + spaceLeftToCharShould;
                float nowY0 = 0;
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
                lastYReal = 0;
                lastXShould = font.getQ().x1();
                lastYShould = 0;
                glEnd();
            }
            float scaleX = this.width < 0 ? -1 : this.width / (x3 - 0);
            float scaleY = this.height < 0 ? -1 : this.height / (y3 - 0);
            if (scaleX < 0) {
                scaleX = scaleY;
            } else if (scaleY < 0) {
                scaleY = scaleX;
            }
            this.setScaleXY(scaleX, scaleY);
            if (this.height < 0) {
                this.height = this.width / x3 * y3;
            }
        }

        public void bake() {
            if (this.scaleX >= 0 && this.scaleY >= 0 && this.height >= 0) {
                //do nothing
            } else if (this.scaleX >= 0 && this.scaleY >= 0) {
                DrawTextStruct drawTextStruct = new DrawTextStruct(this);
                drawTextStruct.color = new Vector4f(0, 0, 0, 0);
                drawTextStruct.leftTopPosX = 0;
                drawTextStruct.leftTopPosY = 0;
                font.drawTextLeftTop(drawTextStruct);
                this.width = drawTextStruct.width;
                this.height = drawTextStruct.height;
            } else if (this.width >= 0 || this.height >= 0) {
                this.calculateScaleXYFromWidthHeight();
            }
//            } else if (this.width >= 0 && this.height >= 0) {
//                this.calculateScaleXYFromWidthHeight();
//            } else if (this.height >= 0) {
//                float scaleXY = font.getScale(this.height);
//                this.setScaleXY(scaleXY, scaleXY);
//                DrawTextStruct drawTextStruct = new DrawTextStruct(this);
//                drawTextStruct.color = new Vector4f(0, 0, 0, 0);
//                drawTextStruct.leftTopPosX = 0;
//                drawTextStruct.leftTopPosY = 0;
//                font.drawTextLeftTop(drawTextStruct);
//                this.width = drawTextStruct.width;
//            } else {
//                this.calculateScaleXYFromWidthHeight();
//            }


            this.bakePosXY();
        }

        public void draw() {
            if (leftTopPosX < 0 || leftTopPosY < 0 || centerPosX < 0 || centerPosY < 0 || scaleX < 0 || scaleY < 0 || characterSpace < 0 || text == null || height < 0) {
                this.bake();
                if (leftTopPosX < 0 || leftTopPosY < 0 || centerPosX < 0 || centerPosY < 0 || scaleX < 0 || scaleY < 0 || characterSpace < 0 || text == null || height < 0) {
                    throw new RuntimeException("font still cannot draw after bake");
                }
            }
            this.font.drawTextLeftTop(this);
        }

        public float getLeftTopPosX() {
            return leftTopPosX;
        }

        public void setLeftTopPosX(float leftTopPosX) {
            this.leftTopPosX = leftTopPosX;
        }

        public float getLeftTopPosY() {
            return leftTopPosY;
        }

        public void setLeftTopPosY(float leftTopPosY) {
            this.leftTopPosY = leftTopPosY;
        }

        public float getCenterPosX() {
            return centerPosX;
        }

        public void setCenterPosX(float centerPosX) {
            this.centerPosX = centerPosX;
        }

        public float getCenterPosY() {
            return centerPosY;
        }

        public void setCenterPosY(float centerPosY) {
            this.centerPosY = centerPosY;
        }

        public float getWidth() {
            return width;
        }

        public void setWidth(float width) {
            this.width = width;
        }

        public float getHeight() {
            return height;
        }

        public void setHeight(float height) {
            this.height = height;
        }

        public float getScaleX() {
            return scaleX;
        }

        public void setScaleX(float scaleX) {
            this.scaleX = scaleX;
        }

        public float getScaleY() {
            return scaleY;
        }

        public void setScaleY(float scaleY) {
            this.scaleY = scaleY;
        }

        public Vector4f getColor() {
            return color;
        }

        public void setColor(Vector4f color) {
            this.color = color;
        }

        public float getCharacterSpace() {
            return characterSpace;
        }

        public void setCharacterSpace(float characterSpace) {
            this.characterSpace = characterSpace;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public Font getFont() {
            return font;
        }

        public void setFont(Font font) {
            if (font == null) {
                font = Font.currentFont;
            }
            this.font = font;
        }

    }

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
    private final List<ByteBuffer> bitmaps = new ArrayList<>(PIC_NUM);

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
     * @param leftTopPosX    a float.
     * @param leftTopPosY    a float.
     * @param scaleX         a float.
     * @param scaleY         a float.
     * @param characterSpace a float.
     * @param color          a {@link org.joml.Vector4f} object.
     * @param text           a {@link java.lang.String} object.
     */
    public DrawTextStruct drawTextLeftTop(
            float leftTopPosX,
            float leftTopPosY,
            float scaleX,
            float scaleY,
            float characterSpace,
            Vector4f color,
            String text
    ) {
        DrawTextStruct drawTextStruct = new DrawTextStruct();
        drawTextStruct.setLeftTopPosXY(leftTopPosX, leftTopPosY);
        drawTextStruct.setScaleXY(scaleX, scaleY);
        drawTextStruct.setCharacterSpace(characterSpace);
        drawTextStruct.setColor(color);
        drawTextStruct.setText(text);
        drawTextStruct.draw();
        return drawTextStruct;
    }

    /**
     * <p>drawText.</p>
     *
     * @param drawTextStruct drawStruct.
     * @see Font#drawTextLeftTop(float, float, float, float, float, Vector4f, String)
     */
    public DrawTextStruct drawTextLeftTop(DrawTextStruct drawTextStruct) {
        this.bind();

        getXb().put(0, drawTextStruct.leftTopPosX);
        getYb().put(0, drawTextStruct.leftTopPosY);

        glEnable(GL_TEXTURE_2D);

        if (drawTextStruct.color != null) {
            glColor4f(drawTextStruct.color.x, drawTextStruct.color.y, drawTextStruct.color.z, drawTextStruct.color.w);
        }

        float lastXReal = drawTextStruct.leftTopPosX;
        float lastYReal = drawTextStruct.leftTopPosY;
        float lastXShould = drawTextStruct.leftTopPosX;
        float lastYShould = drawTextStruct.leftTopPosY;
        for (int i = 0; i < drawTextStruct.text.length(); i++) {
            if (drawTextStruct.text.charAt(i) < 32) {
                continue;
            }
            glBindTexture(GL_TEXTURE_2D, getFontTextures().getPrimitive(drawTextStruct.text.charAt(i) / EACH_CHAR_NUM));
            glBegin(GL_QUADS);
            stbtt_GetPackedQuad(getCharDatas().get(drawTextStruct.text.charAt(i) / EACH_CHAR_NUM), BITMAP_W, BITMAP_H,
                    drawTextStruct.text.charAt(i) % EACH_CHAR_NUM, getXb(), getYb(), getQ(), false);
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
            float nowX0 = lastXReal + spaceLeftToCharShould * drawTextStruct.scaleX;
            float nowY0 = lastYReal + spaceUpToCharShould * drawTextStruct.scaleY;
//            float nowY0 = y;
//            LOGGER.debug(charWidthShould + " " + charHeightShould + "
//            " + spaceLeftToCharShould + " " + spaceUpToCharShould + " " +
//            nowX0 + " " + nowY0);

            drawBoxTC(
                    nowX0, nowY0 + drawTextStruct.height * 0.8f,
                    nowX0 + charWidthShould * drawTextStruct.scaleX,
                    nowY0 + charHeightShould * drawTextStruct.scaleY + drawTextStruct.height * 0.8f,
//                    q.x0(), q.y0(), q.x1(), q.y1(),
                    getQ().s0(), getQ().t0(), getQ().s1(), getQ().t1()
            );
            lastXReal = nowX0 + charWidthShould * drawTextStruct.scaleX;
            lastYReal = drawTextStruct.leftTopPosY;
            lastXShould = getQ().x1() + drawTextStruct.characterSpace;
            lastYShould = drawTextStruct.leftTopPosY;
            glEnd();
        }
        drawTextStruct.setWidth(lastXReal - drawTextStruct.leftTopPosX);
        drawTextStruct.bake();
        return drawTextStruct;
    }

    /**
     * <p>drawTextFillAreaLeftTop.</p>
     *
     * @param leftTopPosX    a float.
     * @param leftTopPosY    a float.
     * @param width          a float.
     * @param height         a float.
     * @param characterSpace a float.
     * @param color          a {@link org.joml.Vector4f} object.
     * @param text           a {@link java.lang.String} object.
     */
    public DrawTextStruct drawTextFillAreaLeftTop(
            float leftTopPosX,
            float leftTopPosY,
            float width,
            float height,
            float characterSpace,
            Vector4f color,
            String text
    ) {
        DrawTextStruct drawTextStruct = new DrawTextStruct();
        drawTextStruct.setLeftTopPosXY(leftTopPosX, leftTopPosY);
        drawTextStruct.setWidthHeight(width, height);
        drawTextStruct.setCharacterSpace(characterSpace);
        drawTextStruct.setColor(color);
        drawTextStruct.setText(text);
        drawTextStruct.draw();
        return drawTextStruct;
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

//    /**
//     * <p>drawTextGivenHeightLeftTop.</p>
//     *
//     * @param x1             a float.
//     * @param y1             a float.
//     * @param height         a float.
//     * @param characterSpace a float.
//     * @param color          a {@link org.joml.Vector4f} object.
//     * @param text           a {@link java.lang.String} object.
//     */
//    public void drawTextGivenHeightLeftTop(float x1, float y1, float height,
//                                           float characterSpace,
//                                           Vector4f color, String text) {
//        this.drawTextGivenHeightLeftTop(x1, y1, -1, -1, height,
//                characterSpace, color, text);
//    }

//    /**
//     * <p>drawTextGivenHeightLeftTop.</p>
//     *
//     * @param x1             a float.
//     * @param y1             a float.
//     * @param xMax           a float.
//     * @param yMax           a float.
//     * @param height         a float.
//     * @param characterSpace a float.
//     * @param color          a {@link org.joml.Vector4f} object.
//     * @param text           a {@link java.lang.String} object.
//     */
//    public void drawTextGivenHeightLeftTop(float x1, float y1, float xMax,
//                                           float yMax, float height,
//                                           float characterSpace,
//                                           Vector4f color, String text) {
//
//        this.bind();
//        float scaleY = this.getScale(height);
//        float scaleX = scaleY;
//
////        drawText(x1, y1, scaleX, scaleY, characterSpace, color, text);
//
//        float x = x1;
//        float y = y1;
//
//        getXb().put(0, x);
//        getYb().put(0, y);
//
////        charData.position(font * MAX_NUM);
//        glEnable(GL_TEXTURE_2D);
//
//        if (color != null) {
//            glColor4f(color.x, color.y, color.z, color.w);
//        }
//
//        float lastXReal = x;
//        float lastYReal = y;
//        float lastXShould = x;
//        float lastYShould = y;
//        for (int i = 0; i < text.length(); i++) {
//            if (text.charAt(i) < 32) {
//                continue;
//            }
//            glBindTexture(GL_TEXTURE_2D, getFontTextures().getPrimitive(text.charAt(i) / EACH_CHAR_NUM));
//            glBegin(GL_QUADS);
//            stbtt_GetPackedQuad(getCharDatas().get(text.charAt(i) / EACH_CHAR_NUM), BITMAP_W, BITMAP_H,
//                    text.charAt(i) % EACH_CHAR_NUM, getXb(), getYb(), getQ(), false);
////            LOGGER.debug("x0:" + q.x0() + " x1:" + q.x1() + " y0:" +
////            q.y0() + " y1:" + q.y1());
//            float charWidthShould = getQ().x1() - getQ().x0();
//            float charHeightShould = getQ().y1() - getQ().y0();
//            float spaceLeftToCharShould = getQ().x0() - lastXShould;
//            float spaceUpToCharShould = getQ().y0() - lastYShould;
//            float nowX0 = lastXReal + spaceLeftToCharShould * scaleX;
//            float nowY0 = y + spaceUpToCharShould * scaleY;
//
//            if (xMax > 0 && nowX0 > xMax) {
//                break;
//            }
//            if (yMax > 0 && nowY0 > yMax) {
//                break;
//            }
////            LOGGER.debug(charWidthShould + " " + charHeightShould + "
////            " + spaceLeftToCharShould + " " + spaceUpToCharShould + " " +
////            nowX0 + " " + nowY0);
//
//            drawBoxTC(
//                    nowX0, nowY0 + height * 0.8f,
//                    nowX0 + charWidthShould * scaleX,
//                    nowY0 + charHeightShould * scaleY + height * 0.8f,
//                    getQ().s0(), getQ().t0(), getQ().s1(), getQ().t1()
//            );
//
////            drawBoxTC(
////                    q.x0(), q.y0(), q.x1(), q.y1(),
////                    q.s0(), q.t0(), q.s1(), q.t1()
////            );
//            lastXReal = nowX0 + charWidthShould * scaleX;
//            lastYReal = y;
//            lastXShould = getQ().x1();
//            lastYShould = y;
//            glEnd();
//        }
//
//    }

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
