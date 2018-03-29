mport net.sf.ehcache.Ehcache;

/**
 * This interface should be used when the cache objects are 
 * created dynamically during runtime using a template configuration
 * provided in the spring configuration file.
 * 
 * The most common usage will be through a pre-configured cache in which
 * case this interface need not be used.
 * 
 * @author uiak
 *
 */
public interface EhcacheFactory {

    /**
     * Any implementation of this needs to create a cache
     * during runtime for the given name.
     * @param cacheName
     * @return Ehcache
     */
    public Ehcache createCache(String cacheName);

}


import java.io.IOException;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MarkerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

import com.company.spring.caching.util.CachingProviderErrorCodes;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.constructs.blocking.BlockingCache;
import net.sf.ehcache.constructs.blocking.CacheEntryFactory;
import net.sf.ehcache.constructs.blocking.SelfPopulatingCache;
import net.sf.ehcache.constructs.blocking.UpdatingCacheEntryFactory;
import net.sf.ehcache.constructs.blocking.UpdatingSelfPopulatingCache;
import net.sf.ehcache.store.MemoryStoreEvictionPolicy;

/**
 * Use this cache factory when the cache needs to be built based on runtime contexts such as cache per thread or cache per session or even application
 * data based cache such as cache per client POID.
 * 
 * <pre>
 * An example is shown below.
 * 
 *  &lt;bean id=&quot;fundsCacheFactory&quot; class=&quot;com.vanguard.spring.caching.support.EhcacheFactoryImpl&quot;&gt;
 *  &lt;property name=&quot;cacheManager&quot; ref=&quot;caching-provider-cacheManager&quot; /&gt;
 *  &lt;!--  This blocking parameter needs to be always true --&gt;
 *  &lt;property name=&quot;blocking&quot; value=&quot;true&quot; /&gt;
 *  &lt;property name=&quot;diskPersistent&quot; value=&quot;false&quot; /&gt;
 *  &lt;property name=&quot;eternal&quot; value=&quot;false&quot; /&gt;
 *  &lt;property name=&quot;maxElementsInMemory&quot; value=&quot;100&quot; /&gt;
 *  &lt;property name=&quot;maxElementsOnDisk&quot; value=&quot;0&quot; /&gt;
 *  &lt;property name=&quot;memoryStoreEvictionPolicy&quot;&gt;
 *  &lt;util:constant static-field=&quot;net.sf.ehcache.store.MemoryStoreEvictionPolicy.LRU&quot; /&gt;
 *  &lt;/property&gt;
 *  &lt;property name=&quot;overflowToDisk&quot; value=&quot;false&quot; /&gt;
 *  &lt;property name=&quot;timeToIdle&quot; value=&quot;0&quot; /&gt;
 *  &lt;property name=&quot;timeToLive&quot; value=&quot;0&quot; /&gt;
 *  &lt;/bean&gt;
 * 
 * </pre>
 * 
 * @author uiak
 * @see org.springframework.cache.ehcache.EhCacheFactoryBean
 */
public class EhcacheFactoryImpl implements InitializingBean, EhcacheFactory {

	private static final Logger LOGGER = LoggerFactory.getLogger(EhcacheFactoryImpl.class);
    
    protected CacheManager cacheManager;

    protected CacheEntryFactory cacheEntryFactory;

    private final ReentrantReadWriteLock readwritelock = new ReentrantReadWriteLock();

    private int maxElementsInMemory = 10000;

    private int maxElementsOnDisk = 10000000;

    private MemoryStoreEvictionPolicy memoryStoreEvictionPolicy = MemoryStoreEvictionPolicy.LRU;

    private boolean overflowToDisk = true;

    private String diskStorePath;

    private boolean eternal = false;

    private int timeToLive = 120;

    private int timeToIdle = 120;

    private boolean diskPersistent = false;

    private int diskExpiryThreadIntervalSeconds = 120;

    private boolean blocking = false;

