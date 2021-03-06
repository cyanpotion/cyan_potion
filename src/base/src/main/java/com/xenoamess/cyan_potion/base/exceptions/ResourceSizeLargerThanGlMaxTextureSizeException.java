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

package com.xenoamess.cyan_potion.base.exceptions;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.xenoamess.cyan_potion.base.memory.NormalResource;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * <p>ResourceSizeLargerThanGlMaxTextureSizeException class.</p>
 *
 * @author XenoAmess
 * @version 0.162.3
 * @see ResourceSizeLargerThanGlMaxTextureSizeException#check
 */
public class ResourceSizeLargerThanGlMaxTextureSizeException extends RuntimeException {
    @JsonIgnore
    private static final transient Logger LOGGER =
            LoggerFactory.getLogger(ResourceSizeLargerThanGlMaxTextureSizeException.class);

    /**
     * if STRICT be true, then will throw ResourceSizeLargerThanGlMaxTextureSizeException if resource.getMemorySize()
     * &gt; resource.getResourceManager().getMaxTextureSize()
     * this shall be true only if you are testing,
     * or on some very special use cases.
     */
    @Getter
    @Setter
    private static boolean strict = false;

    /**
     * check if resource.getMemorySize() &gt; resource.getResourceManager().getMaxTextureSize()
     * if so, throw ResourceSizeLargerThanGlMaxTextureSizeException.
     * notice that this only work when STRICT==true.
     *
     * @param resource a {@link NormalResource} object.
     */
    public static void check(NormalResource resource) {

        if (resource.getMemorySize() > resource.getResourceManager().getMaxTextureSize()) {
            ResourceSizeLargerThanGlMaxTextureSizeException exception =
                    new ResourceSizeLargerThanGlMaxTextureSizeException(resource);
            if (isStrict()) {
                LOGGER.error(exception.getMessage(), exception);
                throw exception;
            } else {
                LOGGER.warn(exception.getMessage(), exception);
            }
        }
    }

    /**
     * <p>Constructor for TextureSizeLargerThanGlMaxTextureSize.</p>
     *
     * @param resource resource checked
     */
    private ResourceSizeLargerThanGlMaxTextureSizeException(NormalResource resource) {
        super("MAX_TEXTURE_SIZE is " + resource.getResourceManager().getMaxTextureSize() + " but need " + resource.getMemorySize() + ", resourceInfo:" + resource.getResourceInfo());
    }
}
