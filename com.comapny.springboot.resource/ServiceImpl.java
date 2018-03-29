import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.company.fas.literature.webservice.dao.api.LiteratureDao;
import com.company.fas.literature.webservice.domain.AllFundsLiteratureDetails;
import com.company.fas.literature.webservice.domain.AllLiteratureDataEntity;
import com.company.fas.literature.webservice.domain.LiteratureDetails;
import com.company.fas.literature.webservice.domain.response.FundLiteratureDetailsRestResponse;
import com.company.fas.literature.webservice.domain.response.LiteratureDetailsRestResponse;
import com.company.fas.literature.webservice.service.helper.LiteratureDataHelper;
import com.company.fas.literature.webservice.service.util.LiteratureCacheUtil;

@Service
public class LiteratureServiceImpl implements LiteratureService {
	
	@Autowired
	LiteratureCacheUtil literatureCacheUtil;
	
	@Autowired
	LiteratureDao aLiteratureDao;
	
	@Autowired
	LiteratureDataHelper aLiteratureDataHelper;
	
	private final Logger logger = LoggerFactory.getLogger(LiteratureServiceImpl.class);
	
	@Override
	public LiteratureDetailsRestResponse getFundLiteratureDetails(String fundId) {
		LiteratureDetailsRestResponse aLiteratureDetailsRestResponse = new LiteratureDetailsRestResponse();
		
		Map<String, List<LiteratureDetails>> fundsLiteratureDetailsResultsMap = new HashMap<>();	
		
		AllLiteratureDataEntity allLiterature = aLiteratureDao.getAllFundsLiterature();
		
		logger.info("Mapping Literature Results to Rest Response.");
		
		List<LiteratureDetails> fundLitDetailsList = aLiteratureDataHelper.mapLiteratureDetails(fundId, allLiterature);
		
		fundsLiteratureDetailsResultsMap.put(fundId, fundLitDetailsList);
		
		aLiteratureDetailsRestResponse.setLiteratureDetailsMap(fundsLiteratureDetailsResultsMap);
		
		return aLiteratureDetailsRestResponse;
	}
	
	@Override
	public FundLiteratureDetailsRestResponse getLiteratureDetails(String fundId){
		FundLiteratureDetailsRestResponse fundLiteratureDetailsRestResponse = new FundLiteratureDetailsRestResponse();
		//Get it from Cache
		List<LiteratureDetails> fundLitDetails = literatureCacheUtil.getLiteratureByFund(fundId);
		
		//If not available in Cache, get it from database and update the cache.
		if(fundLitDetails == null){
			AllLiteratureDataEntity allLiterature = aLiteratureDao.getAllFundsLiterature();
			AllFundsLiteratureDetails allFundsLitDetails = aLiteratureDataHelper.getAllFundsLiteratureDetails(allLiterature);
			literatureCacheUtil.setAllFundsLiterature(allFundsLitDetails);
			fundLitDetails = literatureCacheUtil.getLiteratureByFund(fundId);
		}
		
		fundLiteratureDetailsRestResponse.setLiteratureDetails(fundLitDetails);
		
		return fundLiteratureDetailsRestResponse;
	}
	

}