    public void setCacheManager(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    /**
     * Specify the maximum number of cached objects in memory. Default is 10000 elements.
     */
    public void setMaxElementsInMemory(int maxElementsInMemory) {
        this.maxElementsInMemory = maxElementsInMemory;
    }

    /**
     * Specify the maximum number of cached objects on disk. Default is 10000000 elements.
     */
    public void setMaxElementsOnDisk(int maxElementsOnDisk) {
        this.maxElementsOnDisk = maxElementsOnDisk;
    }

    /**
     * Set the memory style eviction policy for this cache. Supported values are "LRU", "LFU" and "FIFO", according to the constants defined in
     * EHCache's MemoryStoreEvictionPolicy class. Default is "LRU".
     */
    public void setMemoryStoreEvictionPolicyFromObject(MemoryStoreEvictionPolicy memoryStoreEvictionPolicy) {
        Assert.notNull(memoryStoreEvictionPolicy, "memoryStoreEvictionPolicy must not be null");
        this.memoryStoreEvictionPolicy = memoryStoreEvictionPolicy;
    }

    /**
     * Set whether elements can overflow to disk when the in-memory cache has reached the maximum size limit. Default is "true".
     */
    public void setOverflowToDisk(boolean overflowToDisk) {
        this.overflowToDisk = overflowToDisk;
    }

    /**
     * Set the location of temporary files for the disk store of this cache. Default is the CacheManager's disk store path.
     */
    public void setDiskStorePath(String diskStorePath) {
        this.diskStorePath = diskStorePath;
    }

    /**
     * Set whether elements are considered as eternal. If "true", timeouts are ignored and the element is never expired. Default is "false".
     */
    public void setEternal(boolean eternal) {
        this.eternal = eternal;
    }

    /**
     * Set t he time in seconds to live for an element before it expires, i.e. the maximum time between creation time and when an element expires. It
     * is only used if the element is not eternal. Default is 120 seconds.
     */
    public void setTimeToLive(int timeToLive) {
        this.timeToLive = timeToLive;
    }

    /**
     * Set the time in seconds to idle for an element before it expires, that is, the maximum amount of time between accesses before an element
     * expires. This is only used if the element is not eternal. Default is 120 seconds.
     */
    public void setTimeToIdle(int timeToIdle) {
        this.timeToIdle = timeToIdle;
    }

    /**
     * Set whether the disk store persists between restarts of the Virtual Machine. The default is "false".
     */
    public void setDiskPersistent(boolean diskPersistent) {
        this.diskPersistent = diskPersistent;
    }

    /**
     * Set the number of seconds between runs of the disk expiry thread. The default is 120 seconds.
     */
    public void setDiskExpiryThreadIntervalSeconds(int diskExpiryThreadIntervalSeconds) {
        this.diskExpiryThreadIntervalSeconds = diskExpiryThreadIntervalSeconds;
    }

    public void setBlocking(boolean blocking) {
        this.blocking = blocking;
    }

    public void setCacheEntryFactory(CacheEntryFactory cacheEntryFactory) {
        this.cacheEntryFactory = cacheEntryFactory;
    }

    public void afterPropertiesSet() throws CacheException, IOException {
        // If no CacheManager given, fetch the default.

        if (this.cacheManager == null) {
        	LOGGER.trace("Using default EHCache CacheManager");
            this.cacheManager = CacheManager.getInstance();
        }
    }

    public Ehcache createCache(String cacheName) {
        // Fetch cache region: If none with the given name exists,
        // create one on the fly.

        Ehcache decoratedCache = null;
        try {
            readwritelock.readLock().lock();
            if (!this.cacheManager.cacheExists(cacheName)) {
                readwritelock.readLock().unlock();
                readwritelock.writeLock().lock();
                if (!this.cacheManager.cacheExists(cacheName)) {
                	LOGGER.trace("Requested cache does not exist: {}", cacheName);
                    createAndDecorateCache(cacheName);
                }
                readwritelock.readLock().lock();
                readwritelock.writeLock().unlock();
            }
            LOGGER.trace("Fetching cache {} from cache manager", cacheName);
            decoratedCache = this.cacheManager.getEhcache(cacheName);
            return decoratedCache;

        } catch (Exception ex) {
        	LOGGER.error(MarkerFactory.getMarker(CachingProviderErrorCodes.ERROR_IN_DYNAMIC_CACHE_CREATION.getCode()), "Error in creating dynamic cache: {}", cacheName, ex);
            return null;
        } finally {
            if (readwritelock.isWriteLockedByCurrentThread()) {
                readwritelock.writeLock().unlock();
            }
            readwritelock.readLock().unlock();
        }

    }

    /**
     * Decorate the given Cache, if necessary.
     * <p>
     * The default implementation simply returns the given cache object as-is.
     * 
     * @param cache
     *            the raw Cache object, based on the configuration of this FactoryBean
     * @return the (potentially decorated) cache object to be registered with the CacheManager
     */
    protected Ehcache decorateCache(Cache cache) {
        if (this.cacheEntryFactory != null) {
            if (this.cacheEntryFactory instanceof UpdatingCacheEntryFactory) {
                return new UpdatingSelfPopulatingCache(cache, (UpdatingCacheEntryFactory) this.cacheEntryFactory);
            }
            return new SelfPopulatingCache(cache, this.cacheEntryFactory);
        }
        if (this.blocking) {
            return new BlockingCache(cache);
        }
        return cache;
    }

    /**
     * @param cacheName
     */
    void createAndDecorateCache(String cacheName) {
        Ehcache decoratedCache;
        Cache rawCache;
        rawCache = internalcreateCache(cacheName);
        this.cacheManager.addCache(rawCache);
        LOGGER.trace("Created and added requested cache: {}", cacheName);
        decoratedCache = decorateCache(rawCache);
        if (decoratedCache != rawCache) { // parasoft-suppress PB.CUB.UEIC "Object Identity is compared"
            // here to make sure the cache
            // is not decorated.
            this.cacheManager.replaceCacheWithDecoratedCache(rawCache, decoratedCache);
            LOGGER.trace("Converted requested cache into a blocked cache: {}", cacheName);
        }
    }

    /**
     * Create a raw Cache object based on the configuration of this FactoryBean.
     */
    Cache internalcreateCache(String cacheName) {
        return new Cache(cacheName, this.maxElementsInMemory, this.memoryStoreEvictionPolicy, this.overflowToDisk, this.diskStorePath, this.eternal,
                this.timeToLive, this.timeToIdle, this.diskPersistent, this.diskExpiryThreadIntervalSeconds, null, null, this.maxElementsOnDisk);
    }

}

/**
 * Error Codes for the CachingProvider module.
 * <p>Start: 2032060
 * <p>End:   2032079
 * @author uvjl
 */
public enum CachingProviderErrorCodes {
    
