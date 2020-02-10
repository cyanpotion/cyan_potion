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

package com.xenoamess.cyan_potion.rpg_module.render;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.xenoamess.commons.main_thread_only.MainThreadOnly;
import com.xenoamess.cyan_potion.base.DataCenter;
import com.xenoamess.cyan_potion.base.exceptions.TextureStateDisorderException;
import com.xenoamess.cyan_potion.base.exceptions.URITypeNotDefinedException;
import com.xenoamess.cyan_potion.base.memory.ResourceInfo;
import com.xenoamess.cyan_potion.base.memory.ResourceManager;
import com.xenoamess.cyan_potion.base.render.Texture;
import org.lwjgl.system.MemoryUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * <p>TextureUtils class.</p>
 *
 * @author XenoAmess
 * @version 0.157.0
 */
public class TextureUtils {
    /**
     * Constant <code>STRING_CHARACTER="characters"</code>
     */
    public static final String STRING_CHARACTER = "characters";
    /**
     * Constant <code>STRING_A5="A5"</code>
     */
    public static final String STRING_A5 = "A5";
    /**
     * Constant <code>STRING_B="B"</code>
     */
    public static final String STRING_B = "B";
    /**
     * Constant <code>STRING_C="C"</code>
     */
    public static final String STRING_C = "C";
    /**
     * Constant <code>STRING_A2="A2"</code>
     */
    public static final String STRING_A2 = "A2";

    @JsonIgnore
    private static transient final Logger LOGGER =
            LoggerFactory.getLogger(TextureUtils.class);

    private TextureUtils() {

    }

    /**
     * <p>loadAsWalkingTexture.</p>
     *
     * @param texture a {@link com.xenoamess.cyan_potion.base.render.Texture} object.
     * @return a boolean.
     */
    @MainThreadOnly
    public static boolean loadAsWalkingTexture(Texture texture) {
        if (!DataCenter.ifMainThread()) {
            return false;
        }

        ResourceInfo<Texture> resourceInfo = texture.getResourceInfo();

        final int peopleIndex = Integer.parseInt(resourceInfo.getValues()[0]);
        final int textureIndex = Integer.parseInt(resourceInfo.getValues()[1]);


        BufferedImage bufferedImage = null;

        try (InputStream inputStream = resourceInfo.getFileObject().getContent().getInputStream()) {
            bufferedImage = ImageIO.read(inputStream);
        } catch (IOException e) {
            LOGGER.error("TextureUtils.loadAsWalkingTexture(Texture texture, ResourceInfo resourceInfo) fails:{},{}",
                    texture, resourceInfo, e);
        }
        assert (bufferedImage != null);
        final int entireWidth = bufferedImage.getWidth();
        final int entireHeight = bufferedImage.getHeight();
        final int singleWidth = entireWidth / 4 / 3;
        final int singleHeight = entireHeight / 2 / 4;

        final int[] pixelsRaw = bufferedImage.getRGB(0, 0, entireWidth,
                entireHeight, null, 0, entireWidth);

        int startPosX = 0;
        int startPosY = 0;
        int nowPosX;
        int nowPosY;

        for (int k = 0; k < 8; k++) {
            if (k == 4) {
                startPosX = 0;
                startPosY = singleHeight * 4;
            }

            nowPosX = startPosX;
            nowPosY = startPosY;

            for (int i = 0; i < 4; i++) {
                nowPosX = startPosX;
                for (int j = 0; j < 3; j++) {
                    final Texture nowTexture =
                            texture.getResourceManager().fetchResource(
                                    texture.getClass(),
                                    STRING_CHARACTER,
                                    texture.getResourceInfo().getFileString(),
                                    Integer.toString(k),
                                    Integer.toString(i * 3 + j)
                            );
                    if (!nowTexture.isInMemory()) {
                        nowTexture.bake(singleWidth, singleHeight,
                                entireWidth, entireHeight, nowPosX, nowPosY,
                                pixelsRaw);
                        if ((nowTexture.getGlTexture2DInt() == -1) == (nowTexture.isInMemory())) {
                            throw new TextureStateDisorderException(nowTexture);
                        }
                    }
                    nowPosX += singleWidth;
                }
                nowPosY += singleHeight;
            }
            startPosX += singleWidth * 3;
        }

        return true;
    }


