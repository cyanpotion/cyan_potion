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
import com.xenoamess.cyan_potion.base.DataCenter;
import com.xenoamess.cyan_potion.base.GameManager;
import com.xenoamess.cyan_potion.base.SubManager;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.io.IOUtils;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
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
import java.util.function.Predicate;

import static org.lwjgl.opengl.GL11.glGetIntegerv;

/**
 * ResourceManager
 * manager of resources.
 *
 * @author XenoAmess
 * @version 0.162.3
 */
@SuppressWarnings("rawtypes")
@EqualsAndHashCode(callSuper = true)
@ToString
public class ResourceManager extends SubManager {
    @JsonIgnore
    private static final transient Logger LOGGER =
            LoggerFactory.getLogger(ResourceManager.class);

    private long maxTextureSize = 0;

    @Getter
    private static final StandardFileSystemManager FILE_SYSTEM_MANAGER = loadFileSystemManager();

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
            result = getFILE_SYSTEM_MANAGER().resolveFile(fileString);
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

    @Getter
    private final GameManager gameManager;

    @Getter
    @Setter
    private long totalMemorySize = 0;

    @Getter
    private final ArrayList<AbstractResource> inMemoryResources = new ArrayList<>();

    @Getter
    private final ConcurrentHashMap<Class<? extends AbstractResource>, ConcurrentHashMap<ResourceInfo<?
            extends AbstractResource>, ? extends AbstractResource>> defaultResourcesURIMap = new ConcurrentHashMap<>();

    @Getter
    private final ConcurrentHashMap<Class<? extends AbstractResource>, ConcurrentHashMap<String, Predicate<?
            extends AbstractResource>>> defaultResourcesLoaderMap = new ConcurrentHashMap<>();

    /**
     * <p>defaultResourcesURIMapGet.</p>
     *
     * @param <T>    resource class
     * @param tClass resource class
     * @return a {@link java.util.concurrent.ConcurrentHashMap} object.
     */
    @SuppressWarnings("unchecked")
    protected <T extends AbstractResource> ConcurrentHashMap<ResourceInfo<T>, T> defaultResourcesURIMapGet(Class<T> tClass) {
        return (ConcurrentHashMap) defaultResourcesURIMap.get(tClass);
    }

    /**
     * <p>defaultResourcesURIMapPut.</p>
     *
     * @param <T>    resource class
     * @param tClass resource class
     * @param map    a {@link java.util.concurrent.ConcurrentHashMap} object.
     * @return a {@link java.util.concurrent.ConcurrentHashMap} object.
     */
    @SuppressWarnings({"unchecked", "UnusedReturnValue"})
    protected <T extends AbstractResource> ConcurrentHashMap<ResourceInfo<T>, T> defaultResourcesURIMapPut(Class<T> tClass, ConcurrentHashMap<ResourceInfo<T>, T> map) {
        return defaultResourcesURIMap.put(tClass, (ConcurrentHashMap) map);
    }

    /**
     * <p>defaultResourcesURIMapContainsKey.</p>
     *
     * @param <T>    resource class
     * @param tClass resource class
     * @return a boolean.
     */
    @SuppressWarnings("unused")
    protected <T extends AbstractResource> boolean defaultResourcesURIMapContainsKey(Class<T> tClass) {
        return defaultResourcesURIMap.containsKey(tClass);
    }

    /**
     * <p>defaultResourcesLoaderMapGet.</p>
     *
     * @param <T>    resource class
     * @param tClass resource class
     * @return a {@link java.util.concurrent.ConcurrentHashMap} object.
     */
    @SuppressWarnings("unchecked")
    protected <T extends AbstractResource> ConcurrentHashMap<String, Predicate<T>> defaultResourcesLoaderMapGet(Class<T> tClass) {
        return (ConcurrentHashMap) defaultResourcesLoaderMap.get(tClass);
    }

    /**
     * <p>defaultResourcesLoaderMapPut.</p>
     *
     * @param <T>    resource class
     * @param tClass resource class
     * @param map    a {@link java.util.concurrent.ConcurrentHashMap} object.
     * @return a {@link java.util.concurrent.ConcurrentHashMap} object.
     */
    @SuppressWarnings({"unused", "unchecked"})
    protected <T extends AbstractResource> ConcurrentHashMap<String, Predicate<T>> defaultResourcesLoaderMapPut(Class<T> tClass, ConcurrentHashMap<String, Function<T, Boolean>> map) {
        return (ConcurrentHashMap) defaultResourcesLoaderMap.put(tClass, (ConcurrentHashMap) map);
    }

    /**
     * <p>defaultResourcesLoaderMapContainsKey.</p>
     *
     * @param <T>    resource class
     * @param tClass resource class
     * @return a boolean.
     */
    @SuppressWarnings("unused")
    protected <T extends AbstractResource> boolean defaultResourcesLoaderMapContainsKey(Class<T> tClass) {
        return defaultResourcesLoaderMap.containsKey(tClass);
    }

