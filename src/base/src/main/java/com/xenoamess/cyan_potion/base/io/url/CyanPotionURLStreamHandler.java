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

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

/**
 * TODO This class is not stable yet. Will be removed or modified in future.
 *
 * @author XenoAmess
 * @version 0.157.0
 */
public class CyanPotionURLStreamHandler extends URLStreamHandler {
    /**
     * Constant <code>PROTOCOL_CYAN_POTION="cyan_potion"</code>
     */
    public static final String PROTOCOL_CYAN_POTION = "cyan_potion";

    private String userDir;

    /**
     * <p>Constructor for CyanPotionURLStreamHandler.</p>
     *
     * @param userDir a {@link java.lang.String} object.
     */
    public CyanPotionURLStreamHandler(String userDir) {
        this.userDir = userDir;
    }

    static void assureProtocolBeProtocolCyanPotion(URL url) {
        String protocol = url.getProtocol();
        if (!PROTOCOL_CYAN_POTION.equalsIgnoreCase(protocol)) {
            throw new IllegalArgumentException(String.format("Wrong protocol : Expected = %s , Get = %s",
                    PROTOCOL_CYAN_POTION, protocol));
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * Opens a connection to the object referenced by the
     * {@code URL} argument.
     * This method should be overridden by a subclass.
     *
     * <p>If for the handler's protocol (such as HTTP or JAR), there
     * exists a public, specialized URLConnection subclass belonging
     * to one of the following packages or one of their subpackages:
     * java.lang, java.io, java.util, java.net, the connection
     * returned will be of that subclass. For example, for HTTP an
     * HttpURLConnection will be returned, and for JAR a
     * JarURLConnection will be returned.
     */
    @Override
    protected URLConnection openConnection(URL url) throws IOException {
        assureProtocolBeProtocolCyanPotion(url);
        return new CyanPotionURLConnection(this.userDir, url);
    }
}
