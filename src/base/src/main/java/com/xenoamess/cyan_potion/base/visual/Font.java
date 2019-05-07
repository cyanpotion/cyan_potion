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

import com.xenoamess.cyan_potion.base.GameWindow;
import com.xenoamess.cyan_potion.base.io.FileUtil;
import com.xenoamess.cyan_potion.base.render.Shader;
import org.joml.Vector4f;
import org.lwjgl.stb.STBTTAlignedQuad;
import org.lwjgl.stb.STBTTPackContext;
import org.lwjgl.stb.STBTTPackedchar;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.stb.STBImageWrite.stbi_write_bmp;
import static org.lwjgl.stb.STBTruetype.*;

/**
 * @author XenoAmess
 */
public class Font implements AutoCloseable {
    /**
     * the default DEFAULT_FONT_FILE_PATH if you does not set it from setting
     * file.
     *
     * @see com.xenoamess.cyan_potion.base.GameManager
     */
    public static final String DEFAULT_DEFAULT_FONT_FILE_PATH = "/www/fonts" +
            "/SourceHanSans-Normal.ttc";
    public static final boolean TEST_PRINT_FONT_BMP = false;

    public static final int MAX_NUM = 40960;
    public static final int MAX_SIZE = 16 * 1024;
    public static final int BITMAP_W = MAX_SIZE;
    public static final int BITMAP_H = MAX_SIZE;
    public static final float SCALE = 36.0f;

    private String ttfFilePath = null;
    private int fontTexture;
    private STBTTPackedchar.Buffer chardata = null;

    private static final Font DEFAULT_FONT = new Font();
    private static Font CurrentFont = DEFAULT_FONT;


    private GameWindow gameWindow;

    public Font() {

    }

    public Font(String ttfFilePath) {
        this.setTtfFilePath(ttfFilePath);
    }

    /**
     * this buffer will be freed after init(),
     * so please does never use it after that.
     */
    private ByteBuffer bitmap;

    public void loadBitmap() {
        ByteBuffer bitmap;
        STBTTPackedchar.Buffer tmpChardata =
                STBTTPackedchar.malloc(6 * MAX_NUM);
        try (STBTTPackContext pc = STBTTPackContext.malloc()) {
            ByteBuffer ttf =
                    FileUtil.loadFileBuffer(FileUtil.getFile(this.getTtfFilePath()), true);
            bitmap = MemoryUtil.memAlloc(BITMAP_W * BITMAP_H);
            stbtt_PackBegin(pc, bitmap, BITMAP_W, BITMAP_H, 0, 1, 0);
            int p = 32;
            tmpChardata.position(p);
            stbtt_PackSetOversampling(pc, 1, 1);
            stbtt_PackFontRange(pc, ttf, 0, SCALE, 32, tmpChardata);
            tmpChardata.clear();
            stbtt_PackEnd(pc);
            if (TEST_PRINT_FONT_BMP) {
                stbi_write_bmp("font_texture.bmp", BITMAP_W, BITMAP_H, 1,
                        bitmap);
            }

            this.bitmap = bitmap;
            this.setChardata(tmpChardata);
        }
    }


    public void init(GameWindow gameWindow) {
        this.setGameWindow(gameWindow);
        this.setFontTexture(glGenTextures());
        glBindTexture(GL_TEXTURE_2D, getFontTexture());
        glTexImage2D(GL_TEXTURE_2D, 0, GL_ALPHA, BITMAP_W, BITMAP_H, 0,
                GL_ALPHA, GL_UNSIGNED_BYTE, this.bitmap);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        MemoryUtil.memFree(this.bitmap);
    }

