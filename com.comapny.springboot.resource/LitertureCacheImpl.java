import java.util.List;
import java.util.Map;

import com.company.fas.literature.webservice.domain.AllFundsLiteratureDetails;
import com.company.fas.literature.webservice.domain.LiteratureDetails;
import com.company.fas.literature.webservice.util.LiteratureDomainConstants;
import com.company.spring.caching.support.EhcacheFactory;

import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;

public class LiteratureCacheUtil {
	
	protected EhcacheFactory cacheFactory;

	public LiteratureCacheUtil(){
		//Adding this as a default constructor
	}
	
	public LiteratureCacheUtil(EhcacheFactory cacheFactory) {		
		this.cacheFactory = cacheFactory;
	}
	
	protected Ehcache getLiteratureCache() {
		return cacheFactory.createCache(LiteratureDomainConstants.CACHE_NAME);
	}
	
	public AllFundsLiteratureDetails getAllFundsLiterature(){
		
		Ehcache allFundsLiteratureCache = getLiteratureCache();
		Element element = allFundsLiteratureCache.get(LiteratureDomainConstants.CACHE_KEY);
		return element == null ? null : (AllFundsLiteratureDetails) element.getObjectValue();
		
	}
	
	public void setAllFundsLiterature(AllFundsLiteratureDetails allFundsLiteratureDetails){
		
		Ehcache allFundsLiteratureCache = getLiteratureCache();
		synchronized(allFundsLiteratureCache) {
			Element element = getAllFundsLiteratureElement(allFundsLiteratureDetails);
			element.setTimeToLive(LiteratureDomainConstants.CACHE_TIME_TO_LIVE);
			allFundsLiteratureCache.put(element);
		}
		
	}
	
	protected Element getAllFundsLiteratureElement(AllFundsLiteratureDetails allFundsLiteratureDetails) {
		return new Element(LiteratureDomainConstants.CACHE_KEY, allFundsLiteratureDetails);
	}
	
	public List<LiteratureDetails> getLiteratureByFund(String fundNumber){
		AllFundsLiteratureDetails allFundsLit = getAllFundsLiterature();
		if(null!=allFundsLit){
			Map<String, List<LiteratureDetails>> fundLitMap = allFundsLit.getLiteratureDetailsMap();
			return fundLitMap != null ? fundLitMap.get(fundNumber) : null;
		}
		return null;
		
	}
}

import java.util.List;
import java.util.Map;

public class AllFundsLiteratureDetails {
	
	private Map<String, List<LiteratureDetails>> literatureDetailsMap;

	public Map<String, List<LiteratureDetails>> getLiteratureDetailsMap() {
		return literatureDetailsMap;
	}

	public void setLiteratureDetailsMap(Map<String, List<LiteratureDetails>> literatureDetailsMap) {
		this.literatureDetailsMap = literatureDetailsMap;
	}

}


public class LiteratureDomainConstants {	
	
	public static final String FLAG_NO = "N";
	public static final String FLAG_YES = "Y";
	public static final String INTERNAL = "INTERNAL";
	public static final String EXTERNAL = "EXTERNAL";
	public static final String CACHE_NAME = "literatureCache";
	public static final String CACHE_KEY = "allLiterature";
	public static final int CACHE_TIME_TO_LIVE = 82800; //23 hours	

}
