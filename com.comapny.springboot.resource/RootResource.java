import javax.ws.rs.Path;
import javax.ws.rs.container.ResourceContext;
import javax.ws.rs.core.Context;

@Path("/rest/")
public class LiteratureRootResource {
	
	@Context
	ResourceContext resourceContext;
	
	@Path("public/literature")
	public FundLiteratureDetailResource getFundLiteratureDetailResource() {
		return resourceContext.getResource(FundLiteratureDetailResource.class);
	}

}

import javax.annotation.security.PermitAll;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;

import com.vanguard.fas.literature.webservice.domain.response.FundLiteratureDetailsRestResponse;
import com.vanguard.fas.literature.webservice.domain.response.LiteratureDetailsRestResponse;
import com.vanguard.fas.literature.webservice.service.LiteratureService;
import com.vanguard.validator.constraints.Numeric;

@PermitAll

@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
public class FundLiteratureDetailResource {	
	
	@Autowired
	LiteratureService aLiteratureService;	
	
	
	@Path("/details/{fundId}")
	@GET
	public FundLiteratureDetailsRestResponse retrieveFundLiteratureDetails(@PathParam("fundId") @NotNull @Numeric String fundId){
		
		return aLiteratureService.getLiteratureDetails(fundId);	
		
	}
}

import java.util.List;

import com.company.fas.literature.webservice.domain.LiteratureDetails;

public class FundLiteratureDetailsRestResponse {
	
	List<LiteratureDetails> literatureDetails;

	public List<LiteratureDetails> getLiteratureDetails() {
		return literatureDetails;
	}

	public void setLiteratureDetails(List<LiteratureDetails> literatureDetails) {
		this.literatureDetails = literatureDetails;
	}	

}

import java.util.Map;

public class LiteratureDetails {
	
	private String literatureId;	
	private String literatureTypeCode;
	private String vendorLiteratureId;
	private String literatureName;
	private String literatureDesc;
	private String effectiveStartDate;
	private String effectiveEndDate;
	private String asOfDate;	
	private Map<String, LiteratureAvailability> litAvailability;
	private boolean internalOnlyIndicator;
	private String defaultPackagePDFFileURL;	
  //getters/setters
