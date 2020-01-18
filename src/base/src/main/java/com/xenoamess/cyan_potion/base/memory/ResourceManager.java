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
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.impl.StandardFileSystemManager;
import org.lwjgl.opengl.GL11;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import static org.lwjgl.opengl.GL11.glGetIntegerv;

/**
 * //TODO
 * This class is not stable yet.
 * Will be removed or modified in future.
 *
 * @author XenoAmess
 * @version 0.143.0
 */
public class ResourceManager implements AutoCloseable {
    private static final Logger LOGGER =
            LoggerFactory.getLogger(ResourceManager.class);

    private long maxTextureSize = 0;

    private static final StandardFileSystemManager fileSystemManager = new org.apache.commons.vfs2.impl.StandardFileSystemManager();

    {
        fileSystemManager.setLogger(null);
        try {
            fileSystemManager.init();
        } catch (FileSystemException e) {
            e.printStackTrace();
        }
    }

    public long getMaxTextureSize() {
        if (maxTextureSize == 0) {
            int[] maxTextureSizeArray = new int[1];
            glGetIntegerv(GL11.GL_MAX_TEXTURE_SIZE, maxTextureSizeArray);
            maxTextureSize = 1L * maxTextureSizeArray[0] * maxTextureSizeArray[0];
        }
        return maxTextureSize;
    }

    public void setMaxTextureSize(long maxTextureSize) {
        this.maxTextureSize = maxTextureSize;
    }

    /**
     * Constant <code>TOTAL_MEMORY_SIZE_LIMIT_POINT=8L * 1024 * 1024 * 1024</code>
     */
    public static final long TOTAL_MEMORY_SIZE_LIMIT_POINT =
            8L * 1024 * 1024 * 1024;
    /**
     * Constant <code>TOTAL_MEMORY_SIZE_START_POINT=6L * 1024 * 1024 * 1024</code>
     */
    public static final long TOTAL_MEMORY_SIZE_START_POINT =
            6L * 1024 * 1024 * 1024;
    /**
     * Constant <code>TOTAL_MEMORY_SIZE_DIST_POINT=2L * 1024 * 1024 * 1024</code>
     */
    public static final long TOTAL_MEMORY_SIZE_DIST_POINT =
            2L * 1024 * 1024 * 1024;

    private GameManager gameManager;
    private long totalMemorySize = 0;
    private final ArrayList<AbstractResource> inMemoryResources = new ArrayList<>();
    private final ConcurrentHashMap<Class, ConcurrentHashMap> defaultResourcesURIMap = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Class, ConcurrentHashMap> defaultResourcesLoaderMap = new ConcurrentHashMap<>();

//    FileSystemManager fsManager = VFS.getManager();

    /**
     * <p>putResourceLoader.</p>
     *
     * @param tClass       a {@link java.lang.Class} object.
     * @param resourceType resourceType
     * @param loader       a {@link java.util.function.Function} object.
     */
    public <T> void putResourceLoader(Class<T> tClass, String resourceType, Function<T, Void> loader) {
        ConcurrentHashMap<String, Function<T, Void>> resourceLoaderMap =
                defaultResourcesLoaderMap.computeIfAbsent(tClass, aClass -> new ConcurrentHashMap<>(8));
        resourceLoaderMap.put(resourceType, loader);
    }

    /**
     * <p>getResourceLoader.</p>
     *
     * @param tClass       a {@link java.lang.Class} object.
     * @param resourceType resourceType
     * @return return
     */
    public <T> Function<T, Void> getResourceLoader(Class<T> tClass, String resourceType) {
        ConcurrentHashMap<String, Function<T, Void>> resourceLoaderMap =
                defaultResourcesLoaderMap.get(tClass);
        if (resourceLoaderMap == null) {
            return null;
        }
        return resourceLoaderMap.get(resourceType);
    }

