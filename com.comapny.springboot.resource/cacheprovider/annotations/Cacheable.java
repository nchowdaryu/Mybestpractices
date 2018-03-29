import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.vanguard.spring.caching.Constants;

/**
 * This annotation should be used to apply caching interceptor for a method on a
 * concrete class. The interceptor caches the output value of the method. The
 * key used to store the return value in cache is generated based on the key
 * generation policy defined in the CacheConfiguration.
 * 
 * The default key generator uses the method's class name, method name and the
 * arguments toString value of the method.
 * 
 * <p>
 * Example:
 * 
 * <pre>
 * <hr>
 * Cache Configuration:
 * 
 *  &lt;!--  Abstract bean defs.. applications need to inherit from the abstract configuration --&gt;
 *  &lt;bean id=&quot;clientsCache&quot;  parent=&quot;cache-provider-cache-config&quot;&gt;
 *  &lt;property name=&quot;cacheName&quot; value=&quot;clientsCache&quot; /&gt;
 *  &lt;/bean&gt;
 * 
 *  &lt;!-- Uses component scaned key generator --&gt;
 *  &lt;bean id=&quot;clientsCacheConfiguration&quot; class=&quot;com.vanguard.spring.caching.aspect.DefaultCachingConfiguration&quot;&gt;
 *  &lt;!-- This should be same as the cacheName attribute used for cache definition above. --&gt;
 *  &lt;constructor-arg value=&quot;clientsCache&quot;/&gt;
 *  &lt;constructor-arg ref=&quot;pointcutParametersCacheEntryKeyGenerationPolicy&quot;/&gt;
 *  &lt;/bean&gt;
 * <hr>
 * // Uses the Cache Configuration shown above.
 * public class DbClientAccountService implements ClientAccountService {
 *     
 *     {@link Cacheable @Cacheable(cache = &quot;clientsCacheConfiguration&quot;}
 *     public Client find(long aClientIdentifier) {
 * 	// Perform DAO operation and return Client.
 * // The Client object will be cached with a key of 
 * 	// DbClientAccountService.find.{to String value of aClientIdentifier}
 *     }
 *     
 *     {@link Cacheable @Cacheable(cache = &quot;clientsCacheConfiguration&quot;,key = &quot;SOME_HARDCODED_KEY&quot;}
 *     public Client find(long aClientIdentifier) {
 * 	// Perform DAO operation and return Client.
 * 	// The Client object will be cached with a key of 
 * 	// SOME_HARDCODED_KEY
 * 	// and ignores the class name, method name and the arguments
 *     }
 *     
 *     {@link Cacheable @Cacheable(cache = &quot;clientsCacheConfiguration&quot;,prefix = &quot;SOME_KEY_PREFIX&quot;}
 *     public Client find(long aClientIdentifier) {
 * 	// Perform DAO operation and return Client.
 * 	// The Client object will be cached with a key of 
 * 	// SOME_KEY_PREFIX.{the toString value of aClientIdentifier}
 * 	// Uses the prefix and the toString value of the arguments
 * 	// to generate the cache key
 *     }
 * }
 * </pre>
 * 
 * @author uiak
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Cacheable {

    
    /**
     * Name of the CachingConfiguration provided in spring configuration file. The default
     * bean will use the com.vanguard.spring.caching.aspect.DefaultCachingConfiguration to define this.
     */
    String cache() default Constants.DEFAULT_CACHING_CONFIGURATION;

    /**
     * Use this key for the cache storage instead of dynamically generated key.
     */
    String key() default Constants.DEFAULT_KEY;

    /**
     * Use this prefix along with the method arguments to generate the cache entry key.
     */
    String keyprefix() default Constants.DEFAULT_PREFIX;
    
    /**
     * Prefix the dynamically generated key or hard coded key with this key group. This 
     * is used in creating and removing a set of cache entries.
     */
    String keysgroupid() default Constants.DEFAULT_KEYS_GROUP_ID;
    
    /**
     * CacheNullValue flag, default to true  
     */
    boolean cacheNullValue() default true;
}
