package com.xenoamess.cyan_potion.base.memory;

import com.xenoamess.cyan_potion.base.GameManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author XenoAmess
 */
public abstract class AbstractResource implements AutoCloseable {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractResource.class);

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

        LOGGER.debug("loadResource {}, time {}, memory {}", this.getFullResourceURI(), this.getLastUsedFrameIndex(), this.getMemorySize());
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

        LOGGER.debug("closeResource {}, time {}, memory {}", this.getFullResourceURI(), this.getLastUsedFrameIndex(), this.getMemorySize());
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