    /**
     * <b>DynamicCacheCleaner</b>
     * <i>(WARNING)</i>
     * [2032061]
     * There are valid cases when the Http Session based cache name generation
     * policies would not be able to generate the cache name. For e.g. in the
     * Internal Web Applications using DefaultClientScopedCacheNameGeneration
     * Policy, we expect the "poid" to be available in the Http Session. What if
     * the internal user never loads a client, i.e., log-on to the application
     * and log-off, there will not be a POID in the Http Session. Please review
     * the sequence of events to determine if this similar to the scenario
     * listed above.
     */
    CACHE_NAME_GENERATOR("2032061"),
    
    /**
     * <b>DynamicCacheCleaner</b>
     * <i>(WARNING)</i>
     * [2032062]
     * This message indicates that the listed cache is being removed from the
     * cache manager. This message is being logged to enable better debugging in
     * Unix Regions where typically log level is set to WARN and above. This
     * message does not indicate any coding error.
     */
    REMOVING_CACHE("2032062"),
    
    /**
     * <b>DynamicCacheCleaner</b>
     * <i>(WARNING)</i>
     * [2032063]
     * This message indicates that the MBean corresponding to the cache being
     * removed from the cache name failed to be deactivated. This is possible if
     * the module is not running on WebSphere.
     */
    MBEAN_CLEANUP("2032063"),
    