    /**
     * <p>loadAsTilesetTextures8.</p>
     *
     * @param texture a {@link com.xenoamess.cyan_potion.base.render.Texture} object.
     * @return a boolean.
     */
    @MainThreadOnly
    public static boolean loadAsTilesetTextures8(Texture texture) {
        if (!DataCenter.ifMainThread()) {
            return false;
        }

        ResourceInfo resourceInfo = texture.getResourceInfo();
        final String resourceType = resourceInfo.getType();
        int columnNum;
        switch (resourceType) {
            case STRING_A5:
                columnNum = 1;
                break;
            case STRING_B:
            case STRING_C:
                columnNum = 2;
                break;
            default:
                throw new URITypeNotDefinedException(texture.getResourceInfo());
        }

        BufferedImage bufferedImage = null;

        try (InputStream inputStream = resourceInfo.getFileObject().getContent().getInputStream()) {
            bufferedImage =
                    ImageIO.read(inputStream);
        } catch (IOException e) {
            LOGGER.error("TextureUtils.loadAsTilesetTextures8(Texture texture) " +
                            "fails:{},{}",
                    texture, resourceInfo, e);
        }
        assert (bufferedImage != null);
        final int entireWidth = bufferedImage.getWidth();
        final int entireHeight = bufferedImage.getHeight();
        final int singleWidth = entireWidth / columnNum / 8;
        final int singleHeight = singleWidth;

        final int[] pixelsRaw = bufferedImage.getRGB(0, 0, entireWidth,
                entireHeight, null, 0, entireWidth);

        int startPosX = 0;
        int startPosY = 0;
        int nowPosX;
        int nowPosY;

        for (int k = 0; k < columnNum; k++) {
            nowPosX = startPosX;
            nowPosY = startPosY;
            for (int i = 0; i < entireHeight / singleHeight; i++) {

                nowPosX = startPosX;
                for (int j = 0; j < 8; j++) {
                    final Texture nowTexture =
                            texture.getResourceManager().fetchResource(
                                    Texture.class,
                                    resourceType,
                                    resourceInfo.getFileString(),
                                    Integer.toString(k),
                                    Integer.toString(i * 8 + j)
                            );
                    if (texture.getResourceManager().getGameManager().getDataCenter().getGameSettings().isDebug()
                            && nowTexture.getResourceInfo().equals(texture.getResourceInfo())
                            && nowTexture != texture
                    ) {
                        boolean a = nowTexture.getResourceInfo().equals(texture.getResourceInfo());
                        boolean b = nowTexture.getResourceInfo().hashCode() == texture.getResourceInfo().hashCode();
                        LOGGER.error("severe error! get two different Texture object for one same resourceInfo, equals:{}, hashCode:{}", a, b);
                        System.exit(1);
                    }

                    if (!nowTexture.isInMemory()) {
                        nowTexture.bake(singleWidth, singleHeight,
                                entireWidth, entireHeight, nowPosX, nowPosY,
                                pixelsRaw);
                        if ((nowTexture.getGlTexture2DInt() == -1) == (nowTexture.isInMemory())) {
                            throw new TextureStateDisorderException(nowTexture);
                        }
                    }
                    nowPosX += singleWidth;
                }
                nowPosY += singleHeight;
            }

            startPosY = 0;
            startPosX += entireWidth / columnNum;
        }

        return true;
    }

