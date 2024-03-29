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

import com.xenoamess.cyan_potion.base.memory.NormalResource;
import com.xenoamess.cyan_potion.base.memory.ResourceInfo;

/**
 * This exception is thrown when we fail to parse a uri somewhere.
 * The most common case is from loading AbstractResource, or CodePlugin.
 *
 * @author XenoAmess
 * @version 0.162.3
 * @see NormalResource#load()
 * @see com.xenoamess.cyan_potion.base.plugins.CodePluginManager#getCodePluginFunctionFromString(String)
 */
public class URITypeNotDefinedException extends RuntimeException {
    /**
     * <p>Constructor for URITypeNotDefinedException.</p>
     *
     * @param message message
     */
    public URITypeNotDefinedException(String message) {
        super(message);
    }

    /**
     * <p>Constructor for URITypeNotDefinedException.</p>
     *
     * @param resourceInfo a {@link com.xenoamess.cyan_potion.base.memory.ResourceInfo} object.
     */
    public URITypeNotDefinedException(ResourceInfo resourceInfo) {
        this(resourceInfo.toString());
    }
}
