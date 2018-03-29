import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.support.SimpleValueWrapper;
import org.springframework.util.Assert;

import com.company.spring.caching.CacheConfiguration;
import com.company.spring.caching.EhCacheUtils;
import com.company.spring.caching.EnhancedCache;
import com.company.toolbox.object.ObjectUtilz;

import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.constructs.blocking.BlockingCache;

public class EhCacheWrapper implements EnhancedCache {
    
	private static final Logger LOGGER = LoggerFactory.getLogger(EhCacheWrapper.class);
    
    private Ehcache ehCache;
    
    private CacheConfiguration cacheConfiguration;
    
    public EhCacheWrapper(CacheConfiguration cacheConfiguration, Ehcache ehCache) {
        Assert.notNull(ehCache);
        Assert.notNull(cacheConfiguration);
        
        this.ehCache = ehCache;
        this.cacheConfiguration = cacheConfiguration;
    }

    public String getName() {
        return ehCache.getName();
    }

    public Object getNativeCache() {
        return ehCache;
    }

    public ValueWrapper get(Object obj) {
        String key = ObjectUtilz.safeCast(obj, String.class);
        Element element = EhCacheUtils.getActiveElementFromCache(ehCache, key);
        
        if (element == null) {
            return null;
        }
        
        return new SimpleValueWrapper(element.getObjectValue());
    }

    public void put(Object key, Object value) {
        if (value == null) {
            /* Only populate with null value for blocking cache that needs to release lock.
             * get() does not return null value for BlockingCache but check is necessary so Ehcache (non-blocking)
             * does not cache null value if there is exception
             * 
             * TODO investigate if we can live with the behavior of removing this check and remove this check
             */
            if (ehCache instanceof BlockingCache) {
                ehCache.put(new Element(key, value));
            }
        } else {
            ehCache.put(new Element(key, value));
        }
    }

    public void evict(Object key) {
        ehCache.remove(key);
    }

    public void clear() {
        ehCache.removeAll();
    }

    @SuppressWarnings("unchecked")
    public void removeMatchingKeys(Predicate predicate) {
        Collection<String> keys = new ArrayList<String>();
        keys.addAll(ehCache.getKeys());
        CollectionUtils.filter(keys, predicate);
        
        for (String key : keys) {
        	LOGGER.trace("Found a key {} matching predicate: {} - will be removed from cache.", key, predicate.toString());
            evict(key);
        }
    }

    public boolean isBlocking() {
        //always return false because EhCache has a BlockingCache implementation when blocking.  No extra work needed.
        return false;
    }

	public CacheConfiguration getCacheConfiguration() {
		return cacheConfiguration;
	}

    @Override
    public void validateObjectForCache(Object cacheEntry) {
        if (cacheEntry == null) {
            //OK, pass through
        } else if (!(cacheEntry instanceof Serializable)) {
            throw new ClassCastException("Cache result is not Serializable");
        }
    }

    /**
     * @see Cache#get(Object, Class) contract and third-party implementations as the basis for this implementation
     */
	@SuppressWarnings("unchecked")
	@Override
	public <T> T get(Object key, Class<T> type) {
		ValueWrapper valueWrapper = get(key);
		if(valueWrapper == null) {
			return null;
		}
		
		Object value = valueWrapper.get();
		if (value != null && type != null && !type.isInstance(value)) {
			throw new IllegalStateException("Cached value is not of required type [" + type.getName() + "]: " + value);
		}
		return (T) value;		
	}

	@Override
	public ValueWrapper putIfAbsent(Object key, Object value) {
		return new SimpleValueWrapper(ehCache.putIfAbsent(new Element(key, value)));
	}

}