    @MainThreadOnly
    private static boolean loadTilesetTextureA2SingleSingle(ResourceManager resourceManager, String fileString,
                                                            int kk, int ti, int singleSingleWidth, int singleSingleHeight,
                                                            int[] pixelsRaws0, int[] pixelsRaws1, int[] pixelsRaws2,
                                                            int[] pixelsRaws3) {
        if (!DataCenter.ifMainThread()) {
            return false;
        }

        final Texture nowTexture =
                resourceManager.fetchResource(
                        Texture.class,
                        STRING_A2,
                        fileString,
                        Integer.toString(kk),
                        Integer.toString(ti)
                );
        if ((nowTexture.getGlTexture2DInt() == -1) == (nowTexture.isInMemory())) {
            throw new TextureStateDisorderException(nowTexture);
        }
        if (nowTexture.isInMemory()) {
            return true;
        }
        final ByteBuffer byteBuffer =
                MemoryUtil.memAlloc(singleSingleWidth * 2 * singleSingleHeight * 2 * 4);

        for (int i = 0; i < singleSingleHeight; i++) {
            for (int j = 0; j < singleSingleWidth; j++) {
                int pixel = pixelsRaws0[i * singleSingleWidth + j];
                byteBuffer.put((byte) ((pixel >> 16) & 0xFF)); // RED
                byteBuffer.put((byte) ((pixel >> 8) & 0xFF));  // GREEN
                byteBuffer.put((byte) (pixel & 0xFF));          // BLUE
                byteBuffer.put((byte) ((pixel >> 24) & 0xFF)); // ALPHA
            }
            for (int j = 0; j < singleSingleWidth; j++) {
                int pixel = pixelsRaws1[i * singleSingleWidth + j];
                byteBuffer.put((byte) ((pixel >> 16) & 0xFF)); // RED
                byteBuffer.put((byte) ((pixel >> 8) & 0xFF));  // GREEN
                byteBuffer.put((byte) (pixel & 0xFF));          // BLUE
                byteBuffer.put((byte) ((pixel >> 24) & 0xFF)); // ALPHA
            }
        }
        for (int i = 0; i < singleSingleHeight; i++) {
            for (int j = 0; j < singleSingleWidth; j++) {
                int pixel = pixelsRaws2[i * singleSingleWidth + j];
                byteBuffer.put((byte) ((pixel >> 16) & 0xFF)); // RED
                byteBuffer.put((byte) ((pixel >> 8) & 0xFF));  // GREEN
                byteBuffer.put((byte) (pixel & 0xFF));          // BLUE
                byteBuffer.put((byte) ((pixel >> 24) & 0xFF)); // ALPHA
            }
            for (int j = 0; j < singleSingleWidth; j++) {
                int pixel = pixelsRaws3[i * singleSingleWidth + j];
                byteBuffer.put((byte) ((pixel >> 16) & 0xFF)); // RED
                byteBuffer.put((byte) ((pixel >> 8) & 0xFF));  // GREEN
                byteBuffer.put((byte) (pixel & 0xFF));          // BLUE
                byteBuffer.put((byte) ((pixel >> 24) & 0xFF)); // ALPHA
            }
        }
        byteBuffer.flip();

        if (!nowTexture.isInMemory()) {
            nowTexture.bake(singleSingleWidth * 2, singleSingleHeight * 2,
                    byteBuffer);
        }
        MemoryUtil.memFree(byteBuffer);

        return true;
    }

