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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.xenoamess.commons.as_final_field.AsFinalField;
import com.xenoamess.commons.io.FileObjectUtilsx;
import com.xenoamess.commons.main_thread_only.MainThreadOnly;
import com.xenoamess.commons.primitive.collections.lists.array_lists.IntArrayList;
import com.xenoamess.commons.primitive.iterators.IntIterator;
import com.xenoamess.cyan_potion.base.GameManager;
import com.xenoamess.cyan_potion.base.GameWindow;
import com.xenoamess.cyan_potion.base.exceptions.ResourceSizeLargerThanGlMaxTextureSizeException;
import com.xenoamess.cyan_potion.base.memory.NormalResource;
import com.xenoamess.cyan_potion.base.memory.ResourceInfo;
import com.xenoamess.cyan_potion.base.memory.ResourceManager;
import com.xenoamess.cyan_potion.base.render.Shader;
import lombok.*;
import org.apache.commons.vfs2.FileObject;
import org.joml.Vector4fc;
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
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Function;

import static com.xenoamess.commons.as_final_field.AsFinalFieldUtils.asFinalFieldSet;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.stb.STBImageWrite.stbi_write_bmp;
import static org.lwjgl.stb.STBTruetype.*;

/**
 * <p>Font class.</p>
 *
 * @author XenoAmess
 * @version 0.162.3
 */
@EqualsAndHashCode(callSuper = true)
@ToString
public class Font extends NormalResource {

    @JsonIgnore
    private static final transient Logger LOGGER =
            LoggerFactory.getLogger(Font.class);

    /**
     * Constant <code>TEST_PRINT_FONT_BMP=false</code>
     * notice that open this shall create a lot of pictures onto your disk when loading your ttf.
     * Only open it when you are debugging a new ttf file.
     */
    @Getter
    @Setter
    private static boolean testPrintFontBmp = false;

    /**
     * size of each font pic.
     */
    public static final int EACH_SIZE = 1024;
    /**
     * num of characters on each font pic.
     */
    public static final int EACH_CHAR_NUM = 1024;
    /**
     * num of characters on each font pic.
     */
    public static final int PIC_NUM = ((int) (Character.MAX_VALUE) + 1) / EACH_CHAR_NUM;
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

    @Getter
    private final IntArrayList fontTextures = new IntArrayList();

    @Getter
    private final List<STBTTPackedchar.Buffer> charDatas = new ArrayList<>();

    @Getter
    @Setter
    @AsFinalField
    private static Font defaultFont;

    @Getter(onMethod_ = {@Synchronized})
    @Setter(onMethod_ = {@Synchronized})
    private static Font currentFont;

    @Getter
    @Setter
    private GameWindow gameWindow;

    @Getter
    @Setter
    private final STBTTAlignedQuad q = STBTTAlignedQuad.malloc();

    @Getter
    @Setter
    private final FloatBuffer xb = MemoryUtil.memAllocFloat(1);

    @Getter
    @Setter
    private final FloatBuffer yb = MemoryUtil.memAllocFloat(1);

    /**
     * !!!NOTICE!!!
     * <p>
     * This class shall never build from this constructor directly.
     * You shall always use ResourceManager.fetchResource functions to get this instance.
     *
     * @param resourceManager resource Manager
     * @param resourceJson      resource Json
     * @see ResourceManager#fetchResource(Class, ResourceInfo)
     */
    public Font(ResourceManager resourceManager, ResourceInfo<Font> resourceJson) {
        super(resourceManager, resourceJson);
    }

    /**
     * Constant <code>STRING_TTF_FILE="ttfFile"</code>
     */
    public static final String STRING_TTF_FILE = "ttfFile";

    /**
     * !!!NOTICE!!!
     * This function is used by reflection and don't delete it if you don't know about the plugin mechanism here.
     */
    @SuppressWarnings({"unused", "unchecked"})
    public static final Function<GameManager, Void> PUT_FONT_LOADER_TTF_FILE = (GameManager gameManager) -> {
        gameManager.getResourceManager().putResourceLoader(Font.class, STRING_TTF_FILE,
                (Font font) -> font.loadAsTtfFileFont(font.getResourceInfo())
        );
        return null;
    };

    private boolean loadAsTtfFileFont(ResourceInfo<Font> resourceInfo) {
        return this.loadBitmap(resourceInfo.getFileObject());
    }

