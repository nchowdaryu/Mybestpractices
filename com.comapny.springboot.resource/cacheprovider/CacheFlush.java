import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.vanguard.spring.caching.Constants;

/**
 * Use this annotation to apply cache interceptor to a method invocation of
 * which will clear the entire cache.
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
 *     {@link CacheFlush CacheFlush(cache = &quot;clientsCacheConfiguration&quot;)}
 *     public Client clientModelChanged() {
 * 	// perform some logic.
 * 	// on successfull execution of this method the contents of clientCache cache will be
 * 	// cleared.
 *     }
 *     
 * }
 * </pre>
 * 
 * @author uiak
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface CacheFlush {
    
    /**
     * Name of the CachingConfiguration provided in spring configuration file. The default
     * bean will use the com.vanguard.spring.caching.aspect.DefaultCachingConfiguration to define this.
     */
    String cache() default Constants.DEFAULT_CACHING_CONFIGURATION;

    /**
     * Define the group of keys that need to removed from the cache.
     */
    String keysgroupid() default Constants.DEFAULT_KEYS_GROUP_ID;
}