    @MainThreadOnly
    private static boolean loadTilesetTexturesA2Single(ResourceManager resourceManager,
                                                       String fileString, int kk, int singleWidth,
                                                       int singleHeight, int entireWidth,
                                                       int entireHeight, int startWidth,
                                                       int startHeight, int[] pixelsRaw) {
        if (!DataCenter.ifMainThread()) {
            return false;
        }

        final int singleSingleWidth = singleWidth / 2;
        final int singleSingleHeight = singleHeight / 2;

        final int[][] pixelsRaws =
                new int[25][singleSingleWidth * singleSingleHeight];

        int nowPosX = startWidth;
        int nowPosY = startHeight;
        for (int k = 1; k <= 24; k++) {
            for (int i = 0; i < singleSingleHeight; i++) {
                for (int j = 0; j < singleSingleWidth; j++) {
                    pixelsRaws[k][i * singleSingleWidth + j] =
                            pixelsRaw[(nowPosY + i) * entireWidth + nowPosX + j];
                }
            }
            nowPosX += singleSingleWidth;
            if (k % 4 == 0) {
                nowPosY += singleSingleHeight;
                nowPosX = startWidth;
            }
        }
        {
            int ti = 0;
            //0
            loadTilesetTextureA2SingleSingle(resourceManager,
                    fileString, kk, ti, singleSingleWidth,
                    singleSingleHeight, pixelsRaws[19], pixelsRaws[18],
                    pixelsRaws[15], pixelsRaws[14]);
            ti++;
            //1
            loadTilesetTextureA2SingleSingle(resourceManager,
                    fileString, kk, ti, singleSingleWidth,
                    singleSingleHeight, pixelsRaws[3], pixelsRaws[18],
                    pixelsRaws[15], pixelsRaws[14]);
            ti++;
            //2
            loadTilesetTextureA2SingleSingle(resourceManager,
                    fileString, kk, ti, singleSingleWidth,
                    singleSingleHeight, pixelsRaws[19], pixelsRaws[4],
                    pixelsRaws[15], pixelsRaws[14]);
            ti++;
            //3
            loadTilesetTextureA2SingleSingle(resourceManager,
                    fileString, kk, ti, singleSingleWidth,
                    singleSingleHeight, pixelsRaws[3], pixelsRaws[4],
                    pixelsRaws[15], pixelsRaws[14]);
            ti++;
            //4
            loadTilesetTextureA2SingleSingle(resourceManager,
                    fileString, kk, ti, singleSingleWidth,
                    singleSingleHeight, pixelsRaws[19], pixelsRaws[18],
                    pixelsRaws[15], pixelsRaws[8]);
            ti++;
            //5
            loadTilesetTextureA2SingleSingle(resourceManager,
                    fileString, kk, ti, singleSingleWidth,
                    singleSingleHeight, pixelsRaws[3], pixelsRaws[18],
                    pixelsRaws[15], pixelsRaws[8]);
            ti++;
            //6
            loadTilesetTextureA2SingleSingle(resourceManager,
                    fileString, kk, ti, singleSingleWidth,
                    singleSingleHeight, pixelsRaws[19], pixelsRaws[4],
                    pixelsRaws[15], pixelsRaws[8]);
            ti++;
            //7
            loadTilesetTextureA2SingleSingle(resourceManager,
                    fileString, kk, ti, singleSingleWidth,
                    singleSingleHeight, pixelsRaws[3], pixelsRaws[4],
                    pixelsRaws[15], pixelsRaws[8]);
            ti++;
            //8
            loadTilesetTextureA2SingleSingle(resourceManager,
                    fileString, kk, ti, singleSingleWidth,
                    singleSingleHeight, pixelsRaws[19], pixelsRaws[18],
                    pixelsRaws[7], pixelsRaws[14]);
            ti++;
            //9
            loadTilesetTextureA2SingleSingle(resourceManager,
                    fileString, kk, ti, singleSingleWidth,
                    singleSingleHeight, pixelsRaws[3], pixelsRaws[18],
                    pixelsRaws[7], pixelsRaws[14]);
            ti++;
            //10
            loadTilesetTextureA2SingleSingle(resourceManager,
                    fileString, kk, ti, singleSingleWidth,
                    singleSingleHeight, pixelsRaws[19], pixelsRaws[4],
                    pixelsRaws[7], pixelsRaws[14]);
            ti++;
            //11
            loadTilesetTextureA2SingleSingle(resourceManager,
                    fileString, kk, ti, singleSingleWidth,
                    singleSingleHeight, pixelsRaws[3], pixelsRaws[4],
                    pixelsRaws[7], pixelsRaws[14]);
            ti++;
            //12
            loadTilesetTextureA2SingleSingle(resourceManager,
                    fileString, kk, ti, singleSingleWidth,
                    singleSingleHeight, pixelsRaws[19], pixelsRaws[18],
                    pixelsRaws[7], pixelsRaws[8]);
            ti++;
            //13
            loadTilesetTextureA2SingleSingle(resourceManager,
                    fileString, kk, ti, singleSingleWidth,
                    singleSingleHeight, pixelsRaws[3], pixelsRaws[18],
                    pixelsRaws[7], pixelsRaws[8]);
            ti++;
            //14
            loadTilesetTextureA2SingleSingle(resourceManager,
                    fileString, kk, ti, singleSingleWidth,
                    singleSingleHeight, pixelsRaws[19], pixelsRaws[4],
                    pixelsRaws[7], pixelsRaws[8]);
            ti++;
            //15
            loadTilesetTextureA2SingleSingle(resourceManager,
                    fileString, kk, ti, singleSingleWidth,
                    singleSingleHeight, pixelsRaws[3], pixelsRaws[4],
                    pixelsRaws[7], pixelsRaws[8]);
            ti++;
            //16
            loadTilesetTextureA2SingleSingle(resourceManager,
                    fileString, kk, ti, singleSingleWidth,
                    singleSingleHeight, pixelsRaws[17], pixelsRaws[18],
                    pixelsRaws[13], pixelsRaws[14]);
            ti++;
            //17
            loadTilesetTextureA2SingleSingle(resourceManager,
                    fileString, kk, ti, singleSingleWidth,
                    singleSingleHeight, pixelsRaws[17], pixelsRaws[4],
                    pixelsRaws[13], pixelsRaws[14]);
            ti++;
            //18
            loadTilesetTextureA2SingleSingle(resourceManager,
                    fileString, kk, ti, singleSingleWidth,
                    singleSingleHeight, pixelsRaws[17], pixelsRaws[18],
                    pixelsRaws[13], pixelsRaws[8]);
            ti++;
            //19
            loadTilesetTextureA2SingleSingle(resourceManager,
                    fileString, kk, ti, singleSingleWidth,
                    singleSingleHeight, pixelsRaws[17], pixelsRaws[4],
                    pixelsRaws[13], pixelsRaws[8]);
            ti++;
            //20
            loadTilesetTextureA2SingleSingle(resourceManager,
                    fileString, kk, ti, singleSingleWidth,
                    singleSingleHeight, pixelsRaws[11], pixelsRaws[10],
                    pixelsRaws[15], pixelsRaws[14]);
            ti++;
            //21
            loadTilesetTextureA2SingleSingle(resourceManager,
                    fileString, kk, ti, singleSingleWidth,
                    singleSingleHeight, pixelsRaws[11], pixelsRaws[10],
                    pixelsRaws[15], pixelsRaws[8]);
            ti++;
            //22
            loadTilesetTextureA2SingleSingle(resourceManager,
                    fileString, kk, ti, singleSingleWidth,
                    singleSingleHeight, pixelsRaws[11], pixelsRaws[10],
                    pixelsRaws[7], pixelsRaws[14]);
            ti++;
            //23
            loadTilesetTextureA2SingleSingle(resourceManager,
                    fileString, kk, ti, singleSingleWidth,
                    singleSingleHeight, pixelsRaws[11], pixelsRaws[10],
                    pixelsRaws[7], pixelsRaws[8]);
            ti++;
            //24
            loadTilesetTextureA2SingleSingle(resourceManager,
                    fileString, kk, ti, singleSingleWidth,
                    singleSingleHeight, pixelsRaws[19], pixelsRaws[20],
                    pixelsRaws[15], pixelsRaws[16]);
            ti++;
            //25
            loadTilesetTextureA2SingleSingle(resourceManager,
                    fileString, kk, ti, singleSingleWidth,
                    singleSingleHeight, pixelsRaws[19], pixelsRaws[20],
                    pixelsRaws[7], pixelsRaws[16]);
            ti++;
            //26
            loadTilesetTextureA2SingleSingle(resourceManager,
                    fileString, kk, ti, singleSingleWidth,
                    singleSingleHeight, pixelsRaws[3], pixelsRaws[20],
                    pixelsRaws[15], pixelsRaws[16]);
            ti++;
            //27
            loadTilesetTextureA2SingleSingle(resourceManager,
                    fileString, kk, ti, singleSingleWidth,
                    singleSingleHeight, pixelsRaws[3], pixelsRaws[20],
                    pixelsRaws[7], pixelsRaws[16]);
            ti++;
            //28
            loadTilesetTextureA2SingleSingle(resourceManager,
                    fileString, kk, ti, singleSingleWidth,
                    singleSingleHeight, pixelsRaws[19], pixelsRaws[18],
                    pixelsRaws[23], pixelsRaws[22]);
            ti++;
            //29
            loadTilesetTextureA2SingleSingle(resourceManager,
                    fileString, kk, ti, singleSingleWidth,
                    singleSingleHeight, pixelsRaws[3], pixelsRaws[18],
                    pixelsRaws[23], pixelsRaws[22]);
            ti++;
            //30
            loadTilesetTextureA2SingleSingle(resourceManager,
                    fileString, kk, ti, singleSingleWidth,
                    singleSingleHeight, pixelsRaws[19], pixelsRaws[4],
                    pixelsRaws[23], pixelsRaws[22]);
            ti++;
            //31
            loadTilesetTextureA2SingleSingle(resourceManager,
                    fileString, kk, ti, singleSingleWidth,
                    singleSingleHeight, pixelsRaws[3], pixelsRaws[4],
                    pixelsRaws[23], pixelsRaws[22]);
            ti++;
            //32
            loadTilesetTextureA2SingleSingle(resourceManager,
                    fileString, kk, ti, singleSingleWidth,
                    singleSingleHeight, pixelsRaws[17], pixelsRaws[20],
                    pixelsRaws[13], pixelsRaws[16]);
            ti++;
            //33
            loadTilesetTextureA2SingleSingle(resourceManager,
                    fileString, kk, ti, singleSingleWidth,
                    singleSingleHeight, pixelsRaws[11], pixelsRaws[10],
                    pixelsRaws[23], pixelsRaws[22]);
            ti++;
            //34
            loadTilesetTextureA2SingleSingle(resourceManager,
                    fileString, kk, ti, singleSingleWidth,
                    singleSingleHeight, pixelsRaws[9], pixelsRaws[10],
                    pixelsRaws[13], pixelsRaws[14]);
            ti++;
            //35
            loadTilesetTextureA2SingleSingle(resourceManager,
                    fileString, kk, ti, singleSingleWidth,
                    singleSingleHeight, pixelsRaws[9], pixelsRaws[10],
                    pixelsRaws[13], pixelsRaws[8]);
            ti++;
            //36
            loadTilesetTextureA2SingleSingle(resourceManager,
                    fileString, kk, ti, singleSingleWidth,
                    singleSingleHeight, pixelsRaws[11], pixelsRaws[12],
                    pixelsRaws[15], pixelsRaws[16]);
            ti++;
            //37
            loadTilesetTextureA2SingleSingle(resourceManager,
                    fileString, kk, ti, singleSingleWidth,
                    singleSingleHeight, pixelsRaws[11], pixelsRaws[12],
                    pixelsRaws[7], pixelsRaws[16]);
            ti++;
            //38
            loadTilesetTextureA2SingleSingle(resourceManager,
                    fileString, kk, ti, singleSingleWidth,
                    singleSingleHeight, pixelsRaws[19], pixelsRaws[20],
                    pixelsRaws[23], pixelsRaws[24]);
            ti++;
            //39
            loadTilesetTextureA2SingleSingle(resourceManager,
                    fileString, kk, ti, singleSingleWidth,
                    singleSingleHeight, pixelsRaws[3], pixelsRaws[20],
                    pixelsRaws[23], pixelsRaws[24]);
            ti++;
            //40
            loadTilesetTextureA2SingleSingle(resourceManager,
                    fileString, kk, ti, singleSingleWidth,
                    singleSingleHeight, pixelsRaws[17], pixelsRaws[18],
                    pixelsRaws[21], pixelsRaws[22]);
            ti++;
            //41
            loadTilesetTextureA2SingleSingle(resourceManager,
                    fileString, kk, ti, singleSingleWidth,
                    singleSingleHeight, pixelsRaws[17], pixelsRaws[4],
                    pixelsRaws[21], pixelsRaws[22]);
            ti++;
            //42
            loadTilesetTextureA2SingleSingle(resourceManager,
                    fileString, kk, ti, singleSingleWidth,
                    singleSingleHeight, pixelsRaws[9], pixelsRaws[12],
                    pixelsRaws[13], pixelsRaws[16]);
            ti++;
            //43
            loadTilesetTextureA2SingleSingle(resourceManager,
                    fileString, kk, ti, singleSingleWidth,
                    singleSingleHeight, pixelsRaws[9], pixelsRaws[10],
                    pixelsRaws[21], pixelsRaws[22]);
            ti++;
            //44
            loadTilesetTextureA2SingleSingle(resourceManager,
                    fileString, kk, ti, singleSingleWidth,
                    singleSingleHeight, pixelsRaws[17], pixelsRaws[20],
                    pixelsRaws[21], pixelsRaws[24]);
            ti++;
            //45
            loadTilesetTextureA2SingleSingle(resourceManager,
                    fileString, kk, ti, singleSingleWidth,
                    singleSingleHeight, pixelsRaws[11], pixelsRaws[12],
                    pixelsRaws[23], pixelsRaws[24]);
            ti++;
            //46
            loadTilesetTextureA2SingleSingle(resourceManager,
                    fileString, kk, ti, singleSingleWidth,
                    singleSingleHeight, pixelsRaws[9], pixelsRaws[12],
                    pixelsRaws[21], pixelsRaws[24]);
            ti++;
            //47
            loadTilesetTextureA2SingleSingle(resourceManager,
                    fileString, kk, ti, singleSingleWidth,
                    singleSingleHeight, pixelsRaws[1], pixelsRaws[2],
                    pixelsRaws[5], pixelsRaws[6]);
            ti++;
        }

        return true;
    }

