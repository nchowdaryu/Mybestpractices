import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.vanguard.spring.caching.Constants;

/**
 * 
 * This annotation should be used to apply cache entry removal interceptor for
 * method. This annotation uses the cache configuration to determine the cache
 * in which the entry is stored and the key generator to generate the cache
 * entry key.
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
 *     // This configuration will remove the entry populated in the cache by the find method
 *     // in the same class. Also, it is required that the Client object's toString method returns the 
 *     // client identifier used by the find method.
 *     
 *     // Instead of the prefix, a hard coded key can be used for both the cache load and the cache entry removal method.
 *     
 *     {@link SingleKeyCacheFlush @SingleKeyCacheFlush(cache = &quot;clientsCacheConfiguration&quot;,prefix=&quot;DbClientAccountService.find&amp;quot}
 *     public Client update(Client client) {
 * 	// Perform DAO operation to persist Client to DB.
 * // The entry to be removed from the cache needs to be stored with a key of  
 * 	// DbClientAccountService.find.{to String value of aClientIdentifier}
 *     }
 *     
 * </pre>
 * 
 * @author uiak
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface SingleKeyCacheFlush {

    /**
     * Name of the CachingConfiguration provided in spring configuration file. The default bean 
     * will use the com.vanguard.spring.caching.aspect.DefaultCachingConfiguration to define this.
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
     * Prefix the dynamically generated key or hard coded key with this key
     * group. This is used in creating and removing a set of cache entries.
     */
    String keysgroupid() default Constants.DEFAULT_KEYS_GROUP_ID;

}
