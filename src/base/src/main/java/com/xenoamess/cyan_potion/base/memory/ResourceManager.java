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
import com.xenoamess.commons.io.FileUtils;
import com.xenoamess.cyan_potion.base.DataCenter;
import com.xenoamess.cyan_potion.base.GameManager;
import com.xenoamess.cyan_potion.base.SubManager;
import org.apache.commons.io.IOUtils;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.impl.StandardFileSystemManager;
import org.lwjgl.opengl.GL11;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import static org.lwjgl.opengl.GL11.glGetIntegerv;

/**
 * ResourceManager
 * manager of resources.
 *
 * @author XenoAmess
 * @version 0.154.2-SNAPSHOT
 */
public class ResourceManager extends SubManager {
    @JsonIgnore
    private static transient final Logger LOGGER =
            LoggerFactory.getLogger(ResourceManager.class);

    private long maxTextureSize = 0;

    private static final StandardFileSystemManager fileSystemManager = loadFileSystemManager();

    private static StandardFileSystemManager loadFileSystemManager() {
        StandardFileSystemManager fileSystemManager = new org.apache.commons.vfs2.impl.StandardFileSystemManager();
        fileSystemManager.setLogger(null);
        try {
            fileSystemManager.init();
            fileSystemManager.setBaseFile(new File(System.getProperty("user.dir")));
        } catch (FileSystemException e) {
            LOGGER.error("cannot init ResourceManager.fileSystemManager", e);
        }
        return fileSystemManager;
    }

    /**
     * get FileObject from a url string.
     *
     * @param fileString string to the file.
     * @return a {@link org.apache.commons.vfs2.FileObject} object.
     */
    public static FileObject resolveFile(String fileString) {
        FileObject result = null;
        try {
            result = getFileSystemManager().resolveFile(fileString);
        } catch (FileSystemException e) {
            LOGGER.error("resolveFile(String fileString) fails: {}", fileString, e);
        }
        return result;
    }

    /**
     * load the file
     * and get its content as string
     * and return the string.
     *
     * @param fileString string to the file.
     * @return content of the file as string.
     */
    public static String loadString(String fileString) {
        return loadString(resolveFile(fileString));
    }

    /**
     * load the file
     * and get its content as string
     * and return the string.
     *
     * @param fileObject a {@link org.apache.commons.vfs2.FileObject} object.
     * @return content of the file as string.
     */
    public static String loadString(FileObject fileObject) {
        String result = "";
        if (fileObject == null) {
            return result;
        }
        try (InputStream inputStream = fileObject.getContent().getInputStream()) {
            result = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
        } catch (IOException e) {
            LOGGER.error("loadString(FileObject fileObject) fails: {}", fileObject, e);
        }
        return result;
    }

    /**
     * generate a File object from a fileObject object.
     * I had discussed with vfs's guys and they told me they might add a function to do this.
     * I might delete this then.
     * But this is useful for now.
     * You know sometimes we just need a function like this for something in Swing to work,
     * for example JFileChooser.
     *
     * @param fileObject a {@link org.apache.commons.vfs2.FileObject} object.
     * @return a {@link java.io.File} object.
     */
    public static File toFile(FileObject fileObject) {
        File result = null;
        try {
            result = FileUtils.toFile(fileObject);
        } catch (FileSystemException e) {
            LOGGER.error("this FileObject cannot be transformed to a File", e);
        }
        return result;
    }

    /**
     * <p>Getter for the field <code>maxTextureSize</code>.</p>
     *
     * @return a long.
     */
    public long getMaxTextureSize() {
        if (maxTextureSize == 0) {
            if (DataCenter.ifMainThread()) {
                int[] maxTextureSizeArray = new int[1];
                glGetIntegerv(GL11.GL_MAX_TEXTURE_SIZE, maxTextureSizeArray);
                maxTextureSize = 1L * maxTextureSizeArray[0] * maxTextureSizeArray[0];
                return maxTextureSize;
            } else {
                return Long.MAX_VALUE;
            }
        }
        return maxTextureSize;
    }

    /**
     * <p>Setter for the field <code>maxTextureSize</code>.</p>
     *
     * @param maxTextureSize a long.
     */
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

    private final GameManager gameManager;
    private long totalMemorySize = 0;
    private final ArrayList<AbstractResource> inMemoryResources = new ArrayList<>();
    private final ConcurrentHashMap<Class<? extends AbstractResource>, ConcurrentHashMap<ResourceInfo<? extends AbstractResource>, ? extends AbstractResource>> defaultResourcesURIMap = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Class<? extends AbstractResource>, ConcurrentHashMap<String, Function<? extends AbstractResource, Boolean>>> defaultResourcesLoaderMap = new ConcurrentHashMap<>();

    /**
     * <p>defaultResourcesURIMapGet.</p>
     *
     * @param tClass a {@link java.lang.Class} object.
     * @param <T>    a T object.
     * @return a {@link java.util.concurrent.ConcurrentHashMap} object.
     */
    protected <T extends AbstractResource> ConcurrentHashMap<ResourceInfo<T>, T> defaultResourcesURIMapGet(Class<T> tClass) {
        return (ConcurrentHashMap) defaultResourcesURIMap.get(tClass);
    }

