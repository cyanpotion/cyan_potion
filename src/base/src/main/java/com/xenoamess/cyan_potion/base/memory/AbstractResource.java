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

package com.xenoamess.cyan_potion.base.memory;

import com.xenoamess.cyan_potion.base.exceptions.URITypeNotDefinedException;
import com.xenoamess.cyan_potion.base.render.Bindable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

/**
 * <p>Abstract AbstractResource class.</p>
 *
 * @author XenoAmess
 * @version 0.143.0
 */
public abstract class AbstractResource implements AutoCloseable, Bindable {
    private static final Logger LOGGER =
            LoggerFactory.getLogger(AbstractResource.class);

    private final ResourceManager resourceManager;
    private final String fullResourceURI;
    private long memorySize;
    private final AtomicBoolean inMemory = new AtomicBoolean(false);
    private long lastUsedFrameIndex;

    /**
     * !!!NOTICE!!!
     * <p>
     * This class shall never build from this constructor directly.
     * You shall always use ResourceManager.fetchResource functions to get this instance.
     *
     * @param resourceManager resource Manager
     * @param fullResourceURI full Resource URI
     * @see ResourceManager#fetchResourceWithShortenURI(Class, String)
     */
    public AbstractResource(ResourceManager resourceManager, String fullResourceURI) {
        this.resourceManager = resourceManager;
        this.fullResourceURI = fullResourceURI;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void bind(int sampler) {
        this.load();
    }


    /**
     * <p>load.</p>
     */
    public void load() {
        this.setLastUsedFrameIndex(this.getResourceManager().getGameManager().getNowFrameIndex());
        if (this.isInMemory()) {
            return;
        }
        this.forceLoad();
        this.getResourceManager().load(this);

        LOGGER.debug("loadResource {}, time {}, memory {}",
                this.getFullResourceURI(), this.getLastUsedFrameIndex(),
                this.getMemorySize());
        if (this.getMemorySize() == 0) {
            LOGGER.warn("this.memorySize shows 0 here. potential track error?");
        }
        this.setInMemory(true);
    }

    /**
     * <p>reload.</p>
     */
    public void reload() {
        this.close();
        this.load();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() {
        if (!this.isInMemory()) {
            return;
        }
        this.forceClose();
        this.getResourceManager().close(this);

        LOGGER.debug("closeResource {}, time {}, memory {}",
                this.getFullResourceURI(), this.getLastUsedFrameIndex(),
                this.getMemorySize());
//        this.inMemory = false;
    }

    /**
     * <p>fetchResourceWithShortenURI.</p>
     *
     * @param shortenResourceURI a {@link java.lang.String} object.
     * @return a {@link com.xenoamess.cyan_potion.base.memory.AbstractResource} object.
     */
    public AbstractResource fetchResourceWithShortenURI(String shortenResourceURI) {
        return this.getResourceManager().fetchResourceWithShortenURI(this.getClass(),
                shortenResourceURI);
    }

    /**
     * force to reload this resource.
     * using loaders registered in this.getResourceManager() .
     *
     * @see ResourceManager#fetchResourceWithShortenURI(Class, String)
     */
    public void forceLoad() {
        final String[] resourceFileURIStrings =
                this.getFullResourceURI().split(":");
        final String resourceFilePath = resourceFileURIStrings[1];
        final String resourceType = resourceFileURIStrings[2];

        Function loader =
                this.getResourceManager().getResourceLoader(this.getClass(), resourceType);
        if (loader == null) {
            throw new URITypeNotDefinedException(this.getFullResourceURI());
        }
        loader.apply(this);
    }

    /**
     * <p>forceClose.</p>
     */
    protected abstract void forceClose();


    /**
     * <p>Getter for the field <code>resourceManager</code>.</p>
     *
     * @return a {@link com.xenoamess.cyan_potion.base.memory.ResourceManager} object.
     */
    public ResourceManager getResourceManager() {
        return this.resourceManager;
    }

    /**
     * <p>Getter for the field <code>fullResourceURI</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getFullResourceURI() {
        return fullResourceURI;
    }

    /**
     * <p>Getter for the field <code>memorySize</code>.</p>
     *
     * @return a long.
     */
    public long getMemorySize() {
        return memorySize;
    }

    /**
     * <p>Setter for the field <code>memorySize</code>.</p>
     *
     * @param memorySize a long.
     */
    public void setMemorySize(long memorySize) {
        this.memorySize = memorySize;
    }

    /**
     * <p>isInMemory.</p>
     *
     * @return a boolean.
     */
    public boolean isInMemory() {
        return inMemory.get();
    }

    /**
     * <p>Setter for the field <code>inMemory</code>.</p>
     *
     * @param inMemory a boolean.
     */
    public void setInMemory(boolean inMemory) {
        this.inMemory.set(inMemory);
    }

    /**
     * <p>Getter for the field <code>lastUsedFrameIndex</code>.</p>
     *
     * @return a long.
     */
    public long getLastUsedFrameIndex() {
        return lastUsedFrameIndex;
    }

    /**
     * <p>Setter for the field <code>lastUsedFrameIndex</code>.</p>
     *
     * @param lastUsedFrameIndex a long.
     */
    public void setLastUsedFrameIndex(long lastUsedFrameIndex) {
        this.lastUsedFrameIndex = lastUsedFrameIndex;
    }

}