    /**
     * <p>loadAsTilesetTexturesA2.</p>
     *
     * @param texture a {@link com.xenoamess.cyan_potion.base.render.Texture} object.
     * @return a boolean.
     */
    @MainThreadOnly
    public static boolean loadAsTilesetTexturesA2(Texture texture) {
        if (!DataCenter.ifMainThread()) {
            return false;
        }

        ResourceInfo resourceInfo = texture.getResourceInfo();

        BufferedImage bufferedImage = null;

        try (InputStream inputStream = resourceInfo.getFileObject().getContent().getInputStream()) {
            bufferedImage =
                    ImageIO.read(inputStream);
        } catch (IOException e) {
            LOGGER.error("TextureUtils.loadAsTilesetTexturesA2(Texture texture, ResourceInfo resourceInfo) " +
                            "fails:{},{}",
                    texture, resourceInfo, e);
        }
        assert (bufferedImage != null);
        final int entireWidth = bufferedImage.getWidth();
        final int entireHeight = bufferedImage.getHeight();
        final int singleWidth = entireWidth / 8 / 2;
        final int singleHeight = singleWidth;

        final int[] pixelsRaw = bufferedImage.getRGB(0, 0, entireWidth,
                entireHeight, null, 0, entireWidth);


        int startStartPosX = 0;
        int startStartPosY = 0;
        int startPosX = 0;
        int startPosY = 0;


        for (int k = 0; k < 32; k++) {

            for (int ti = 0; ti < 48; ti++) {
                final Texture nowTexture =
                        texture.getResourceManager().fetchResource(
                                texture.getClass(),
                                STRING_A2,
                                texture.getResourceInfo().getFileString(),
                                Integer.toString(k),
                                Integer.toString(ti)
                        );
                if (!nowTexture.isInMemory()) {
                    loadTilesetTexturesA2Single(texture.getResourceManager(), resourceInfo.getFileString(),
                            k, singleWidth, singleHeight, entireWidth, entireHeight, startPosX, startPosY, pixelsRaw);
                    break;
                }
            }

            startPosX += singleWidth * 2;
            //            if (k % 16 == 15) {
            //                startPosY = startStartPosY;
            //                startStartPosX += singleWidth * 2 * 4;
            //                startPosX = startStartPosX;
            //            } else
            if (k % 8 == 7) {
                startPosY += singleHeight * 3;
                startPosX = startStartPosX;
            }
        }

        return true;
    }