    /**
     * <p>defaultResourcesURIMapPut.</p>
     *
     * @param tClass a {@link java.lang.Class} object.
     * @param map    a {@link java.util.concurrent.ConcurrentHashMap} object.
     * @param <T>    a T object.
     * @return a {@link java.util.concurrent.ConcurrentHashMap} object.
     */
    protected <T extends AbstractResource> ConcurrentHashMap<ResourceInfo<T>, T> defaultResourcesURIMapPut(Class<T> tClass, ConcurrentHashMap<ResourceInfo<T>, T> map) {
        return defaultResourcesURIMap.put(tClass, (ConcurrentHashMap) map);
    }

    /**
     * <p>defaultResourcesURIMapContainsKey.</p>
     *
     * @param tClass a {@link java.lang.Class} object.
     * @param <T>    a T object.
     * @return a boolean.
     */
    protected <T extends AbstractResource> boolean defaultResourcesURIMapContainsKey(Class<T> tClass) {
        return defaultResourcesURIMap.containsKey(tClass);
    }

    /**
     * <p>defaultResourcesLoaderMapGet.</p>
     *
     * @param tClass a {@link java.lang.Class} object.
     * @param <T>    a T object.
     * @return a {@link java.util.concurrent.ConcurrentHashMap} object.
     */
    protected <T extends AbstractResource> ConcurrentHashMap<String, Function<T, Boolean>> defaultResourcesLoaderMapGet(Class<T> tClass) {
        return (ConcurrentHashMap) defaultResourcesLoaderMap.get(tClass);
    }

    /**
     * <p>defaultResourcesLoaderMapPut.</p>
     *
     * @param tClass a {@link java.lang.Class} object.
     * @param map    a {@link java.util.concurrent.ConcurrentHashMap} object.
     * @param <T>    a T object.
     * @return a {@link java.util.concurrent.ConcurrentHashMap} object.
     */
    protected <T extends AbstractResource> ConcurrentHashMap<String, Function<T, Boolean>> defaultResourcesLoaderMapPut(Class<T> tClass, ConcurrentHashMap<String, Function<T, Boolean>> map) {
        return (ConcurrentHashMap) defaultResourcesLoaderMap.put(tClass, (ConcurrentHashMap) map);
    }

    /**
     * <p>defaultResourcesLoaderMapContainsKey.</p>
     *
     * @param tClass a {@link java.lang.Class} object.
     * @param <T>    a T object.
     * @return a boolean.
     */
    protected <T extends AbstractResource> boolean defaultResourcesLoaderMapContainsKey(Class<T> tClass) {
        return defaultResourcesLoaderMap.containsKey(tClass);
    }

    /**
     * <p>putResourceLoader.</p>
     *
     * @param tClass       a {@link java.lang.Class} object.
     * @param resourceType resourceType
     * @param loader       a {@link java.util.function.Function} object.
     * @param <T>          tClass
     */
    public <T extends AbstractResource> void putResourceLoader(Class<T> tClass, String resourceType, Function<T, Boolean> loader) {
        ConcurrentHashMap<String, Function<? extends AbstractResource, Boolean>> resourceLoaderMap =
                defaultResourcesLoaderMap.computeIfAbsent(
                        tClass, aClass -> new ConcurrentHashMap<>(8));
        resourceLoaderMap.put(resourceType, loader);
    }

