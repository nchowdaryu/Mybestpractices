import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.company.fas.literature.webservice.dao.api.LiteratureDao;
import com.company.fas.literature.webservice.dao.storedprocedure.RetreiveAllLiteratureStoredProcedure;
import com.company.fas.literature.webservice.domain.AllLiteratureDataEntity;
import com.company.fas.literature.webservice.domain.LiteratureDeliveryMethods;
import com.company.fas.literature.webservice.domain.LiteratureDetails;
import com.company.fas.literature.webservice.domain.LiteratureFunds;
import com.company.fas.literature.webservice.domain.LiteraturePackage;
import com.company.logging.aspect.Loggable;

@Component
public class LiteratureDaoImpl implements LiteratureDao {
	
	@Autowired
	RetreiveAllLiteratureStoredProcedure aRetreiveAllLiteratureStoredProcedure;
	
	private final Logger logger = LoggerFactory.getLogger(LiteratureDaoImpl.class);

	@Override
	@SuppressWarnings("unchecked")
	@Loggable(enablePerformanceLogging=true, performanceSystem="fas-literature.webservice-dao")
	public AllLiteratureDataEntity getAllFundsLiterature() {
		
		AllLiteratureDataEntity allLiterature = new AllLiteratureDataEntity();
		
		//Call SP & get the Results
		Map<String, Object> fundLiteratureSPParams = aRetreiveAllLiteratureStoredProcedure.buildParameters();
		Map<String, Object> results = aRetreiveAllLiteratureStoredProcedure.execute(fundLiteratureSPParams);		
		
		logger.info("Executed Literature Retreival Stored Procedure and Size of Results is: {}",results.size());
		
		List<LiteratureFunds> litFundsList = (List<LiteratureFunds>) results.get(RetreiveAllLiteratureStoredProcedure.LIT_FUNDS_ROW_MAPPER);
		List<LiteratureDetails> litDetailsList = (List<LiteratureDetails>) results.get(RetreiveAllLiteratureStoredProcedure.LIT_DETAILS_ROW_MAPPER);		
		List<LiteratureDeliveryMethods> litDeliveryMethodsList = (List<LiteratureDeliveryMethods>) results.get(RetreiveAllLiteratureStoredProcedure.LIT_DELIVARY_METHODS_ROW_MAPPER);
		List<LiteraturePackage> litPackagesList = (List<LiteraturePackage>) results.get(RetreiveAllLiteratureStoredProcedure.LIT_PACKAGE_ROW_MAPPER);
		
		logger.info("Literaure Funds, Details, Delivery Methods and Packages Results List Size are: {}, {}, {}, {}",litFundsList.size(), litDetailsList.size(), litDeliveryMethodsList.size(), litPackagesList.size() );
		
		allLiterature.setLitFundsList(litFundsList);
		allLiterature.setLitDetailsList(litDetailsList);		
		allLiterature.setLitDeliveryMethodsList(litDeliveryMethodsList);
		allLiterature.setLitPackagesList(litPackagesList);
		
		return allLiterature;
	}

}
