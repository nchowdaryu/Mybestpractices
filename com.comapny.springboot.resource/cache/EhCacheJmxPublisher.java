import javax.management.MBeanServer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MarkerFactory;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.management.ManagementService;

/**
 * @author uiak Utility class to register the EHCache Mbeans through Spring context.
 */
public class EhCacheJmxPublisher {

	private static final Logger LOGGER = LoggerFactory.getLogger(EhCacheJmxPublisher.class);

    private static CacheManager mcacheManager;

    private static MBeanServer mbeanServer;

    public final static void init() {
        ManagementService.registerMBeans(mcacheManager, EhCacheJmxPublisher.mbeanServer, false, false, false, true);
        LOGGER.info(MarkerFactory.getMarker("report"), "Started the JMX Cache monitor");
    }

    public CacheManager getCacheManager() {
        return mcacheManager;
    }

    public void setCacheManager(CacheManager mcacheManager) {
        EhCacheJmxPublisher.mcacheManager = mcacheManager;
    }

    public void setMbeanServer(MBeanServer mbeanServer) {
        EhCacheJmxPublisher.mbeanServer = mbeanServer;
    }
}
