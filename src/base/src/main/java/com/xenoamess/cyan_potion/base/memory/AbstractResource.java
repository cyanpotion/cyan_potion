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

package com.xenoamess.cyan_potion.base.memory;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.xenoamess.cyan_potion.base.render.Bindable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;

/**
 * <p>Abstract AbstractResource class.</p>
 *
 * @author XenoAmess
 * @version 0.162.3
 */
public interface AbstractResource extends Closeable, Bindable {
    @JsonIgnore
    Logger LOGGER = LoggerFactory.getLogger(AbstractResource.class);

    /**
     * get resource info
     * @return resource info
     */
    ResourceInfo getResourceInfo();

    /**
     * get resource manager
     * @return resource manager
     */
    ResourceManager getResourceManager();

    /**
     * {@inheritDoc}
     */
    @Override
    default void bind(int sampler) {
        this.load();
    }

    /**
     * will wait until load completed(make sure be loaded).
     *
     * @return after the function, if this Resource loaded correctly and is in memory, then true.
     * otherwise if cannot load correctly, then false.
     */
    @SuppressWarnings("UnusedReturnValue")
    boolean load();

    /**
     * <p>reload.</p>
     */
    @SuppressWarnings("unused")
    default void reload() {
        this.close();
        this.load();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void close();

    /**
     * <p>isInMemory.</p>
     *
     * @return a boolean.
     */
    boolean isInMemory();

    /**
     * <p>Setter for the field <code>inMemory</code>.</p>
     *
     * @param inMemory a boolean.
     */
    void setInMemory(boolean inMemory);

    /**
     * get last used frame index
     *
     * @return last used frame index
     */
    long getLastUsedFrameIndex();
}
