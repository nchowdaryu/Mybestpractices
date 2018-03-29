import net.sf.ehcache.Ehcache;

/**
 * @author uiak
 * 
 */
public interface CachingConfiguration extends CacheConfiguration {

	public Ehcache getCache();
}
