package com.xenoamess.cyan_potion.base.memory;

import com.xenoamess.cyan_potion.base.DataCenter;
import com.xenoamess.cyan_potion.base.GameManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author XenoAmess
 */
public class ResourceManager implements AutoCloseable {
    private static final Logger LOGGER = LoggerFactory.getLogger(ResourceManager.class);

    public static final long TOTAL_MEMORY_SIZE_LIMIT_POINT = 4L * 1024 * 1024 * 1024;
    public static final long TOTAL_MEMORY_SIZE_START_POINT = 3L * 1024 * 1024 * 1024;
    public static final long TOTAL_MEMORY_SIZE_DIST_POINT = 2L * 1024 * 1024 * 1024;

    private GameManager gameManager;
    private long totalMemorySize = 0;
    private ArrayList<AbstractResource> inMemoryResources = new ArrayList<>();
    private ConcurrentHashMap<Class, ConcurrentHashMap> defaultResourecesURIMap = new ConcurrentHashMap<>();

    public <T> void putResourceWithFullURI(String fullResourceURI, T t) {
        if (fullResourceURI == null || fullResourceURI.isEmpty()) {
            throw (new Error("putResourceWithURI : fullResourceURI is null or Empty"));
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
            resourceClass = this.getClass().getClassLoader().loadClass(resourceClassName);
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

        if (DataCenter.DEBUG) {
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
            resourceClass = this.getClass().getClassLoader().loadClass(resourceClassName);
        } catch (ClassNotFoundException e) {
            //            e.printStackTrace();
        }

        if (resourceClass == null) {
            return null;
        }

        Object res = this.getResourceFromShortenURI(resourceClass, fullResourceURI.substring(i));
        return res;
    }


    public <T> void putResourceWithShortenURI(String shortenResourceURI, T t) {
        ConcurrentHashMap<String, T> resourceURIMap = getDefaultResourecesURIMap().get(t.getClass());
        if (resourceURIMap == null) {
            resourceURIMap = new ConcurrentHashMap<>(100);
            getDefaultResourecesURIMap().put(t.getClass(), resourceURIMap);
        }
        resourceURIMap.put(shortenResourceURI, t);
    }

    public <T> T getResourceFromShortenURI(Class<T> tClass, String shortenResourceURI) {

        ConcurrentHashMap<String, T> resourceURIMap = getDefaultResourecesURIMap().get(tClass);
        if (resourceURIMap == null) {
            return null;
        } else {
            return (T) resourceURIMap.get(shortenResourceURI);
        }
    }

    public <T> boolean ifExistResourceFromShortenURI(Class<T> tClass, String shortenResourceURI) {

        ConcurrentHashMap<String, T> resourceURIMap = getDefaultResourecesURIMap().get(tClass);
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
                res = tClass.getDeclaredConstructor(GameManager.class, String.class).newInstance(this.getGameManager(), tClass.getCanonicalName() + ":" + shortenResourceURI);
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

    public void gc() {
        if (this.getGameManager().getNowFrameIndex() % 1000 == 0) {
            LOGGER.debug("totalMemorySize : {}", this.getTotalMemorySize());
        }
        if (this.getTotalMemorySize() <= TOTAL_MEMORY_SIZE_START_POINT) {
            return;
        }

        getInMemoryResources().sort((o1, o2) -> o1.getLastUsedFrameIndex() < o2.getLastUsedFrameIndex() ? -1 : o1.getLastUsedFrameIndex() == o2.getLastUsedFrameIndex() ? 0 : 1);
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
        this.setInMemoryResources(newInMemoryResources);
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

    public void setInMemoryResources(ArrayList<AbstractResource> inMemoryResources) {
        this.inMemoryResources = inMemoryResources;
    }

    public ConcurrentHashMap<Class, ConcurrentHashMap> getDefaultResourecesURIMap() {
        return defaultResourecesURIMap;
    }

    public void setDefaultResourecesURIMap(ConcurrentHashMap<Class, ConcurrentHashMap> defaultResourecesURIMap) {
        this.defaultResourecesURIMap = defaultResourecesURIMap;
    }
}
