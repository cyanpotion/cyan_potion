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

import com.xenoamess.cyan_potion.base.exceptions.URITypeNotDefinedException;
import com.xenoamess.cyan_potion.base.render.Bindable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.ScheduledExecutorService;
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
    private final ResourceInfo resourceInfo;
    private long memorySize;
    private final AtomicBoolean inMemory = new AtomicBoolean(false);
    private long lastUsedFrameIndex;

    /**
     * get ScheduledExecutorService from gameManager
     *
     * @return return
     */
    private ScheduledExecutorService getScheduledExecutorService() {
        return this.getResourceManager().getGameManager().getScheduledExecutorService();
    }

//    class ResourceLoadFutureTask extends FutureTask<Void> {
//        public ResourceLoadFutureTask() {
//            super(
//                    () -> {
//                        AbstractResource.this.load();
//                        return null;
//                    }
//            );
//        }
//    }

    private FutureTask<Boolean> loadTask;

    /**
     * <p>createNewLoadTask.</p>
     */
    protected synchronized void createNewLoadTask() {
        loadTask = new FutureTask<>(AbstractResource.this::loadByLoadTaskOrSelf);
    }

    /**
     * <p>Getter for the field <code>loadTask</code>.</p>
     *
     * @return a {@link java.util.concurrent.FutureTask} object.
     */
    protected synchronized FutureTask<Boolean> getLoadTask() {
        return this.loadTask;
    }

    /**
     * <p>destroyLoadTask.</p>
     */
    protected synchronized void destroyLoadTask() {
        this.loadTask = null;
    }

    /**
     * !!!NOTICE!!!
     * <p>
     * This class shall never build from this constructor directly.
     * You shall always use ResourceManager.fetchResource functions to get this instance.
     *
     * @param resourceManager resource Manager
     * @param resourceInfo    Resource Json
     * @see ResourceManager#fetchResource(Class, ResourceInfo)
     */
    public AbstractResource(ResourceManager resourceManager, ResourceInfo resourceInfo) {
        this.resourceManager = resourceManager;
        this.resourceInfo = resourceInfo;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void bind(int sampler) {
        this.load();
    }

    /**
     * start loading this AbstractResource
     * if the AbstractResource is in memory now, do nothing.
     * if the AbstractResource is already being loaded, do nothing.
     *
     * @return if this function start loading now, then true.
     * if this function did not start the loading, then false.
     */
    public synchronized boolean startLoad() {
        if (this.inMemory.get()) {
            return false;
        }
        if (this.getLoadTask() != null) {
            return false;
        }
        this.createNewLoadTask();
        getScheduledExecutorService().execute(this.getLoadTask());
        return true;
    }

    /**
     * will wait until load completed(make sure be loaded).
     *
     * @return after the function, if this Resource loaded correctly and is in memory, then true.
     * otherwise if cannot load correctly, then false.
     */
    public synchronized boolean load() {
        boolean result;
        if (startLoad()) {
            try {
                result = this.getLoadTask().get();
            } catch (InterruptedException | ExecutionException e) {
                result = false;
                LOGGER.debug("load resource by load task failed! Resource:{}", this, e);
            }
            if (!result) {
                result = this.loadByLoadTaskOrSelf();
                if (!result) {
                    LOGGER.debug("load resource by self failed! Resource:{}", this);
                }
            }
        } else {
            result = true;
        }
        this.destroyLoadTask();
        return result;
    }

    /**
     * load this Resource.
     * shall only be invoked by this.loadTask.
     *
     * @return a boolean.
     */
    protected boolean loadByLoadTaskOrSelf() {
        this.setLastUsedFrameIndex(this.getResourceManager().getGameManager().getNowFrameIndex());
        if (this.isInMemory()) {
            return true;
        }
        if (!this.forceLoad()) {
            return false;
        }
        this.getResourceManager().load(this);

        LOGGER.debug("loadResource {}, time {}, memory {}",
                this.getResourceInfo(), this.getLastUsedFrameIndex(),
                this.getMemorySize());
        if (this.getMemorySize() == 0) {
            LOGGER.warn("this.memorySize shows 0 here. potential track error? : {}", this.resourceInfo);
        }
        this.setInMemory(true);

        return true;
    }

    /**
     * <p>reload.</p>
     */
    public synchronized void reload() {
        this.close();
        this.load();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void close() {
        if (!this.isInMemory()) {
            return;
        }
        this.forceClose();
        this.getResourceManager().close(this);

        LOGGER.debug("closeResource {}, time {}, memory {}",
                this.getResourceInfo(), this.getLastUsedFrameIndex(),
                this.getMemorySize());
//        this.inMemory = false;
    }

    /**
     * force to reload this resource.
     * using loaders registered in this.getResourceManager() .
     *
     * @return a boolean.
     * @see ResourceManager#fetchResource(Class, ResourceInfo)
     */
    protected boolean forceLoad() {
        Function<AbstractResource, Boolean> loader = (Function) this.getResourceManager().getResourceLoader(this.getClass(), this.getResourceInfo().type);
        if (loader == null) {
            throw new URITypeNotDefinedException(this.getResourceInfo());
        }
        return loader.apply(this);
    }

    /**
     * <p>forceClose.</p>
     */
    protected abstract void forceClose();

    /**
     * <p>Getter for the field <code>resourceManager</code>.</p>
     *
     * @return return
     */
    public ResourceManager getResourceManager() {
        return this.resourceManager;
    }

    /**
     * <p>Getter for the field <code>resourceJson</code>.</p>
     *
     * @return return
     */
    public ResourceInfo getResourceInfo() {
        return resourceInfo;
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

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return this.getResourceInfo() == null ? "null" : this.getResourceInfo().toString();
    }

}
