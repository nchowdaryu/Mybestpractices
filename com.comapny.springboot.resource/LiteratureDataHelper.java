import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.company.fas.literature.webservice.dao.impl.LiteratureDaoImpl;
import com.company.fas.literature.webservice.domain.AllFundsLiteratureDetails;
import com.company.fas.literature.webservice.domain.AllLiteratureDataEntity;
import com.company.fas.literature.webservice.domain.LiteratureAvailability;
import com.company.fas.literature.webservice.domain.LiteratureDeliveryMethods;
import com.company.fas.literature.webservice.domain.LiteratureDetails;
import com.company.fas.literature.webservice.domain.LiteratureFunds;
import com.company.fas.literature.webservice.domain.LiteraturePackage;
import com.company.fas.literature.webservice.util.LiteratureDeliveryTypeEnum;
import com.company.fas.literature.webservice.util.LiteratureDomainConstants;

@Component
public class LiteratureDataHelper {
	
	private final Logger logger = LoggerFactory.getLogger(LiteratureDaoImpl.class);

	public List<LiteratureDetails> mapLiteratureDetails(String fundId, AllLiteratureDataEntity allLit){

		List<LiteratureDetails> fundLitDetailsList = null;
		List<LiteratureFunds> allFundsLitList =  allLit.getLitFundsList();
		List<LiteratureDetails> allLitDetailsList = allLit.getLitDetailsList();	
		List<LiteratureDeliveryMethods> allLitDeliveryMethodsList = allLit.getLitDeliveryMethodsList();
		List<LiteraturePackage> allLitPackageList = allLit.getLitPackagesList();
		
		
		//Separate unique Literature Ids for given Fund.
		Set<String> fundLitIDsSet = new HashSet<>();		
		
		for(LiteratureFunds item: allFundsLitList ){
			if(fundId.equalsIgnoreCase(item.getFundId())){
				fundLitIDsSet.add(item.getLiteratureId()); 
			}
		}		
		
		logger.info("No.of Literature Ids availble for given Fund:{} is {}",fundId, fundLitIDsSet.size());
		
		if(fundLitIDsSet.isEmpty()){
			logger.warn("There is No Literature available for given Fund:{}",fundId);
			return fundLitDetailsList;
		}
		
		// Iterate through All Literature Details List for fund specific Literature Id's List and get corresponding Fund specific LiteratureDetails objects.		
		fundLitDetailsList = allLitDetailsList.stream().filter(litDetail -> fundLitIDsSet.contains(litDetail.getLiteratureId())).collect(Collectors.toList());
		logger.info("Fund Specific Literature Details List Size is {}", fundLitDetailsList.size());		

		// Iterate through All Literature Details List for fund specific Literature Id's List and get corresponding Fund specific LiteratureDeliveryMethods objects.		

		List<LiteratureDeliveryMethods> fundLitDeliveryMethodsList = allLitDeliveryMethodsList.stream().filter(litDeliveryMethod -> fundLitIDsSet.contains(litDeliveryMethod.getLiteratureId())).collect(Collectors.toList());
		logger.info("Fund Specific Literature Ids Delivery Methods List Size is {}", fundLitDeliveryMethodsList.size());				
		
		//Separate the Literature packages List based on Default Indicator Flag.
		List<LiteraturePackage> defaultPackagesList = allLitPackageList.stream().filter(litPackage -> litPackage.getDefaultPackageIndicator().equals(LiteratureDomainConstants.FLAG_YES)).collect(Collectors.toList());		
		logger.info("Default Packages List Size is {}", defaultPackagesList.size());
		
		// Iterate through Default Literature Packages List for fund specific Literature Id's List and get corresponding Fund specific Default LiteraturePackages objects.		
		List<LiteraturePackage> fundLitDefaultPackagesList = defaultPackagesList.stream().filter(litDefaultPackage -> fundLitIDsSet.contains(litDefaultPackage.getLiteratureId())).collect(Collectors.toList());
		logger.info("Fund Specific Literature Ids Default Packages List Size is {}", fundLitDefaultPackagesList.size());
		
		setFundSpecificLiteratureAvailablityAndPDFUrl(fundLitDetailsList, fundLitDeliveryMethodsList, fundLitDefaultPackagesList);

		return fundLitDetailsList;

	}
	
	
	public AllFundsLiteratureDetails getAllFundsLiteratureDetails(AllLiteratureDataEntity allLit){
		AllFundsLiteratureDetails allFundsLiteratureDetails = new AllFundsLiteratureDetails();
		Map<String, List<LiteratureDetails>> fundsLiteratureDetailsResultsMap = new HashMap<>();
		List<LiteratureFunds> allFundsLitList =  allLit.getLitFundsList();
		//Separate unique Literature Ids for given Fund.
		Set<String> fundIDsSet = new HashSet<>();
		for(LiteratureFunds item: allFundsLitList ){
			fundIDsSet.add(item.getFundId());
		}
		
		//Iterate through Each unique fund and separate corresponding Literature Details.
		
		for(String fundNumnber: fundIDsSet){
			List<LiteratureDetails> fundLitDetails = mapLiteratureDetails(fundNumnber, allLit);
			fundsLiteratureDetailsResultsMap.put(fundNumnber, fundLitDetails);
		}
		
		allFundsLiteratureDetails.setLiteratureDetailsMap(fundsLiteratureDetailsResultsMap);
		
		return allFundsLiteratureDetails;
	}