    /**
     * <p>getWalkingTextures.</p>
     *
     * @param resourceManager         a {@link com.xenoamess.cyan_potion.base.memory.ResourceManager} object.
     * @param walkingTexturesFilepath walkingTexturesFilepath
     * @return return
     */
    public static List<List<Texture>> getWalkingTextures(ResourceManager resourceManager,
                                                         String walkingTexturesFilepath) {
        final List<List<Texture>> res = new ArrayList<>();

        for (int k = 0; k < 8; k++) {
            final List<Texture> nowTextures = new ArrayList<>();
            for (int i = 0; i < 4; i++) {

                for (int j = 0; j < 3; j++) {
                    Texture nowTexture =
                            resourceManager.fetchResource(
                                    Texture.class,
                                    "characters",
                                    walkingTexturesFilepath,
                                    Integer.toString(k),
                                    Integer.toString(i * 3 + j)
                            );
                    nowTextures.add(nowTexture);
                }
            }
            res.add(nowTextures);
        }

        return res;
    }


    /**
     * <p>getTilesetTexturesA2.</p>
     *
     * @param resourceManager         a {@link com.xenoamess.cyan_potion.base.memory.ResourceManager} object.
     * @param tilesetTexturesFilepath tilesetTexturesFilepath
     * @return return
     */
    public static List<Texture> getTilesetTexturesA2(ResourceManager resourceManager, String tilesetTexturesFilepath) {
        final List<Texture> res = new LinkedList<>();

        for (int k = 0; k < 32; k++) {
            for (int ti = 0; ti < 48; ti++) {
                res.add(
                        resourceManager.fetchResource(
                                Texture.class,
                                STRING_A2,
                                tilesetTexturesFilepath,
                                Integer.toString(k),
                                Integer.toString(ti)
                        )
                );
            }
        }
        return res;
    }