    /**
     * <p>putResourceWithShortenURI.</p>
     *
     * @param resourceInfo resourceInfo
     * @param t            a T object.
     */
    public <T> void putResource(ResourceInfo resourceInfo, T t) {
        ConcurrentHashMap<ResourceInfo, T> resourceURIMap =
                getDefaultResourcesURIMap().get(t.getClass());
        if (resourceURIMap == null) {
            resourceURIMap = new ConcurrentHashMap<>(100);
            getDefaultResourcesURIMap().put(t.getClass(), resourceURIMap);
        }
        resourceURIMap.put(resourceInfo, t);
    }

    /**
     * <p>getResourceFromShortenURI.</p>
     *
     * @param tClass       a {@link java.lang.Class} object.
     * @param resourceInfo resourceInfo
     * @return a T object.
     */
    public <T> T getResource(Class<T> tClass,
                             ResourceInfo resourceInfo) {

        ConcurrentHashMap<ResourceInfo, T> resourceURIMap =
                getDefaultResourcesURIMap().get(tClass);
        if (resourceURIMap == null) {
            return null;
        } else {
            return resourceURIMap.get(resourceInfo);
        }
    }

    /**
     * <p>ifExistResourceFromShortenURI.</p>
     *
     * @param resourceInfo resourceInfo
     * @return a boolean.
     */
    public boolean ifExistResource(ResourceInfo resourceInfo) {

        ConcurrentHashMap resourceURIMap =
                getDefaultResourcesURIMap().get(resourceInfo.getClass());
        if (resourceURIMap == null) {
            return false;
        } else {
            return resourceURIMap.containsKey(resourceInfo);
        }
    }