	private void setFundSpecificLiteratureAvailablityAndPDFUrl(List<LiteratureDetails> inputFundLitDetailsList, List<LiteratureDeliveryMethods> inputFundLitDeliveryList, List<LiteraturePackage> inputFundLitDefaultPackagesList){

		//Separate the List based on External Flag.
		List<LiteratureDeliveryMethods> fundLitDeliveryMethodsListExternal = inputFundLitDeliveryList.stream().filter(litDeliveryMethod -> litDeliveryMethod.getExternalFlag().equals(LiteratureDomainConstants.FLAG_YES)).collect(Collectors.toList());		
		logger.info("Fund Specific Literature Ids Delivery Methods EXTERNAL List Size is {}", fundLitDeliveryMethodsListExternal.size());
		
		List<LiteratureDeliveryMethods> fundLitDeliveryMethodsListInternal = inputFundLitDeliveryList.stream().filter(litDeliveryMethod -> litDeliveryMethod.getExternalFlag().equals(LiteratureDomainConstants.FLAG_NO)).collect(Collectors.toList());	
		logger.info("Fund Specific Literature Ids Delivery Methods INTERNAL List Size is {}", fundLitDeliveryMethodsListInternal.size());
		
		Map<String, LiteratureAvailability> litAvailabilityMap = new HashMap<>();

		for(LiteratureDetails litDetail:inputFundLitDetailsList ){

			//separate Delivery Methods list based on Literature Id
			
			litAvailabilityMap = findLiteratureAvailbility(fundLitDeliveryMethodsListExternal, litAvailabilityMap, LiteratureDomainConstants.EXTERNAL, litDetail);
			litAvailabilityMap = findLiteratureAvailbility(fundLitDeliveryMethodsListInternal, litAvailabilityMap, LiteratureDomainConstants.INTERNAL, litDetail);			
			litDetail.setLitAvailability(litAvailabilityMap);
			//Set is internalOnly on litDetail object based on litAvialability
			
			if(litAvailabilityMap.size()==0){
				litDetail.setInternalOnlyIndicator(false);
			}else{
				setLitInternalOnlyIndicator(litDetail, litAvailabilityMap);
			}			
			
			//Set default package PDF URL
			//Get the corresponding LiteratureDefaultPackage object based on current literature Id.
			LiteraturePackage litPackage = inputFundLitDefaultPackagesList.stream().filter(fundLitDefaultPackage -> fundLitDefaultPackage.getLiteratureId().equals(litDetail.getLiteratureId())).findFirst().get();
			
			litDetail.setDefaultPackagePDFFileURL(litPackage.getPdfFileName());
			
		}


	}

	private Map<String, LiteratureAvailability> findLiteratureAvailbility(List<LiteratureDeliveryMethods> inputFundLitDeliveryList,
			Map<String, LiteratureAvailability> litAvailabilityMap, String type, LiteratureDetails litDetail) {
		List<String> myList = new ArrayList<>();
		List<LiteratureDeliveryMethods> specificLitIdsList = inputFundLitDeliveryList.stream().filter(litDeliveryMethod -> litDeliveryMethod.getLiteratureId().equalsIgnoreCase(litDetail.getLiteratureId())).collect(Collectors.toList());
		for(LiteratureDeliveryMethods litDeliveyMethod: specificLitIdsList){
			myList.add(litDeliveyMethod.getDeliveryType());
		}
		LiteratureAvailability litAvialability = setLitAvailbility(myList);
		litAvailabilityMap.put(type, litAvialability);		

		return litAvailabilityMap;
	}

	private LiteratureAvailability setLitAvailbility(List<String> inputList){
		LiteratureAvailability litAvialability = new LiteratureAvailability();
		for(String deliveryMethod: inputList){

			if(LiteratureDeliveryTypeEnum.EMAIL.getValue().equalsIgnoreCase(deliveryMethod)){
				litAvialability.setAvailbleForEmailDelivery(true);
			}
			if(LiteratureDeliveryTypeEnum.MAIL.getValue().equalsIgnoreCase(deliveryMethod)){
				litAvialability.setAvailbleForMailDelivery(true);
			}
			if(LiteratureDeliveryTypeEnum.ONLINE_VIEW.getValue().equalsIgnoreCase(deliveryMethod)){
				litAvialability.setAvailbleForOnlineView(true);
			}
			if(LiteratureDeliveryTypeEnum.ONLINE_PROCESS.getValue().equalsIgnoreCase(deliveryMethod)){
				litAvialability.setAvailbleForOnlineProcess(true);
			}
			if(LiteratureDeliveryTypeEnum.PRINT_ON_DEMAND.getValue().equalsIgnoreCase(deliveryMethod)){
				litAvialability.setAvailbleForPOD(true);
			}

		}
		return litAvialability;
	}

	private void setLitInternalOnlyIndicator(LiteratureDetails litDetail, Map<String, LiteratureAvailability> litAvailabilityMap ){		
		boolean internalOnly = true;
		
		for(Map.Entry<String, LiteratureAvailability> entry: litAvailabilityMap.entrySet()){

			if(LiteratureDomainConstants.EXTERNAL.equalsIgnoreCase(entry.getKey())){
				LiteratureAvailability litAvial = entry.getValue();
				if(litAvial.isAvailbleForEmailDelivery() ||  litAvial.isAvailbleForMailDelivery() || litAvial.isAvailbleForOnlineView()){
					internalOnly = false;					
					break;
				}

			}else if(LiteratureDomainConstants.INTERNAL.equalsIgnoreCase(entry.getKey())){
				LiteratureAvailability litAvial = entry.getValue();
				if(litAvial.isAvailbleForEmailDelivery() ||  litAvial.isAvailbleForMailDelivery() || litAvial.isAvailbleForOnlineView()){
					internalOnly = true;
				}
			}

		}

		litDetail.setInternalOnlyIndicator(internalOnly);	

	}
}