    /**
     * <b>ThreadScopedCacheResetEventListener</b>
     * <i>(WARNING)</i>
     * [2032064]
     * This message indicates that the Thread Reset Event has been raised and is
     * removing Client scoped cache names. This message is being logged to
     * enable better debugging in Unix Regions where typically log level is set
     * to WARN and above. This message does not indicate code error.
     */
    CLIENT_RESET_EVENT("2032064"),
    
    /**
     * <b>ThreadScopedCacheResetEventListener</b>
     * <i>(WARNING)</i>
     * [2032065]
     * This message indicates that the Thread Reset Event has been raised and is
     * removing Thread scoped cache names. This message is being logged to
     * enable better debugging in Unix Regions where typically log level is set
     * to WARN and above. This message does not indicate code error.
     */
    THREAD_RESET_EVENT("2032065"),
    
    /**
     * <b>WebSphereAdminServerUtility</b>
     * <i>(WARNING)</i>
     * [2032069]
     * This message indicates that the WebSphere JMX MBean server instance
     * cannot be reached while trying to deregister/remove a JMX MBean. If the
     * module is being run outside of WebSphere, this can be ignored but
     * suggests misconfiguration (attempting to use JMX outside of WebSphere).
     */
    NO_ADMIN_SERVER("2032069"),
    
    /**
     * <b>SessionCacheProviderSessionListener</b>
     * <i>(ERROR)</i>
     * [2032070]
     * When a session is invalidated, an event is published through the Spring
     * ApplicationContext to notify listeners that session-based caches need to
     * be destroyed. If the ApplicationContext did not load properly, this
     * causes an IllegalStateException to be thrown which is the basis for this
     * error log message (see NGSA TeamTrack 1148 for more background). If the
     * application did not start successfully, diagnose the errors in the
     * HVLM/standard out/standard error logs and fix any problems. Once the
     * ApplicationContext starts without errors, you should no longer hit this
     * error condition.
     */
    COULD_NOT_PUBLISH_SESSION_INVALIDATED("2032070"),
    
    /**
     * <b>WebSphereAdminServerUtility</b>
     * <i>(WARNING)</i>
     * [2032071]
     * This message indicates that an exception was thrown while trying to
     * deregister/remove a JMX MBean from WebSphere. This is not a error error
     * but should be investigated when possible since the application requested
     * JMX to be used through configuration.
     */
    COULD_NOT_REMOVE_MBEAN("2032071"),
    
    /**
     * <b>BlockingCacheDeadlockDetector</b>
     * <i>(ERROR)</i>
     * [2032073]
     * This error occurs when caching application code is nested and using the
     * same blocking cache.  This has a potential to create deadlocks and is
     * not allowed.  An exception will be thrown and the application implementing
     * caching in this manner needs to investigate the error and exception to
     * eliminate the nested calls to the same cache.
     */
    NESTED_CACHING_CALL("2032073"),
    
    /**
     * <b>EhcacheFactoryImpl</b>
     * <i>(ERROR)</i>
     * [2032074]
     * This error would occur when caching provider is unable to create a EhCache.
     * The name of the failed cache will be listed in the log as well as the exception.
     * When a cache cannot be created, caching for application code using the specified
     * cache will not be caching.  Inspect the exception in the log for troubleshooting.
     */
    ERROR_IN_DYNAMIC_CACHE_CREATION("2032074"),
    
    /**
     * <b>GemFireCache</b>
     * <i>(ERROR)</i>
     * [2032075]
     * An exception was thrown when attempting to get/put a value into GemFire.  The
     * log will indicate if it was a put or a get that failed.  Look at the exception
     * for help with resolution.
     */
    GEMFIRE_IO_FAILURE("2032075"),
    
    /**
     * <b>CacheServiceImpl</b>
     * <i>(ERROR)</i>
     * [2032076]
     * The error occurs when the specified Gemfire Region was not retrievable.  When
     * this happens, the logic returns a Null Object Pattern Cache that will do no
     * caching.
     */
    GEMFIRE_REGION_FAILURE("2032076");
    
    private String code;
    
    CachingProviderErrorCodes(String code) {
        this.code = code;
    }
    
    public String getCode() {
        return code;
    }

}
