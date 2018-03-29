import net.sf.ehcache.Ehcache;

import org.springframework.util.Assert;

import com.vanguard.spring.caching.CacheEntryKeyGenerationPolicy;
import com.vanguard.spring.caching.CacheNameGenerationPolicy;
import com.vanguard.spring.caching.CachingConfiguration;
import com.vanguard.spring.caching.EhCacheUtils;

/**
 * 
 * <p>
 * 
 * <pre>
 * A sample configuration is shown below.
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
 *  &lt;!-- Uses component scaned key generator --&gt;
 *  &lt;bean id=&quot;fundsCacheConfiguration&quot; class=&quot;com.vanguard.spring.caching.support.DynamicCacheConfiguration&quot;&gt;
 *  &lt;!-- This should be same as the cacheName attribute used for cache definition above. --&gt;
 *  &lt;constructor-arg ref=&quot;fundsCacheFactory&quot; /&gt;
 *  &lt;constructor-arg&gt;
 *  &lt;bean class=&quot;com.vanguard.spring.caching.support.ThreadscopedCacheNameGeneratorPolicy&quot;&gt;&lt;/bean&gt;
 *  &lt;/constructor-arg&gt;
 *  &lt;constructor-arg ref=&quot;pointcutParametersCacheEntryKeyGenerationPolicy&quot; /&gt;
 *  &lt;/bean&gt;
 * 
 * </pre>
 * 
 * @author uiak
 * 
 */
public class DynamicCacheConfiguration implements CachingConfiguration {

    private EhcacheFactory cacheFactory;

    private CacheEntryKeyGenerationPolicy cacheEntryKeyGenerator;

    private CacheNameGenerationPolicy cacheNameGenerator;

    /**
     * @param cacheFactory
     *            This parameter should refer to a dynamic cache creator
     *            configured using EhcacheFactoryImpl
     * @param cacheNameGenerator
     *            Should refer to an instance of CacheNameGenerationPolicy
     * @param keygenerationpolicy
     *            Should refer to an instance of
     *            pointcutParametersCacheEntryKeyGenerationPolicy
     */
    public DynamicCacheConfiguration(EhcacheFactory cacheFactory, CacheNameGenerationPolicy cacheNameGenerator, CacheEntryKeyGenerationPolicy keygenerationpolicy) {
        this.cacheEntryKeyGenerator = keygenerationpolicy;
        this.cacheFactory = cacheFactory;
        this.cacheNameGenerator = cacheNameGenerator;
    }

    public Ehcache getCache() {
        String cacheName = getCacheName();
        return cacheFactory.createCache(EhCacheUtils.cleanUpCacheName(cacheName));
    }

    public CacheEntryKeyGenerationPolicy getCacheEntryKeyGenerator() {
        Assert.notNull(cacheEntryKeyGenerator, "Intercepted method does not have proper Cache Entry Key generator configured..");
        return cacheEntryKeyGenerator;
    }

    public String getCacheName() {
        return cacheNameGenerator.generateCacheName();
    }

    public boolean isDynamic() {
        return true;
    }

    public String getCacheNameGeneratorPolicyString() {
        return getCacheName();
    }

    public boolean isBlocking() {
        return false;
    }

}