    /**
     * <p>putResourceLoader.</p>
     *
     * @param <T>          resource class
     * @param tClass       resource class
     * @param resourceType resourceType
     * @param loader       a {@link java.util.function.Function} object.
     */
    public <T extends AbstractResource> void putResourceLoader(
            Class<T> tClass,
            String resourceType,
            Predicate<T> loader
    ) {
        ConcurrentHashMap<String, Predicate<? extends AbstractResource>> resourceLoaderMap =
                defaultResourcesLoaderMap.computeIfAbsent(
                        tClass, aClass -> new ConcurrentHashMap<>(8));
        resourceLoaderMap.put(resourceType, loader);
    }

    /**
     * <p>getResourceLoader.</p>
     *
     * @param <T>          resource class
     * @param tClass       resource class
     * @param resourceType resourceType
     * @return return
     */
    public <T extends AbstractResource> Predicate<T> getResourceLoader(Class<T> tClass, String resourceType) {
        ConcurrentHashMap<String, Predicate<T>> resourceLoaderMap =
                defaultResourcesLoaderMapGet(tClass);
        if (resourceLoaderMap == null) {
            return null;
        }
        return resourceLoaderMap.get(resourceType);
    }

    /**
     * <p>putResourceWithShortenURI.</p>
     *
     * @param <T>          resource class
     * @param resourceInfo resourceInfo
     * @param t            resource
     */
    @SuppressWarnings("unchecked")
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
     * @param <T>              resource class
     * @param resourceInfoJson resourceInfo
     * @param t                resource
     */
    @SuppressWarnings({"unused", "unchecked"})
    public <T extends AbstractResource> void putResource(String resourceInfoJson, T t) {
        this.putResource((ResourceInfo<T>) ResourceInfo.of(resourceInfoJson), t);
    }

    /**
     * <p>getResourceFromShortenURI.</p>
     *
     * @param <T>          resource class
     * @param tClass       resource class
     * @param resourceInfo resourceInfo
     * @return resource
     */
    public <T extends AbstractResource> T getResource(
            Class<T> tClass,
            ResourceInfo<T> resourceInfo
    ) {
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
     * @param <T>          resource class
     * @param resourceInfo resourceInfo
     * @return resource
     */
    @SuppressWarnings("unused")
    public <T extends AbstractResource> T getResource(ResourceInfo<T> resourceInfo) {
        return this.getResource(resourceInfo.getResourceClass(), resourceInfo);
    }

    /**
     * <p>getResourceFromShortenURI.</p>
     *
     * @param <T>              resource class
     * @param tClass           resource class
     * @param resourceInfoJson resourceInfo
     * @return resource
     */
    @SuppressWarnings({"unused", "unchecked"})
    public <T extends AbstractResource> T getResource(Class<T> tClass,
                                                      String resourceInfoJson) {
        return this.getResource(tClass, (ResourceInfo<T>) ResourceInfo.of(resourceInfoJson));
    }

    /**
     * <p>ifExistResourceFromShortenURI.</p>
     *
     * @param <T>          resource class
     * @param resourceInfo resourceInfo
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
    @SuppressWarnings({"unused", "unchecked"})
    public boolean ifExistResource(String resourceInfoJson) {
        return this.ifExistResource(ResourceInfo.of(resourceInfoJson));
    }


    /**
     * <p>fetchResource.</p>
     *
     * @param <T>          resource class
     * @param tClass       resource class
     * @param resourceInfo resourceInfo
     * @return resource
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
     * @param <T>          resource class
     * @param resourceInfo resourceInfo
     * @return resource
     */
    public <T extends AbstractResource> T fetchResource(ResourceInfo<T> resourceInfo) {
        return this.fetchResource(resourceInfo.getResourceClass(), resourceInfo);
    }

    /**
     * <p>fetchResource.</p>
     *
     * @param <T>              resource class
     * @param tClass           resource class
     * @param resourceInfoJson resource Info Json String
     * @return resource
     */
    @SuppressWarnings("unchecked")
    public <T extends AbstractResource> T fetchResource(Class<T> tClass, String resourceInfoJson) {
        return this.fetchResource(tClass, (ResourceInfo<T>) ResourceInfo.of(resourceInfoJson));
    }

    /**
     * <p>fetchResource.</p>
     *
     * @param <T>              resource class
     * @param tClass           resource class
     * @param type             a {@link java.lang.String} object.
     * @param fileObjectString a {@link java.lang.String} object.
     * @param values           a {@link java.lang.String} object.
     * @return resource
     */
    public <T extends AbstractResource> T fetchResource(Class<T> tClass, String type, String fileObjectString,
                                                        String... values) {
        ResourceInfo<T> resourceInfo = ResourceInfo.of(tClass, type, fileObjectString, values);
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
     * {@inheritDoc}
     */
    @Override
    public void init() {
        //do nothing
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unused")
    @Override
    public void update() {
        //do nothing
    }

    /**
     * <p>load.</p>
     *
     * @param resource resource
     */
    public void load(NormalResource resource) {
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
    public void close(NormalResource resource) {
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
}