    /**
     * <p>getTilesetTexturesA5.</p>
     *
     * @param resourceManager         a {@link com.xenoamess.cyan_potion.base.memory.ResourceManager} object.
     * @param tilesetTexturesFilepath tilesetTexturesFilepath
     * @return return
     */
    public static List<Texture> getTilesetTexturesA5(ResourceManager resourceManager, String tilesetTexturesFilepath) {
        return getTilesetTextures8(resourceManager, STRING_A5,
                tilesetTexturesFilepath, 1);
    }

    /**
     * <p>getTilesetTexturesB.</p>
     *
     * @param resourceManager         a {@link com.xenoamess.cyan_potion.base.memory.ResourceManager} object.
     * @param tilesetTexturesFilepath tilesetTexturesFilepath
     * @return return
     */
    public static List<Texture> getTilesetTexturesB(ResourceManager resourceManager, String tilesetTexturesFilepath) {
        return getTilesetTextures8(resourceManager, "B",
                tilesetTexturesFilepath, 2);
    }

    /**
     * <p>getTilesetTexturesC.</p>
     *
     * @param resourceManager         a {@link com.xenoamess.cyan_potion.base.memory.ResourceManager} object.
     * @param tilesetTexturesFilepath tilesetTexturesFilepath
     * @return return
     */
    public static List<Texture> getTilesetTexturesC(ResourceManager resourceManager, String tilesetTexturesFilepath) {
        return getTilesetTextures8(resourceManager, STRING_C,
                tilesetTexturesFilepath, 2);
    }