    /**
     * <p>getResourceLoader.</p>
     *
     * @param tClass       a {@link java.lang.Class} object.
     * @param resourceType resourceType
     * @param <T>          tClass
     * @return return
     */
    public <T extends AbstractResource> Function<T, Boolean> getResourceLoader(Class<T> tClass, String resourceType) {
        ConcurrentHashMap<String, Function<T, Boolean>> resourceLoaderMap =
                defaultResourcesLoaderMapGet(tClass);
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
     * @param <T>          tClass
     */
    public <T extends AbstractResource> void putResource(ResourceInfo<T> resourceInfo, T t) {
        ConcurrentHashMap<ResourceInfo<T>, T> resourceURIMap =
                defaultResourcesURIMapGet((Class<T>) t.getClass());
        if (resourceURIMap == null) {
            resourceURIMap = new ConcurrentHashMap<>(100);
            defaultResourcesURIMapPut((Class<T>) t.getClass(), resourceURIMap);
        }
        resourceURIMap.put(resourceInfo, t);
    }

    /**
     * put resource into efaultResourcesURIMap
     *
     * @param resourceInfoJson resourceInfo
     * @param t                resource
     * @param <T>              resource class
     */
    public <T extends AbstractResource> void putResource(String resourceInfoJson, T t) {
        this.putResource((ResourceInfo<T>) ResourceInfo.of(resourceInfoJson), t);
    }

    /**
     * <p>getResourceFromShortenURI.</p>
     *
     * @param tClass       a {@link java.lang.Class} object.
     * @param resourceInfo resourceInfo
     * @param <T>          a T object.
     * @return a T object.
     */
    public <T extends AbstractResource> T getResource(Class<T> tClass,
                                                      ResourceInfo<T> resourceInfo) {
        assert (tClass == resourceInfo.getResourceClass());
        ConcurrentHashMap<ResourceInfo<T>, T> resourceURIMap =
                defaultResourcesURIMapGet(tClass);
        if (resourceURIMap == null) {
            return null;
        } else {
            return resourceURIMap.get(resourceInfo);
        }
    }

    /**
     * <p>getResourceFromShortenURI.</p>
     *
     * @param resourceInfo resourceInfo
     * @param <T>          resourceInfo class
     * @return a T object.
     */
    public <T extends AbstractResource> T getResource(ResourceInfo<T> resourceInfo) {
        return this.getResource(resourceInfo.getResourceClass(), resourceInfo);
    }

    /**
     * <p>getResourceFromShortenURI.</p>
     *
     * @param tClass           a {@link java.lang.Class} object.
     * @param resourceInfoJson resourceInfo
     * @param <T>              resourceInfo class
     * @return a T object.
     */
    public <T extends AbstractResource> T getResource(Class<T> tClass,
                                                      String resourceInfoJson) {
        return this.getResource(tClass, (ResourceInfo<T>) ResourceInfo.of(resourceInfoJson));
    }

    /**
     * <p>ifExistResourceFromShortenURI.</p>
     *
     * @param resourceInfo resourceInfo
     * @param <T>          resourceInfo class
     * @return a boolean.
     */
    public <T extends AbstractResource> boolean ifExistResource(ResourceInfo<T> resourceInfo) {
        ConcurrentHashMap resourceURIMap =
                defaultResourcesURIMapGet(resourceInfo.getResourceClass());
        if (resourceURIMap == null) {
            return false;
        } else {
            return resourceURIMap.containsKey(resourceInfo);
        }
    }

    /**
     * <p>ifExistResourceFromShortenURI.</p>
     *
     * @param resourceInfoJson resourceInfoJson
     * @return a boolean.
     */
    public boolean ifExistResource(String resourceInfoJson) {
        return this.ifExistResource(ResourceInfo.of(resourceInfoJson));
    }


    /**
     * <p>fetchResource.</p>
     *
     * @param tClass       a {@link java.lang.Class} object.
     * @param resourceInfo resourceInfo
     * @param <T>          resourceInfo class
     * @return a T object.
     */
    public <T extends AbstractResource> T fetchResource(Class<T> tClass, ResourceInfo<T> resourceInfo) {
        assert (tClass == resourceInfo.getResourceClass());
        T res = null;
        if (this.ifExistResource(resourceInfo)) {
            res = this.getResource(tClass, resourceInfo);
        } else {
            try {
                res = tClass.getDeclaredConstructor(ResourceManager.class,
                        resourceInfo.getClass()).newInstance(this,
                        resourceInfo);
                this.putResource(resourceInfo, res);
            } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
                LOGGER.debug("ResourceManager.fetchResource(Class<T> tClass, ResourceInfo resourceInfo)" +
                        " fails:{},{}", tClass, resourceInfo, e);
            }
        }
        return res;
    }

    /**
     * <p>fetchResource.</p>
     *
     * @param resourceInfo resourceInfo
     * @param <T>          resourceInfo class
     * @return a T object.
     */
    public <T extends AbstractResource> T fetchResource(ResourceInfo<T> resourceInfo) {
        return this.fetchResource(resourceInfo.getResourceClass(), resourceInfo);
    }

    /**
     * <p>fetchResource.</p>
     *
     * @param tClass           a {@link java.lang.Class} object.
     * @param resourceInfoJson resource Info Json String
     * @param <T>              resourceInfo class
     * @return a T object.
     */
    public <T extends AbstractResource> T fetchResource(Class<T> tClass, String resourceInfoJson) {
        return this.fetchResource(tClass, (ResourceInfo<T>) ResourceInfo.of(resourceInfoJson));
    }

    /**
     * <p>fetchResource.</p>
     *
     * @param tClass           a {@link java.lang.Class} object.
     * @param type             a {@link java.lang.String} object.
     * @param fileObjectString a {@link java.lang.String} object.
     * @param values           a {@link java.lang.String} object.
     * @param <T>              resource class
     * @return a T object.
     */
    public <T extends AbstractResource> T
    fetchResource(Class<T> tClass, String type, String fileObjectString, String... values) {
        ResourceInfo<T> resourceInfo = new ResourceInfo<>(tClass, type, fileObjectString, values);
        return this.fetchResource(tClass, resourceInfo);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void close() {
        closeMap(defaultResourcesURIMap);
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
            if (object instanceof Closeable) {
                try {
                    ((Closeable) object).close();
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
        super(gameManager);
        this.gameManager = gameManager;
    }

    /**
     * <p>init.</p>
     */
    @Override
    public void init() {
        //do nothing
    }

    @Override
    public void update() {
        //do nothing
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
     * <p>Getter for the field <code>fileSystemManager</code>.</p>
     *
     * @return a {@link org.apache.commons.vfs2.FileSystemManager} object.
     */
    public static FileSystemManager getFileSystemManager() {
        return fileSystemManager;
    }
}