    /**
     * <p>fetchResourceWithShortenURI.</p>
     *
     * @param tClass       a {@link java.lang.Class} object.
     * @param resourceInfo resourceInfo
     * @return a T object.
     */
    public <T extends AbstractResource> T fetchResource(Class<T> tClass, ResourceInfo resourceInfo) {
        T res = null;
        if (this.ifExistResource(resourceInfo)) {
            res = this.getResource(tClass, resourceInfo);
        } else {
            try {
                res = tClass.getDeclaredConstructor(ResourceManager.class,
                        String.class).newInstance(this,
                        resourceInfo);
                this.putResource(resourceInfo, res);
            } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
                LOGGER.debug("ResourceManager.fetchResourceWithShortenURI(Class<T> tClass, ResourceInfo resourceInfo)" +
                        " fails:{},{}", tClass, resourceInfo, e);
            }
        }
        return res;
    }


    /**
     * <p>fetchResourceWithShortenURI.</p>
     *
     * @param tClass a {@link java.lang.Class} object.
     * @return a T object.
     */
    public <T extends AbstractResource> T fetchResource(Class<T> tClass, String type, FileObject fileObject, String... values) {
        ResourceInfo resourceInfo = new ResourceInfo(tClass, type, fileObject, values);
        return fetchResource(tClass, resourceInfo);
    }

    /**
     * <p>fetchResourceWithShortenURI.</p>
     *
     * @param tClass a {@link java.lang.Class} object.
     * @return a T object.
     */
    public <T extends AbstractResource> T fetchResource(Class<T> tClass, String type, String fileObjectString, String... values) {
        ResourceInfo resourceInfo = new ResourceInfo(tClass, type, fileObjectString, values);
        return fetchResource(tClass, resourceInfo);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() {
        closeMap(getDefaultResourcesURIMap());
    }

    /**
     * <p>closeMap.</p>
     *
     * @param mapToClose mapToClose
     */
    public static void closeMap(Map mapToClose) {
        if (mapToClose == null) {
            return;
        }
        for (Object object : mapToClose.values()) {
            if (object instanceof Map) {
                closeMap((Map) object);
            }
            if (object instanceof AutoCloseable) {
                try {
                    ((AutoCloseable) object).close();
                } catch (Exception e) {
                    LOGGER.error("close fails:{}", object, e);
                }
            }
        }
    }

    /**
     * <p>Constructor for ResourceManager.</p>
     *
     * @param gameManager gameManager
     */
    public ResourceManager(GameManager gameManager) {
        this.setGameManager(gameManager);
    }

    /**
     * <p>load.</p>
     *
     * @param resource resource
     */
    public void load(AbstractResource resource) {
        if (resource.isInMemory()) {
            return;
        }
        setTotalMemorySize(getTotalMemorySize() + resource.getMemorySize());
        getInMemoryResources().add(resource);
        resource.setInMemory(true);
    }

    /**
     * <p>close.</p>
     *
     * @param resource resource
     */
    public void close(AbstractResource resource) {
        if (!resource.isInMemory()) {
            return;
        }
        setTotalMemorySize(getTotalMemorySize() - resource.getMemorySize());
        resource.setInMemory(false);
    }


    /**
     * <p>suggestGc.</p>
     */
    public void suggestGc() {
        if (this.getGameManager().getNowFrameIndex() % 1000 == 0) {
            LOGGER.debug("suggestGc at totalMemorySize : {}", this.getTotalMemorySize());
        }
        if (this.getTotalMemorySize() <= TOTAL_MEMORY_SIZE_START_POINT) {
            return;
        }

        LOGGER.debug("apply gc");
        this.forceGc();
        LOGGER.debug("after forceGc totalMemorySize changed to : {}", this.getTotalMemorySize());
    }

    /**
     * <p>forceGc.</p>
     */
    public void forceGc() {
        getInMemoryResources().sort(Comparator.comparingLong(AbstractResource::getLastUsedFrameIndex));
        ArrayList<AbstractResource> newInMemoryResources = new ArrayList<>();
        for (AbstractResource nowResource : getInMemoryResources()) {
            if (nowResource.isInMemory()) {
                if (this.getTotalMemorySize() <= TOTAL_MEMORY_SIZE_DIST_POINT) {
                    newInMemoryResources.add(nowResource);
                    continue;
                }
                if (this.getTotalMemorySize() <= TOTAL_MEMORY_SIZE_LIMIT_POINT) {
                    if (this.getGameManager().getNowFrameIndex() - nowResource.getLastUsedFrameIndex() < 3000) {
                        newInMemoryResources.add(nowResource);
                    } else {
                        nowResource.close();
                    }
                    continue;
                }
                if (this.getGameManager().getNowFrameIndex() - nowResource.getLastUsedFrameIndex() < 50) {
                    newInMemoryResources.add(nowResource);
                } else {
                    nowResource.close();
                }
            }
        }
        this.getInMemoryResources().clear();
        this.getInMemoryResources().addAll(newInMemoryResources);
    }


    //getters and setters

    /**
     * <p>Getter for the field <code>gameManager</code>.</p>
     *
     * @return return
     */
    public GameManager getGameManager() {
        return gameManager;
    }

    /**
     * <p>Setter for the field <code>gameManager</code>.</p>
     *
     * @param gameManager gameManager
     */
    public void setGameManager(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    /**
     * <p>Getter for the field <code>totalMemorySize</code>.</p>
     *
     * @return a long.
     */
    public long getTotalMemorySize() {
        return totalMemorySize;
    }

    /**
     * <p>Setter for the field <code>totalMemorySize</code>.</p>
     *
     * @param totalMemorySize a long.
     */
    public void setTotalMemorySize(long totalMemorySize) {
        this.totalMemorySize = totalMemorySize;
    }

    /**
     * <p>Getter for the field <code>inMemoryResources</code>.</p>
     *
     * @return return
     */
    public ArrayList<AbstractResource> getInMemoryResources() {
        return inMemoryResources;
    }

    /**
     * <p>Getter for the field <code>defaultResourcesURIMap</code>.</p>
     *
     * @return return
     */
    public ConcurrentHashMap<Class, ConcurrentHashMap> getDefaultResourcesURIMap() {
        return defaultResourcesURIMap;
    }

    public static FileSystemManager getFileSystemManager() {
        return fileSystemManager;
    }
}
