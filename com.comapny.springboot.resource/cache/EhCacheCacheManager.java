import static com.company.spring.caching.Constants.DEFAULT_CACHING_CONFIGURATION;

import java.util.Collection;
import java.util.Collections;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.constructs.blocking.BlockingCache;
import net.sf.ehcache.constructs.blocking.SelfPopulatingCache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.support.AbstractCacheManager;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import com.company.spring.caching.CacheConfiguration;
import com.company.spring.caching.CachingConfiguration;
import com.company.spring.caching.EnhancedCache;
import com.company.spring.caching.EnhancedCacheManager;
import com.company.spring.caching.events.ObjectCacheDeleteEvent;
import com.company.spring.caching.support.DefaultCachingConfiguration;
import com.company.spring.caching.support.NgsaCacheNullObject;
import com.company.spring.core.events.ApplicationEventPublisherService;
import com.company.spring.servicelocator.SpringBeanServiceLocator;
import com.company.toolbox.object.ObjectUtilz;

public class EhCacheCacheManager extends AbstractCacheManager implements EnhancedCacheManager {
    
	private static final Logger LOGGER = LoggerFactory.getLogger(EhCacheCacheManager.class);
    private CacheManager nativeCacheManager;

    @Override
    public EnhancedCache getCache(String cacheConfigurationName) {
        CachingConfiguration cachingConfiguration = determineCachingConfiguration(cacheConfigurationName);
        
        if (cachingConfiguration == null) {
            return null;
        }
        
        Ehcache ehCache = getConfiguredCache(cacheConfigurationName, cachingConfiguration);
        return new EhCacheWrapper(cachingConfiguration, ehCache);
    }

    Ehcache getConfiguredCache(String cachingConfigurationName, CachingConfiguration cachingConfiguration) {
        Ehcache cache = cachingConfiguration.getCache();
        if (cache == null) {
            throw new IllegalStateException("Intercepted method does not have proper Cacheable Configuration. Missing cache for " + cachingConfigurationName + ". Detail - possible reason for error is that multiple threads are pointing to this cache (container for cache entries) " + "and one thread has invalidated this cache while another thread was in midst of caching a key. If this is a dynamic " + "cache that should be unique (e.g. per user, per thread) make sure the cache name generation policy is generating a unique name.");
        }

        // Static caches must be blocking caches
        // Dynamic caches could be non-blocking
        if ((cachingConfiguration instanceof DefaultCachingConfiguration) && !(cache instanceof BlockingCache)) {
            throw new IllegalStateException(cachingConfigurationName + " : Configured cache is not a blocking cache.. invalid configuration : configure the cache to be blocking..");
        }

        return cache;
    }
    
    @Override
    protected Collection<? extends Cache> loadCaches() {
        return Collections.singletonList(NgsaCacheNullObject.getInstance());
    }

    private static CachingConfiguration determineCachingConfiguration(String cachingConfigurationName) {
        Assert.isTrue(!DEFAULT_CACHING_CONFIGURATION.equals(cachingConfigurationName), cachingConfigurationName + " : Cache configuration is not provided .. invalid configuration");

        LOGGER.trace("Interceptor configured for custom cache configuration {}", cachingConfigurationName);

        // If bean cannot be found this call throw Spring framework generated exception.
        return ObjectUtilz.safeCast(SpringBeanServiceLocator.getSpringManagedBean(cachingConfigurationName), CachingConfiguration.class);
    }

    public boolean cacheExists(String cacheName) {
        if (StringUtils.hasText(cacheName)) {
            CacheManager nativeCacheManager = getNativeCacheManager();
            return nativeCacheManager.cacheExists(cacheName);
        }
        
        return false;
    }

    public void destroyData(CacheConfiguration cacheConfiguration) {
        Assert.notNull(cacheConfiguration);
        
        CachingConfiguration ehCacheConfiguration = ObjectUtilz.safeCast(cacheConfiguration, CachingConfiguration.class);
        if (ehCacheConfiguration != null) {
            Ehcache ehCache = ehCacheConfiguration.getCache();
            String cacheName = ehCache.getName();
            
            if (ehCache instanceof SelfPopulatingCache) {
            	LOGGER.trace("Publishing ObjectCacheDeleteEvent for cache - {}", cacheName);
                ApplicationEventPublisherService.publishEvent(new ObjectCacheDeleteEvent(this, cacheName));
            }
            
            CacheManager nativeCacheManager = getNativeCacheManager();
            
            if (!nativeCacheManager.cacheExists(cacheName)) {
            	LOGGER.trace("Cache with name {} does not exist.", cacheName);
                return;
            }
            nativeCacheManager.removeCache(cacheName);
        }
        
        //do nothing, not a EhCache
    }

    private CacheManager getNativeCacheManager() {
        if (nativeCacheManager == null) {
            nativeCacheManager = (CacheManager) SpringBeanServiceLocator.getSpringManagedBean("caching-provider-cacheManager");
        }
        
        return nativeCacheManager;
    }
    
    void setNativeCacheManager(CacheManager cachemanager) {
        nativeCacheManager = cachemanager;
    }

}
