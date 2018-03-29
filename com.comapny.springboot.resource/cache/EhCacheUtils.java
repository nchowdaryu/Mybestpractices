import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.constructs.blocking.BlockingCache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.company.spring.core.config.support.PropertyLocator;

/**
 * @author uiak This class is a collection of utility methods to interact with ehcache. Create utility APIs only in cases where a group of ehcache
 *         APIs have to perform a task.
 */
public class EhCacheUtils {

    static final Object SEMI = "__SEMI__";
    private static final Logger LOGGER = LoggerFactory.getLogger(EhCacheUtils.class);

    /**
     * @param ehcache
     * @param cacheEntryKey
     * @return returns the element if present in the provided cache and its not an expired entry.
     */
    public static Element getActiveElementFromCache(Ehcache ehcache, String cacheEntryKey) {

        if (ehcache == null || !StringUtils.hasText(cacheEntryKey)) {
            throw new IllegalArgumentException(" To check if element present in cache the cache needs to be non-null and key has to be valid ");
        }
        
        LOGGER.trace("Checking for active element for key {} in cache {}", cacheEntryKey, ehcache.getName());

        if (ehcache instanceof BlockingCache) {
            ((BlockingCache) ehcache).setTimeoutMillis(getProperty("BLOCKING_CACHE_ENTRYREAD_LOCK_TIMEOUT_SECS"));
        }
        Element element = ehcache.get(cacheEntryKey);

        if (isInvalidEntry(cacheEntryKey, ehcache, element)) {
            return null;
        }
        
        return element;
    }

    public static String cleanUpCacheName(String cacheName) {
        // EhCache does not allow : so if : is found replace it with "SEMI"
        String fixedCacheName = null;
        if (cacheName.indexOf(':') >= 0) {
        	LOGGER.trace("Cache name contains a reserved char semicolon : will be replaced with SEMI");
            fixedCacheName = cacheName.replaceAll(":", "__SEMI__");
        } else {
            fixedCacheName = cacheName;
        }
        return fixedCacheName;
    }

    /**
     * @param cacheEntryKey
     * @param ehCache
     * @param element
     * @return
     */
    private static boolean isInvalidEntry(String cacheEntryKey, Ehcache ehCache, Element element) {
        return !ehCache.isKeyInCache(cacheEntryKey) || element == null || (element != null && ehCache.isExpired(element));
    }
    
    private static int getProperty(String propertyName) {
        return PropertyLocator.getIntegerProperty("caching-provider", propertyName);
    }

}
