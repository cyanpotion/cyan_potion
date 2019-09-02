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

package com.xenoamess.cyan_potion.base.exceptions;

import com.xenoamess.cyan_potion.base.memory.AbstractResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;


/**
 * This error will be thrown when the
 *
 * @author XenoAmess
 * @version 0.143.0
 */
public class ResourceSizeLargerThanGlMaxTextureSize extends RuntimeException {
    private static final Logger LOGGER =
            LoggerFactory.getLogger(ResourceSizeLargerThanGlMaxTextureSize.class);


    public static void check(AbstractResource resource) {
        if (resource.getMemorySize() > resource.getResourceManager().getMaxTextureSize()) {
            throw new ResourceSizeLargerThanGlMaxTextureSize(resource);
        }
    }

//    public static void check(long resourceSize) {
//        if (resourceSize > MAX_TEXTURE_SIZE) {
//            throw new ResourceSizeLargerThanGlMaxTextureSize(resourceSize);
//        }
//    }

    /**
     * <p>Constructor for TextureSizeLargerThanGlMaxTextureSize.</p>
     *
     * @param resource resource checked
     */
    public ResourceSizeLargerThanGlMaxTextureSize(AbstractResource resource) {
        super("MAX_TEXTURE_SIZE is " + resource.getResourceManager().getMaxTextureSize() + " but need " + resource.getMemorySize());
        try {
            Field loggerField = resource.getClass().getDeclaredField("LOGGER");
            loggerField.setAccessible(true);
            ((Logger) loggerField.get(resource)).error(
                    "MAX_TEXTURE_SIZE is {} but need {}",
                    resource.getResourceManager().getMaxTextureSize(),
                    resource.getMemorySize()
            );
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

//    /**
//     * <p>Constructor for TextureSizeLargerThanGlMaxTextureSize.</p>
//     *
//     * @param resourceSize resource size needed
//     */
//    public ResourceSizeLargerThanGlMaxTextureSize(long resourceSize) {
//        super("MAX_TEXTURE_SIZE is " + MAX_TEXTURE_SIZE + " but need " + resourceSize);
//        LOGGER.error("MAX_TEXTURE_SIZE is {} but need {}",
//                MAX_TEXTURE_SIZE,
//                resourceSize
//        );
//    }

}
