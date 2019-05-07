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
import com.xenoamess.cyan_potion.base.render.Texture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;

/**
 * @author XenoAmess
 */
public class ResourceManager implements AutoCloseable {
    private static final Logger LOGGER =
            LoggerFactory.getLogger(ResourceManager.class);

    public static final long TOTAL_MEMORY_SIZE_LIMIT_POINT =
            8L * 1024 * 1024 * 1024;
    public static final long TOTAL_MEMORY_SIZE_START_POINT =
            6L * 1024 * 1024 * 1024;
    public static final long TOTAL_MEMORY_SIZE_DIST_POINT =
            2L * 1024 * 1024 * 1024;

    private GameManager gameManager;
    private long totalMemorySize = 0;
    private final ArrayList<AbstractResource> inMemoryResources = new ArrayList<>();
    private final ConcurrentHashMap<Class, ConcurrentHashMap> defaultResourecesURIMap = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Class, ConcurrentHashMap> defaultResourecesLoaderMap = new ConcurrentHashMap<>();

    public <T> void putResourceLoader(Class<T> tClass, String resourceType, BiFunction<T, String, Void> loader) {
        ConcurrentHashMap<String, BiFunction<T, String, Void>> resourceLoaderMap =
                defaultResourecesLoaderMap.get(tClass);
        if (resourceLoaderMap == null) {
            resourceLoaderMap = new ConcurrentHashMap<>(8);
            defaultResourecesLoaderMap.put(tClass, resourceLoaderMap);
        }
        resourceLoaderMap.put(resourceType, loader);
    }

    public <T> BiFunction<T, String, Void> getResourceLoader(Class<T> tClass, String resourceType) {
        ConcurrentHashMap<String, BiFunction<T, String, Void>> resourceLoaderMap =
                defaultResourecesLoaderMap.get(tClass);
        if (resourceLoaderMap == null) {
            return null;
        }
        return resourceLoaderMap.get(resourceType);
    }


    private final ConcurrentHashMap<String, BiFunction<Texture, String[], Void>> resourceLoaders =
            new ConcurrentHashMap<>();

    public <T> void putResourceWithFullURI(String fullResourceURI, T t) {
        if (fullResourceURI == null || fullResourceURI.isEmpty()) {
            throw (new Error("putResourceWithURI : fullResourceURI is null or" +
                    " Empty"));
        }

        LOGGER.debug("putResource {}", fullResourceURI);

        StringBuilder sb = new StringBuilder();
        int i = 0;

        for (; i < fullResourceURI.length(); i++) {
            if (fullResourceURI.charAt(i) == ':') {
                i++;
                break;
            }
            sb.append(fullResourceURI.charAt(i));
        }

        String resourceClassName = sb.toString();
        sb = new StringBuilder();

        Class resourceClass = null;

        try {
            resourceClass =
                    this.getClass().getClassLoader().loadClass(resourceClassName);
        } catch (ClassNotFoundException e) {
            //            e.printStackTrace();
        }

        if (resourceClass == null) {
            throw (new Error("putResourceWithURI : resourceClass is null"));
        }

        this.putResourceWithShortenURI(fullResourceURI.substring(i), t);
    }


    public Object getResourceFromFullURI(String fullResourceURI) {
        if (fullResourceURI == null || fullResourceURI.isEmpty()) {
            return null;
        }

        if (this.gameManager.getDataCenter().isDebug()) {
            LOGGER.debug("putResource {}", fullResourceURI);
        }

        StringBuilder sb = new StringBuilder();
        int i = 0;

        for (; i < fullResourceURI.length(); i++) {
            if (fullResourceURI.charAt(i) == ':') {
                i++;
                break;
            }
            sb.append(fullResourceURI.charAt(i));
        }

        String resourceClassName = sb.toString();
        sb = new StringBuilder();

        Class resourceClass = null;

        try {
            resourceClass =
                    this.getClass().getClassLoader().loadClass(resourceClassName);
        } catch (ClassNotFoundException e) {
            //            e.printStackTrace();
        }

        if (resourceClass == null) {
            return null;
        }

        Object res = this.getResourceFromShortenURI(resourceClass,
                fullResourceURI.substring(i));
        return res;
    }


