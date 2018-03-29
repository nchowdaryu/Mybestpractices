import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.vanguard.spring.caching.Constants;

/**
 * @author uiak
 * This annotation should be used to include method arguments toString method
 * in generating a cache key.
 * E.g.
 * <br><pre>
 * public Object simpleMethod(@IncludeForCacheKey String arg1, @IncludeForCacheKey String arg2)
 * </pre><br>
 * In the above example, both the method argument's toString methods would be used to generate a key. And 
 * it will use the toString method output on the argument objects.
 * 
 * The following example is a variation.
 * <br><pre>
 * {@link Cacheable @Cacheable(cache = &quot;clientsCacheConfiguration&quot;}
 * public Fund find(@IncludeForCacheKey("getRequestedFundId") FundFindRequest anRequest) {
 *     	return find(1); 
 * }
 * </pre><p>
 * The above configuration will invoke a method called getRequestedFundId on the FundFindRequest
 * parameter object.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
@Inherited
public @interface IncludeForCacheKey {
    
    /**
     * @return String representing the method name on the annotated argument
     * that needs to be invoked to create the cache key value. If this method returns the default
     * the toString method will be invoked.
     */
    String value() default Constants.DEFAULT_KEY_GEN_METHOD_NAME;
    
    /**
     * Optional attribute to disable sorting. By default set to false, enabling sorting 
     *  used if annotated argument is of type List<? extends Comparable>
     */
    boolean disableSort() default false;
}
