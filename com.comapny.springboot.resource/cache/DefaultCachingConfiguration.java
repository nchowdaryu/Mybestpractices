import net.sf.ehcache.Ehcache;

import org.springframework.util.Assert;

import com.company.spring.caching.CacheEntryKeyGenerationPolicy;
import com.company.spring.caching.CachingConfiguration;

public class DefaultCachingConfiguration implements CachingConfiguration {

    private CacheEntryKeyGenerationPolicy cacheEntryKeyGenerator = new PointcutParametersCacheEntryKeyGenerationPolicy();

    private Ehcache cache;

    DefaultCachingConfiguration() {
        super();
    }

    DefaultCachingConfiguration(Ehcache cache, CacheEntryKeyGenerationPolicy cacheEntryKeyGenerator) {
        this();
        this.cache = cache;
        this.cacheEntryKeyGenerator = cacheEntryKeyGenerator;
    }

    public CacheEntryKeyGenerationPolicy getCacheEntryKeyGenerator() {
        Assert.notNull(cacheEntryKeyGenerator, "Intercepted method does not have proper Cache Entry Key generator configured..");
        return cacheEntryKeyGenerator;
    }

    public Ehcache getCache() {
        return cache;
    }

    public String getCacheName() {
        return cache.getName();
    }

    public boolean isDynamic() {
        return false;
    }

    public String getCacheNameGeneratorPolicyString() {
        return "";
    }

    public boolean isBlocking() {
        return true;
    }

}
