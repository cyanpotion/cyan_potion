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

import com.xenoamess.cyan_potion.base.GameManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author XenoAmess
 */
public abstract class AbstractResource implements AutoCloseable {
    private static final Logger LOGGER =
            LoggerFactory.getLogger(AbstractResource.class);

    private final GameManager gameManager;
    private final String fullResourceURI;
    private long memorySize;
    private boolean inMemory = false;
    private long lastUsedFrameIndex;

    public AbstractResource(GameManager gameManager, String fullResourceURI) {
        this.gameManager = gameManager;
        this.fullResourceURI = fullResourceURI;
    }


    public void load() {
        this.setLastUsedFrameIndex(this.getGameManager().getNowFrameIndex());
        if (this.isInMemory()) {
            return;
        }
        this.forceLoad();
        this.getGameManager().getResourceManager().load(this);

        LOGGER.debug("loadResource {}, time {}, memory {}",
                this.getFullResourceURI(), this.getLastUsedFrameIndex(),
                this.getMemorySize());
        if (this.getMemorySize() == 0) {
            LOGGER.warn("this.memorySize shows 0 here. potential track error?");
        }
//        this.inMemory = true;
    }

    public void reload() {
        this.close();
        this.load();
    }

    @Override
    public void close() {
        if (!this.isInMemory()) {
            return;
        }
        this.forceClose();
        this.getGameManager().getResourceManager().close(this);

        LOGGER.debug("closeResource {}, time {}, memory {}",
                this.getFullResourceURI(), this.getLastUsedFrameIndex(),
                this.getMemorySize());
//        this.inMemory = false;
    }

    public AbstractResource fetchResourceWithShortenURI(String shortenResourceURI) {
        return this.getGameManager().getResourceManager().fetchResourceWithShortenURI(this.getClass(), shortenResourceURI);
    }

    protected abstract void forceLoad();

    protected abstract void forceClose();


    public GameManager getGameManager() {
        return gameManager;
    }

    public String getFullResourceURI() {
        return fullResourceURI;
    }

    public long getMemorySize() {
        return memorySize;
    }

    public void setMemorySize(long memorySize) {
        this.memorySize = memorySize;
    }

    public boolean isInMemory() {
        return inMemory;
    }

    public void setInMemory(boolean inMemory) {
        this.inMemory = inMemory;
    }

    public long getLastUsedFrameIndex() {
        return lastUsedFrameIndex;
    }

    public void setLastUsedFrameIndex(long lastUsedFrameIndex) {
        this.lastUsedFrameIndex = lastUsedFrameIndex;
    }

}