    /**
     * this buffer will be freed after init(),
     * so please does never use it after that.
     */
    private final List<ByteBuffer> bitmaps = new ArrayList<>(PIC_NUM);

    private static class LoadBitmapPojo {
        final int index;
        final ByteBuffer bitmapLocal;
        final STBTTPackedchar.Buffer charDataLocal;

        public LoadBitmapPojo(int index, ByteBuffer bitmapLocal, STBTTPackedchar.Buffer charDataLocal) {
            this.index = index;
            this.bitmapLocal = bitmapLocal;
            this.charDataLocal = charDataLocal;
        }
    }

    /**
     * <p>loadBitmap.</p>
     *
     * @param fileObject fileObject
     * @return a boolean.
     */
    public boolean loadBitmap(FileObject fileObject) {
        System.out.println(fileObject);
        final ByteBuffer ttf = FileObjectUtilsx.loadBuffer(fileObject, true);
        if (ttf == null) {
            throw new IllegalArgumentException("ttf buffer load failed!:"+fileObject);
        }
        this.setMemorySize(1L * PIC_NUM * BITMAP_W * BITMAP_H);
        ResourceSizeLargerThanGlMaxTextureSizeException.check(this);

        final ExecutorService executorService = Executors.newCachedThreadPool();
        final List<Callable<LoadBitmapPojo>> returnValueList = new ArrayList<>();
        for (int i = 0; i < PIC_NUM; i++) {
            final int ti = i;
            returnValueList.add(
                    new Callable<LoadBitmapPojo>() {
                        @Override
                        public LoadBitmapPojo call() {
                            try (STBTTPackContext pc = STBTTPackContext.malloc()) {
                                ByteBuffer bitmapLocal = MemoryUtil.memAlloc(BITMAP_W * BITMAP_H);
                                stbtt_PackBegin(pc, bitmapLocal, BITMAP_W, BITMAP_H, 0, 1, 0);
                                STBTTPackedchar.Buffer charDataLocal =
                                        STBTTPackedchar.malloc(6 * EACH_CHAR_NUM);
                                charDataLocal.position(0);
                                charDataLocal.limit(EACH_CHAR_NUM);
                                stbtt_PackSetOversampling(pc, 1, 1);
                                stbtt_PackFontRange(pc, ttf, 0, SCALE, ti * EACH_CHAR_NUM, charDataLocal);

                                stbtt_PackEnd(pc);
                                if (testPrintFontBmp) {
                                    stbi_write_bmp("font_texture" + ti + ".bmp", BITMAP_W, BITMAP_H, 1,
                                            bitmapLocal);
                                }
                                return new LoadBitmapPojo(ti, bitmapLocal, charDataLocal);
                            }
                        }
                    }
            );
        }

        this.bitmaps.clear();
        this.getCharDatas().clear();

        try {
            List<Future<LoadBitmapPojo>> result = executorService.invokeAll(returnValueList);
            int ti = 0;
            for (Future<LoadBitmapPojo> au : result) {
                LoadBitmapPojo pojo = au.get();
                assert (ti == pojo.index);
                ti++;
                this.bitmaps.add(pojo.bitmapLocal);
                this.getCharDatas().add(pojo.charDataLocal);
            }
        } catch (Exception e) {
            LOGGER.error("Font.loadBitmap fails: Font:{}, fileObject:{}", this, fileObject, e);
            return false;
        }

        executorService.shutdown();
        MemoryUtil.memFree(ttf);
        return true;
    }


