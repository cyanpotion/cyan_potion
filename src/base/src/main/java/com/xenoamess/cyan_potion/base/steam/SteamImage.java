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
package com.xenoamess.cyan_potion.base.steam;

import com.codedisaster.steamworks.SteamException;
import com.codedisaster.steamworks.SteamUtils;

import java.nio.ByteBuffer;

/**
 * SteamImage
 * SteamImage means image got from steam.
 * I create this wrapper class for more convenience dealing with steamworks4j's image.
 * <p>
 * I already created pull request for this class to steamworks4j.
 * If the pull request is accepted then I will remove this class here.
 * But right now let's just use this class here.
 *
 * @author XenoAmess
 * @version 0.162.1-SNAPSHOT
 */
public class SteamImage {
    private final int imageHandle;
    private int width = -1;
    private int height = -1;
    private ByteBuffer imageBuffer = null;

    /**
     * <p>Constructor for SteamImage.</p>
     *
     * @param imageHandle a int.
     */
    public SteamImage(int imageHandle) {
        this.imageHandle = imageHandle;
    }

    /**
     * <p>Getter for the field <code>imageHandle</code>.</p>
     *
     * @return a int.
     */
    public int getImageHandle() {
        return imageHandle;
    }

    /**
     * <p>Getter for the field <code>width</code>.</p>
     *
     * @param steamUtils a {@link com.codedisaster.steamworks.SteamUtils} object.
     * @return a int.
     */
    public int getWidth(SteamUtils steamUtils) {
        if (width == -1) {
            this.getImageSizeAndSet(steamUtils);
        }
        return width;
    }

    /**
     * <p>Getter for the field <code>height</code>.</p>
     *
     * @param steamUtils a {@link com.codedisaster.steamworks.SteamUtils} object.
     * @return a int.
     */
    public int getHeight(SteamUtils steamUtils) {
        if (height == -1) {
            this.getImageSizeAndSet(steamUtils);
        }
        return height;
    }

    /**
     * <p>Getter for the field <code>imageBuffer</code>.</p>
     *
     * @param steamUtils a {@link com.codedisaster.steamworks.SteamUtils} object.
     * @return a {@link java.nio.ByteBuffer} object.
     * @throws com.codedisaster.steamworks.SteamException if any.
     */
    public ByteBuffer getImageBuffer(SteamUtils steamUtils) throws SteamException {
        if (this.imageBuffer != null) {
            return this.imageBuffer;
        }
        ByteBuffer imageBufferLocal =
                ByteBuffer.allocateDirect(this.getWidth(steamUtils) * this.getHeight(steamUtils) * 4);
        if (steamUtils.getImageRGBA(getImageHandle(), imageBufferLocal)) {
            this.imageBuffer = imageBufferLocal;
        }
        return this.imageBuffer;
    }

    /**
     * <p>getImageSizeAndSet.</p>
     *
     * @param steamUtils a {@link com.codedisaster.steamworks.SteamUtils} object.
     */
    protected void getImageSizeAndSet(SteamUtils steamUtils) {
        int[] size = new int[2];
        steamUtils.getImageSize(getImageHandle(), size);
        this.width = size[0];
        this.height = size[1];
    }

}