    public void bind() {
        Shader.unbind();
        gameWindow.bindGlViewportToFullWindow();
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        glOrtho(0.0, getGameWindow().getRealWindowWidth(),
                getGameWindow().getRealWindowHeight(), 0.0, -1.0, 1.0);
        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();
    }


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

//    public void drawText(float x, float y, float scalex, float scaley,
//    float characterSpace, Vector4f color, String text) {
////        System.out.println("!!! x:" + x + " y:" + y);
//        this.bind();
//
//        xb.put(0, x);
//        yb.put(0, y);
//
////        chardata.position(font * MAX_NUM);
//        chardata.position(0 * MAX_NUM);
//        glEnable(GL_TEXTURE_2D);
//        glBindTexture(GL_TEXTURE_2D, fontTexture);
//
//        if (color != null) {
//            glColor4f(color.x, color.y, color.z, color.w);
//        }
//
//        glBegin(GL_QUADS);
//        float lastx_ = x;
//        float lasty_ = y;
//        float lastx_Should = x;
//        float lasty_Should = y;
//        for (int i = 0; i < text.length(); i++) {
//            stbtt_GetPackedQuad(chardata, BITMAP_W, BITMAP_H, text.charAt
//            (i), xb, yb, q, false);
////            System.out.println("x0:" + q.x0() + " x1:" + q.x1() + " y0:"
// + q.y0() + " y1:" + q.y1());
//            float charWidthShould = q.x1() - q.x0();
//            float charHeightShould = q.y1() - q.y0();
//            float spaceLeftToCharShould = q.x0() - lastx_Should;
//            float spaceUpToCharShould = q.y0() - lasty_Should;
//            float nowx0 = lastx_ + spaceLeftToCharShould * scalex;
////            float nowy0 = lasty_ + spaceUpToCharShould * scaley;
//            float nowy0 = y;
////            System.out.println(charWidthShould + " " + charHeightShould +
// " " + spaceLeftToCharShould + " " + spaceUpToCharShould + " " + nowx0 + "
// " + nowy0);
//            drawBoxTC(
//                    nowx0, nowy0, nowx0 + charWidthShould * scalex, nowy0 +
//                    charHeightShould * scaley,
////                    q.x0(), q.y0(), q.x1(), q.y1(),
//                    q.s0(), q.t0(), q.s1(), q.t1()
//            );
//            lastx_ = nowx0 + charWidthShould * scalex;
//            lasty_ = y;
//            lastx_Should = q.x1();
//            lasty_Should = y;
//        }
//        glEnd();
//    }

    public void drawText(float x, float y, float scalex, float scaley,
                         float height, float characterSpace, Vector4f color,
                         String text) {
//        System.out.println("!!! x:" + x + " y:" + y);
//        STBTTFontinfo fontInfo = STBTTFontinfo.create();
        this.bind();

        getXb().put(0, x);
        getYb().put(0, y);

//        chardata.position(font * MAX_NUM);
        getChardata().position(0 * MAX_NUM);
        glEnable(GL_TEXTURE_2D);
        glBindTexture(GL_TEXTURE_2D, getFontTexture());

        if (color != null) {
            glColor4f(color.x, color.y, color.z, color.w);
        }

        glBegin(GL_QUADS);
        float lastxReal = x;
        float lastyReal = y;
        float lastxShould = x;
        float lastyShould = y;
        for (int i = 0; i < text.length(); i++) {
            stbtt_GetPackedQuad(getChardata(), BITMAP_W, BITMAP_H,
                    text.charAt(i), getXb(), getYb(), getQ(), false);
//            System.out.println("x0:" + q.x0() + " x1:" + q.x1() + " y0:" +
//            q.y0() + " y1:" + q.y1());
//            System.out.println("s0:" + q.s0() + " s1:" + q.s1() + " t0:" +
//            q.t0() + " t1:" + q.t1());
            float charWidthShould = getQ().x1() - getQ().x0();
            float charHeightShould = getQ().y1() - getQ().y0();
            float spaceLeftToCharShould = getQ().x0() - lastxShould;
            float spaceUpToCharShould = getQ().y0() - lastyShould;
            float nowx0 = lastxReal + spaceLeftToCharShould * scalex;
            float nowy0 = lastyReal + spaceUpToCharShould * scaley;
//            float nowy0 = y;
//            System.out.println(charWidthShould + " " + charHeightShould + "
//            " + spaceLeftToCharShould + " " + spaceUpToCharShould + " " +
//            nowx0 + " " + nowy0);

            drawBoxTC(
                    nowx0, nowy0 + height * 0.8f,
                    nowx0 + charWidthShould * scalex,
                    nowy0 + charHeightShould * scaley + height * 0.8f,
//                    q.x0(), q.y0(), q.x1(), q.y1(),
                    getQ().s0(), getQ().t0(), getQ().s1(), getQ().t1()
            );
            lastxReal = nowx0 + charWidthShould * scalex;
            lastyReal = y;
            lastxShould = getQ().x1();
            lastyShould = y;
        }
        glEnd();
    }