    /**
     * <p>init.</p>
     *
     * @param gameWindow gameWindow
     */
    @MainThreadOnly
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
        this.bitmaps.clear();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void bind(int sampler) {
        super.bind(sampler);
        Shader.unbind();
        getGameWindow().bindGlViewportToFullWindow();
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        glOrtho(
                0.0,
                getGameWindow().getRealWindowWidth(),
                getGameWindow().getRealWindowHeight(),
                0.0,
                -1.0,
                1.0
        );
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
     * @return a {@link com.xenoamess.cyan_potion.base.visual.DrawTextStruct} object.
     */
    @SuppressWarnings("unused")
    public DrawTextStruct drawTextLeftTop(
            float leftTopPosX,
            float leftTopPosY,
            float scaleX,
            float scaleY,
            float characterSpace,
            Vector4fc color,
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
     * @return a {@link com.xenoamess.cyan_potion.base.visual.DrawTextStruct} object.
     * @see Font#drawTextLeftTop(float, float, float, float, float, Vector4fc, String)
     */
    @SuppressWarnings("UnusedReturnValue")
    public DrawTextStruct drawTextLeftTop(DrawTextStruct drawTextStruct) {
        this.bind();

        getXb().put(0, drawTextStruct.getLeftTopPosX());
        getYb().put(0, drawTextStruct.getLeftTopPosY());

        glEnable(GL_TEXTURE_2D);

        if (drawTextStruct.getColor() != null) {
            glColor4f(
                    drawTextStruct.getColor().x(),
                    drawTextStruct.getColor().y(),
                    drawTextStruct.getColor().z(),
                    drawTextStruct.getColor().w()
            );
        }

        float lastXReal = drawTextStruct.getLeftTopPosX();
        float lastYReal = drawTextStruct.getLeftTopPosY();
        float lastXShould = drawTextStruct.getLeftTopPosX();
        float lastYShould = drawTextStruct.getLeftTopPosY();
        for (int i = 0; i < drawTextStruct.getText().length(); i++) {
            if (drawTextStruct.getText().charAt(i) < 32) {
                continue;
            }
            glBindTexture(
                    GL_TEXTURE_2D,
                    getFontTextures().getPrimitive(drawTextStruct.getText().charAt(i) / EACH_CHAR_NUM)
            );
            glBegin(GL_QUADS);
            stbtt_GetPackedQuad(
                    getCharDatas().get(drawTextStruct.getText().charAt(i) / EACH_CHAR_NUM),
                    BITMAP_W,
                    BITMAP_H,
                    drawTextStruct.getText().charAt(i) % EACH_CHAR_NUM,
                    getXb(),
                    getYb(),
                    getQ(),
                    false
            );

            float charWidthShould = getQ().x1() - getQ().x0();
            float charHeightShould = getQ().y1() - getQ().y0();
            float spaceLeftToCharShould = getQ().x0() - lastXShould;
            float spaceUpToCharShould = getQ().y0() - lastYShould;
            float nowX0 = lastXReal + spaceLeftToCharShould * drawTextStruct.getScaleX();
            float nowY0 = lastYReal + spaceUpToCharShould * drawTextStruct.getScaleY();

            drawBoxTC(
                    nowX0, nowY0 + drawTextStruct.getHeight() * 0.8f,
                    nowX0 + charWidthShould * drawTextStruct.getScaleX(),
                    nowY0 + charHeightShould * drawTextStruct.getScaleY() + drawTextStruct.getHeight() * 0.8f,
                    getQ().s0(), getQ().t0(), getQ().s1(), getQ().t1()
            );
            lastXReal = nowX0 + charWidthShould * drawTextStruct.getScaleX();
            lastYReal = drawTextStruct.getLeftTopPosY();
            lastXShould = getQ().x1() + drawTextStruct.getCharacterSpace();
            lastYShould = drawTextStruct.getLeftTopPosY();
            glEnd();
        }
        drawTextStruct.setWidth(lastXReal - drawTextStruct.getLeftTopPosX());
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
     * @return a {@link com.xenoamess.cyan_potion.base.visual.DrawTextStruct} object.
     */
    @SuppressWarnings("unused")
    public DrawTextStruct drawTextFillAreaLeftTop(
            float leftTopPosX,
            float leftTopPosY,
            float width,
            float height,
            float characterSpace,
            Vector4fc color,
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

    private float maxCharHeight = Float.NaN;

    @SuppressWarnings("SpellCheckingInspection")
    static final String text =
            "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    /**
     * <p>Getter for the field <code>maxCharHeight</code>.</p>
     *
     * @return a float.
     */
    public float getMaxCharHeight() {
        if (Float.isNaN(maxCharHeight)) {
            this.bind();
            getXb().put(0, 0);
            getYb().put(0, 0);
            glEnable(GL_TEXTURE_2D);
            glBindTexture(GL_TEXTURE_2D, getFontTextures().get(0));

//            glBegin(GL_QUADS);

            //noinspection unused
            float x3 = Float.MIN_VALUE;
            //noinspection unused
            float y3 = Float.MIN_VALUE;

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
            glDeleteTextures(it.nextPrimitive());
        }
        this.getFontTextures().clear();
    }

    /**
     * <p>Setter for the field <code>defaultFont</code>.</p>
     *
     * @param defaultFont defaultFont
     */
    public static void setDefaultFont(Font defaultFont) {
        asFinalFieldSet(Font.class, "defaultFont", defaultFont);
    }
}