    public <T> void putResourceWithShortenURI(String shortenResourceURI, T t) {
        ConcurrentHashMap<String, T> resourceURIMap =
                getDefaultResourecesURIMap().get(t.getClass());
        if (resourceURIMap == null) {
            resourceURIMap = new ConcurrentHashMap<>(100);
            getDefaultResourecesURIMap().put(t.getClass(), resourceURIMap);
        }
        resourceURIMap.put(shortenResourceURI, t);
    }

    public <T> T getResourceFromShortenURI(Class<T> tClass,
                                           String shortenResourceURI) {

        ConcurrentHashMap<String, T> resourceURIMap =
                getDefaultResourecesURIMap().get(tClass);
        if (resourceURIMap == null) {
            return null;
        } else {
            return (T) resourceURIMap.get(shortenResourceURI);
        }
    }

    public <T> boolean ifExistResourceFromShortenURI(Class<T> tClass,
                                                     String shortenResourceURI) {

        ConcurrentHashMap<String, T> resourceURIMap =
                getDefaultResourecesURIMap().get(tClass);
        if (resourceURIMap == null) {
            return false;
        } else {
            return resourceURIMap.containsKey(shortenResourceURI);
        }
    }

    public <T extends AbstractResource> T fetchResourceWithShortenURI(Class<T> tClass, String shortenResourceURI) {
        T res = null;
        if (this.ifExistResourceFromShortenURI(tClass, shortenResourceURI)) {
            res = this.getResourceFromShortenURI(tClass, shortenResourceURI);
        } else {
            try {
                res = tClass.getDeclaredConstructor(ResourceManager.class,
                        String.class).newInstance(this,
                        tClass.getCanonicalName() + ":" + shortenResourceURI);
                this.putResourceWithShortenURI(shortenResourceURI, res);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return res;
    }

    @Override
    public void close() {
        for (Map<Class, Map> submap : getDefaultResourecesURIMap().values()) {
            for (Object object : submap.values()) {
                if (object instanceof AutoCloseable) {
                    try {
                        ((AutoCloseable) object).close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }


//    public ArrayList<AbstractResource> deletingResources = new ArrayList<>();

    public ResourceManager(GameManager gameManager) {
        this.setGameManager(gameManager);
    }

    public void load(AbstractResource resource) {
        if (resource.isInMemory()) {
            return;
        }
        setTotalMemorySize(getTotalMemorySize() + resource.getMemorySize());
        getInMemoryResources().add(resource);
        resource.setInMemory(true);
    }

    public void close(AbstractResource resource) {
        if (!resource.isInMemory()) {
            return;
        }
        setTotalMemorySize(getTotalMemorySize() - resource.getMemorySize());
//        deletingResources.add(resource);
//        inMemoryResources.remove(resource);
        resource.setInMemory(false);
    }


    public void suggestGc() {
        if (this.getGameManager().getNowFrameIndex() % 1000 == 0) {
            LOGGER.debug("suggestGc at totalMemorySize : {}", this.getTotalMemorySize());
        }
        if (this.getTotalMemorySize() <= TOTAL_MEMORY_SIZE_START_POINT) {
            LOGGER.debug("refuse gc");
            return;
        }

        this.forceGc();
        LOGGER.debug("after forceGc totalMemorySize changed to : {}", this.getTotalMemorySize());
    }

    public void forceGc() {
        getInMemoryResources().sort((o1, o2) ->
                o1.getLastUsedFrameIndex() < o2.getLastUsedFrameIndex() ? -1 :
                        o1.getLastUsedFrameIndex() == o2.getLastUsedFrameIndex() ? 0 : 1);
        ArrayList<AbstractResource> newInMemoryResources = new ArrayList<>();
        for (AbstractResource nowResource : getInMemoryResources()) {
            if (nowResource.isInMemory() == false) {
                continue;
            }

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
        this.getInMemoryResources().clear();
        this.getInMemoryResources().addAll(newInMemoryResources);
    }


    public GameManager getGameManager() {
        return gameManager;
    }

    public void setGameManager(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    public long getTotalMemorySize() {
        return totalMemorySize;
    }

    public void setTotalMemorySize(long totalMemorySize) {
        this.totalMemorySize = totalMemorySize;
    }

    public ArrayList<AbstractResource> getInMemoryResources() {
        return inMemoryResources;
    }

    public ConcurrentHashMap<Class, ConcurrentHashMap> getDefaultResourecesURIMap() {
        return defaultResourecesURIMap;
    }
}