    public void drawTextFillAreaLeftTop(float x1, float y1, float width,
                                        float height, float characterSpace,
                                        Vector4f color, String text) {
//        System.out.println("!!! x:" + x + " y:" + y);
        this.bind();
        getXb().put(0, x1);
        getYb().put(0, y1);

//        chardata.position(font * MAX_NUM);
        getChardata().position(0 * MAX_NUM);
        glEnable(GL_TEXTURE_2D);
        glBindTexture(GL_TEXTURE_2D, getFontTexture());

        if (color != null) {
            glColor4f(color.x, color.y, color.z, color.w);
        }

        glBegin(GL_QUADS);
        float lastxReal = x1;
        float lastyReal = y1;
        float lastxShould = x1;
        float lastyShould = y1;

        float x3 = Float.MIN_VALUE;
        float y3 = Float.MIN_VALUE;

        for (int i = 0; i < text.length(); i++) {
            stbtt_GetPackedQuad(getChardata(), BITMAP_W, BITMAP_H,
                    text.charAt(i), getXb(), getYb(), getQ(), false);
//            System.out.println("x0:" + q.x0() + " x1:" + q.x1() + " y0:" +
//            q.y0() + " y1:" + q.y1());
            float charWidthShould = getQ().x1() - getQ().x0();
            float charHeightShould = getQ().y1() - getQ().y0();
            float spaceLeftToCharShould = getQ().x0() - lastxShould;
            float spaceUpToCharShould = getQ().y0() - lastyShould;
            float nowx0 = lastxReal + spaceLeftToCharShould;
            float nowy0 = y1;
//            System.out.println(charWidthShould + " " + charHeightShould + "
//            " + spaceLeftToCharShould + " " + spaceUpToCharShould + " " +
//            nowx0 + " " + nowy0);
//            drawBoxTC(
//                    nowx0, nowy0, nowx0 + charWidthShould * 1, nowy0 +
//                    charHeightShould * 1,
//                    q.s0(), q.t0(), q.s1(), q.t1()
//            );
            x3 = Math.max(x3, nowx0 + charWidthShould);
            y3 = Math.max(y3, nowy0 + charHeightShould);
            lastxReal = nowx0 + charWidthShould * 1;
            lastyReal = y1;
            lastxShould = getQ().x1();
            lastyShould = y1;
        }
        glEnd();
        float scalex = width / (x3 - x1);
        float scaley = height / (y3 - y1);
        if (width < 0) {
            scalex = scaley;
        } else if (height < 0) {
            scaley = scalex;
        }
        drawText(x1, y1, scalex, scaley, height, characterSpace, color, text);
    }

    private float maxCharHeight = -1;

    public float getMaxCharHeight() {
        if (maxCharHeight == -1) {
            this.bind();
            getXb().put(0, 0);
            getYb().put(0, 0);
            getChardata().position(0 * MAX_NUM);
            glEnable(GL_TEXTURE_2D);
            glBindTexture(GL_TEXTURE_2D, getFontTexture());

//            glBegin(GL_QUADS);

            float x3 = Float.MIN_VALUE;
            float y3 = Float.MIN_VALUE;
            String text =
                    "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
            float ymin = Float.MAX_VALUE;
            float ymax = Float.MIN_VALUE;

            for (int i = 0; i < text.length(); i++) {
                stbtt_GetPackedQuad(getChardata(), BITMAP_W, BITMAP_H,
                        text.charAt(i), getXb(), getYb(), getQ(), false);
                ymin = Math.min(ymin, getQ().y0());
                ymax = Math.max(ymax, getQ().y1());
            }
            maxCharHeight = ymax - ymin;
        }
        return maxCharHeight;
    }

    public float getScale(float height) {
        return height / getMaxCharHeight();
    }

    public void drawTextGivenHeightLeftTop(float x1, float y1, float height,
                                           float characterSpace,
                                           Vector4f color, String text) {
        this.drawTextGivenHeightLeftTop(x1, y1, -1, -1, height,
                characterSpace, color, text);
    }