    /**
     * <p>getTilesetTextures8.</p>
     *
     * @param resourceManager         a {@link com.xenoamess.cyan_potion.base.memory.ResourceManager} object.
     * @param resourceType            a {@link java.lang.String} object.
     * @param tilesetTexturesFilepath tilesetTexturesFilepath
     * @param columnNum               a int.
     * @return return
     */
    public static List<Texture> getTilesetTextures8(ResourceManager resourceManager, String resourceType, String
            tilesetTexturesFilepath, int columnNum) {
        final List<Texture> res = new ArrayList<>();

        BufferedImage bufferedImage = null;

        try (InputStream inputStream = ResourceManager.resolveFile(tilesetTexturesFilepath).getContent().getInputStream()) {
            bufferedImage = ImageIO.read(inputStream);
        } catch (IOException e) {
            LOGGER.error("TextureUtils.getTilesetTextures8(ResourceManager resourceManager, String resourceType, " +
                            "String tilesetTexturesFilepath, int columnNum) fails:{},{},{},{}",
                    resourceManager, resourceType, tilesetTexturesFilepath, columnNum, e);
        }
        assert (bufferedImage != null);
        final int entireWidth = bufferedImage.getWidth();
        final int entireHeight = bufferedImage.getHeight();
        final int singleWidth = entireWidth / columnNum / 8;
        final int singleHeight = singleWidth;

        for (int k = 0; k < columnNum; k++) {
            for (int i = 0; i < entireHeight / singleHeight; i++) {
                for (int j = 0; j < 8; j++) {
                    res.add(
                            resourceManager.fetchResource(
                                    Texture.class,
                                    resourceType,
                                    tilesetTexturesFilepath,
                                    Integer.toString(k),
                                    Integer.toString(i * 8 + j)
                            )
                    );
                }
            }
        }
        return res;
    }

}
