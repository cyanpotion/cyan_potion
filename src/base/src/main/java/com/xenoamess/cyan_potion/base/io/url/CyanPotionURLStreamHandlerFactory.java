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

package com.xenoamess.cyan_potion.base.io.url;

import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com.xenoamess.cyan_potion.base.io.url.CyanPotionURLStreamHandler.PROTOCOL_CYAN_POTION;

/**
 * TODO This class is not stable yet. Will be removed or modified in future.
 *
 * @author XenoAmess
 * @version 0.155.2
 */
public class CyanPotionURLStreamHandlerFactory implements URLStreamHandlerFactory {
    private String userDir;

    /**
     * <p>Constructor for CyanPotionURLStreamHandlerFactory.</p>
     */
    public CyanPotionURLStreamHandlerFactory() {
        this(generateUserDir());
    }

    /**
     * <p>Constructor for CyanPotionURLStreamHandlerFactory.</p>
     *
     * @param userDir a {@link java.lang.String} object.
     */
    public CyanPotionURLStreamHandlerFactory(String userDir) {
        this.userDir = userDir;
    }

    private static String generateUserDir() {
        Path currentRelativePath = Paths.get("");
        String userDir = currentRelativePath.toAbsolutePath().toString();
        return userDir;
    }

    private final CyanPotionURLStreamHandler cyanPotionURLStreamHandler = new CyanPotionURLStreamHandler(this.userDir);

    /**
     * {@inheritDoc}
     * <p>
     * Creates a new {@code URLStreamHandler} instance with the specified
     * protocol.
     *
     * @see URLStreamHandler
     */
    @Override
    public URLStreamHandler createURLStreamHandler(String protocol) {
        if (PROTOCOL_CYAN_POTION.equalsIgnoreCase(protocol)) {
            return cyanPotionURLStreamHandler;
        }
        return null;
    }
}