    public void drawTextGivenHeightLeftTop(float x1, float y1, float xMax,
                                           float yMax, float height,
                                           float characterSpace,
                                           Vector4f color, String text) {
//        System.out.println("!!! x:" + x + " y:" + y);
        this.bind();
        float scaley = this.getScale(height);
        float scalex = scaley;

//        drawText(x1, y1, scalex, scaley, characterSpace, color, text);

        float x = x1;
        float y = y1;
        //        System.out.println("!!! x:" + x + " y:" + y);
        getXb().put(0, x);
        getYb().put(0, y);

//        chardata.position(font * MAX_NUM);
        getChardata().position(0 * MAX_NUM);
        glEnable(GL_TEXTURE_2D);
        glBindTexture(GL_TEXTURE_2D, getFontTexture());

        if (color != null) {
            glColor4f(color.x, color.y, color.z, color.w);
        }

        glBegin(GL_QUADS);
        float lastxReal = x;
        float lastyReal = y;
        float lastxShould = x;
        float lastyShould = y;
        for (int i = 0; i < text.length(); i++) {
            stbtt_GetPackedQuad(getChardata(), BITMAP_W, BITMAP_H,
                    text.charAt(i), getXb(), getYb(), getQ(), false);
//            System.out.println("x0:" + q.x0() + " x1:" + q.x1() + " y0:" +
//            q.y0() + " y1:" + q.y1());
            float charWidthShould = getQ().x1() - getQ().x0();
            float charHeightShould = getQ().y1() - getQ().y0();
            float spaceLeftToCharShould = getQ().x0() - lastxShould;
            float spaceUpToCharShould = getQ().y0() - lastyShould;
            float nowx0 = lastxReal + spaceLeftToCharShould * scalex;
            float nowy0 = y + spaceUpToCharShould * scaley;

            if (xMax > 0 && nowx0 > xMax) {
                break;
            }
            if (yMax > 0 && nowy0 > yMax) {
                break;
            }
//            System.out.println(charWidthShould + " " + charHeightShould + "
//            " + spaceLeftToCharShould + " " + spaceUpToCharShould + " " +
//            nowx0 + " " + nowy0);

            drawBoxTC(
                    nowx0, nowy0 + height * 0.8f,
                    nowx0 + charWidthShould * scalex,
                    nowy0 + charHeightShould * scaley + height * 0.8f,
                    getQ().s0(), getQ().t0(), getQ().s1(), getQ().t1()
            );

//            drawBoxTC(
//                    q.x0(), q.y0(), q.x1(), q.y1(),
//                    q.s0(), q.t0(), q.s1(), q.t1()
//            );
            lastxReal = nowx0 + charWidthShould * scalex;
            lastyReal = y;
            lastxShould = getQ().x1();
            lastyShould = y;
        }
        glEnd();
    }

    @Override
    public void close() {
        getChardata().close();

        MemoryUtil.memFree(getYb());
        MemoryUtil.memFree(getXb());
        getQ().free();

        if (this.getFontTexture() != 0) {
            glDeleteTextures(this.getFontTexture());
            this.setFontTexture(0);
        }
    }

    public String getTtfFilePath() {
        return ttfFilePath;
    }

    public int getFontTexture() {
        return fontTexture;
    }

    public STBTTPackedchar.Buffer getChardata() {
        return chardata;
    }

    public static Font getDefaultFont() {
        return DEFAULT_FONT;
    }

    public static synchronized Font getCurrentFont() {
        return CurrentFont;
    }

    public static synchronized void setCurrentFont(Font currentFont) {
        CurrentFont = currentFont;
    }

    public GameWindow getGameWindow() {
        return gameWindow;
    }

    public void setGameWindow(GameWindow gameWindow) {
        this.gameWindow = gameWindow;
    }

    public void setMaxCharHeight(float maxCharHeight) {
        this.maxCharHeight = maxCharHeight;
    }

    public STBTTAlignedQuad getQ() {
        return q;
    }

    public FloatBuffer getXb() {
        return xb;
    }

    public FloatBuffer getYb() {
        return yb;
    }

    public void setTtfFilePath(String ttfFilePath) {
        this.ttfFilePath = ttfFilePath;
    }

    public void setFontTexture(int fontTexture) {
        this.fontTexture = fontTexture;
    }

    public void setChardata(STBTTPackedchar.Buffer chardata) {
        this.chardata = chardata;
    }

}
