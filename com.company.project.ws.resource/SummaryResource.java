import java.util.Collection;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import com.company.service.application.entity.ltqtnotification.response.NotificationDefaultSummaryResponse;
import com.company.service.application.entity.ltqtnotification.response.NotificationIMComplexSummaryGroup;
import com.company.service.application.entity.ltqtnotification.response.NotificationIMComplexSummaryResponse;
import com.company.service.application.entity.ltqtnotification.response.NotificationIMFundManagers;
import com.company.service.application.entity.ltqtnotification.response.NotificationIMSummaryResponse;
import com.company.service.application.entity.ltqtnotification.response.NotificationSummaryResponse;
import com.company.service.application.ltqtnotification.download.NotificationSummaryDownloadService;
import com.company.service.application.ltqtnotification.impl.NotificationSummaryService;
import com.company.service.application.ltqtnotification.shared.enums.DateIndicator;
import com.company.service.application.ltqtnotification.shared.enums.NotificationSummaryIndicator;
import com.company.service.application.ltqtnotification.util.LTQTNotificationApplicationConstants;
import com.company.spring.servicelocator.SpringBeanServiceLocator;
import com.company.validator.constraints.SafeString;
import com.company.validator.constraints.Text;

　
　
public class NotificationSummaryResource {
	
	
	
	private String content = LTQTNotificationApplicationConstants.CONTENT_DISPOSITION; 
	
	@GET
	@Path("/notifications")
	@RolesAllowed({ "ACCESS_LTI", "CREATE_LT_QT_TRADE_NOTIFICATION", "EDIT_LT_QT_TRADE_NOTIFICATION", "APPROVE_LT_QT_TRADE_NOTIFICATION" ,"CREATE_LTQT_NOTIFICATION_FAS_CLIENT","CREATE_LTQT_NOTIFICATION_VISTA_CLIENT","CREATE_LTQT_NOTIFICATION_VAST_CLIENT","CREATE_LTQT_NOTIFICATION_ARK_CLIENT","CREATE_LTQT_NOTIFICATION_FUNDONLY_CLIENT","CREATE_LTQT_NOTIFICATION_IPA_CLIENT"})
	public NotificationDefaultSummaryResponse getNotificaitonSummary(@Valid @SafeString @DefaultValue("01") @QueryParam("searchIndicator") String searchIndicator, 
			 @Text @QueryParam("searchBeginDate") String searchBeginDate, 
			 @Text @QueryParam("searchEndDate") String searchEndDate,
			 @Text @DefaultValue("0") @QueryParam("searchClientPoid") String searchClientPoid,
			 @Text @DefaultValue("0") @QueryParam("searchFileId") String searchFileId,
			 @Text @DefaultValue("0") @QueryParam("searchLinkedGroupId") String searchLinkGroupId,
			 @Text @DefaultValue("false") @QueryParam("defaultSummary") String defaultSummary,
			 @SafeString @DefaultValue("")@QueryParam("fundNumber") String fundNumber,			 
			 @SafeString @DefaultValue("")@QueryParam("masterAccountNumber") String masterAccountNumber,
			 @SafeString @DefaultValue("")@QueryParam("accountNumber") String accountNumber,
			 @SafeString @DefaultValue("")@QueryParam("nsccId") String nsccId
			) {
		
		if("true".equalsIgnoreCase(defaultSummary)) {
			searchIndicator = "07";
		}
		return getNotificationSummaryService().retrieveNotificationSummary(searchIndicator, searchBeginDate, searchEndDate, searchClientPoid, searchFileId,searchLinkGroupId, fundNumber, masterAccountNumber, accountNumber, nsccId);
	}	
	
	//IM View Summary
	@GET
	@Path("/notificationsIM")
	@RolesAllowed({ "ACCESS_LTI", "CREATE_LT_QT_TRADE_NOTIFICATION", "EDIT_LT_QT_TRADE_NOTIFICATION", "APPROVE_LT_QT_TRADE_NOTIFICATION" ,"CREATE_LTQT_NOTIFICATION_FAS_CLIENT","CREATE_LTQT_NOTIFICATION_VISTA_CLIENT","CREATE_LTQT_NOTIFICATION_VAST_CLIENT","CREATE_LTQT_NOTIFICATION_ARK_CLIENT","CREATE_LTQT_NOTIFICATION_FUNDONLY_CLIENT","CREATE_LTQT_NOTIFICATION_IPA_CLIENT", "VIEW_LTQT_NOTIFICATION_IMG_EQUITY", "VIEW_LTQT_NOTIFICATION_IMG_FIXED_INCOME"})
	public NotificationIMSummaryResponse getIMNotificaitonSummary() {
		return getNotificationSummaryService().retrieveIMNotificationSummary();
	}
	
	@GET
	@Path("/notificationsIMComplex")
	@RolesAllowed({ "ACCESS_LTI", "CREATE_LT_QT_TRADE_NOTIFICATION", "EDIT_LT_QT_TRADE_NOTIFICATION", "APPROVE_LT_QT_TRADE_NOTIFICATION" ,"CREATE_LTQT_NOTIFICATION_FAS_CLIENT","CREATE_LTQT_NOTIFICATION_VISTA_CLIENT","CREATE_LTQT_NOTIFICATION_VAST_CLIENT","CREATE_LTQT_NOTIFICATION_ARK_CLIENT","CREATE_LTQT_NOTIFICATION_FUNDONLY_CLIENT","CREATE_LTQT_NOTIFICATION_IPA_CLIENT", "VIEW_LTQT_NOTIFICATION_IMG_EQUITY", "VIEW_LTQT_NOTIFICATION_IMG_FIXED_INCOME"})
	public NotificationIMComplexSummaryResponse getIMComplexNotificationSummary( @Text @QueryParam("dateIndicator") String dateIndicator			) {
		return getNotificationSummaryService().retrieveIMComplexNotificationSummary(dateIndicator);
	}
	
	@GET
	@Path("/notificationsIMDetails")
	@RolesAllowed({ "ACCESS_LTI", "CREATE_LT_QT_TRADE_NOTIFICATION", "EDIT_LT_QT_TRADE_NOTIFICATION", "APPROVE_LT_QT_TRADE_NOTIFICATION" ,"CREATE_LTQT_NOTIFICATION_FAS_CLIENT","CREATE_LTQT_NOTIFICATION_VISTA_CLIENT","CREATE_LTQT_NOTIFICATION_VAST_CLIENT","CREATE_LTQT_NOTIFICATION_ARK_CLIENT","CREATE_LTQT_NOTIFICATION_FUNDONLY_CLIENT","CREATE_LTQT_NOTIFICATION_IPA_CLIENT", "VIEW_LTQT_NOTIFICATION_IMG_EQUITY", "VIEW_LTQT_NOTIFICATION_IMG_FIXED_INCOME"})
	public NotificationIMComplexSummaryGroup getNotificaitonSummaryIMDetails(@Text @QueryParam("parentFundNumber") String parentFundNumber) {
		return getNotificationSummaryService().retrieveNotificationSummaryIMDetails(parentFundNumber);
	}
	

	@GET
	@Path("/fundManager")
	@RolesAllowed({ "ACCESS_LTI", "CREATE_LT_QT_TRADE_NOTIFICATION", "EDIT_LT_QT_TRADE_NOTIFICATION", "APPROVE_LT_QT_TRADE_NOTIFICATION" ,"CREATE_LTQT_NOTIFICATION_FAS_CLIENT","CREATE_LTQT_NOTIFICATION_VISTA_CLIENT","CREATE_LTQT_NOTIFICATION_VAST_CLIENT","CREATE_LTQT_NOTIFICATION_ARK_CLIENT","CREATE_LTQT_NOTIFICATION_FUNDONLY_CLIENT","CREATE_LTQT_NOTIFICATION_IPA_CLIENT", "VIEW_LTQT_NOTIFICATION_IMG_EQUITY", "VIEW_LTQT_NOTIFICATION_IMG_FIXED_INCOME"})
	public Collection<NotificationIMFundManagers> retrieveIMFundManagers(@Text @QueryParam("fundGroupCode") String fundGroupCode) {
		return getNotificationSummaryService().retrieveIMFundManagers(fundGroupCode);
	}
	
	
	

	private NotificationSummaryService getNotificationSummaryService() {
		return SpringBeanServiceLocator.getSpringManagedBean("notificationSummaryService", NotificationSummaryService.class);
	}
	
	
	//download functionality
	@GET
	@Path("/notificationsIMDetailCSV")
	@Produces("text/csv")
	@RolesAllowed({ "ACCESS_LTI", "CREATE_LT_QT_TRADE_NOTIFICATION", "EDIT_LT_QT_TRADE_NOTIFICATION", "APPROVE_LT_QT_TRADE_NOTIFICATION" ,"CREATE_LTQT_NOTIFICATION_FAS_CLIENT","CREATE_LTQT_NOTIFICATION_VISTA_CLIENT","CREATE_LTQT_NOTIFICATION_VAST_CLIENT","CREATE_LTQT_NOTIFICATION_ARK_CLIENT","CREATE_LTQT_NOTIFICATION_FUNDONLY_CLIENT","CREATE_LTQT_NOTIFICATION_IPA_CLIENT", "VIEW_LTQT_NOTIFICATION_IMG_EQUITY", "VIEW_LTQT_NOTIFICATION_IMG_FIXED_INCOME"})
	public Response getNotificationIMDetailSummaryCSV(@Text @QueryParam("parentFundNumber") String parentFundNumber) {
		
		NotificationIMComplexSummaryGroup notificationIMComplexSummaryGroup = getNotificationSummaryService().retrieveNotificationSummaryIMDetails(parentFundNumber);
		ResponseBuilder response = null;
		response = Response.ok(getNotificationSummaryDownloadService().getNotificationSummaryIMDetailCSVResponse(notificationIMComplexSummaryGroup).toString());
		response.header(content, "attachment; filename=\"Notification_IM_Detail_Summary_Today.csv\"");
		
		return response.build();
	}
	
	@GET
	@Path("/allnotificationsCSV")
	@Produces("text/csv")
	@RolesAllowed({ "ACCESS_LTI", "CREATE_LT_QT_TRADE_NOTIFICATION", "EDIT_LT_QT_TRADE_NOTIFICATION", "APPROVE_LT_QT_TRADE_NOTIFICATION" ,"CREATE_LTQT_NOTIFICATION_FAS_CLIENT","CREATE_LTQT_NOTIFICATION_VISTA_CLIENT","CREATE_LTQT_NOTIFICATION_VAST_CLIENT","CREATE_LTQT_NOTIFICATION_ARK_CLIENT","CREATE_LTQT_NOTIFICATION_FUNDONLY_CLIENT","CREATE_LTQT_NOTIFICATION_IPA_CLIENT"})
	public Response getAllNotificationSummaryCSV(@Valid @SafeString @DefaultValue("01") @QueryParam("searchIndicator") String searchIndicator, 
			 @Text @QueryParam("searchBeginDate") String searchBeginDate, 
			 @Text @QueryParam("searchEndDate") String searchEndDate, @Text @QueryParam("filters") String filters,
			 @Text @DefaultValue("0") @QueryParam("searchClientPoid") String searchClientPoid,
			 @Text @DefaultValue("0") @QueryParam("searchFileId") String searchFileId,
			 @Text @DefaultValue("0") @QueryParam("searchLinkedGroupId") String searchLinkGroupId,
			 @Text @DefaultValue("false") @QueryParam("defaultSummary") String defaultSummary,
			 @SafeString @DefaultValue("")@QueryParam("fundNumber") String fundNumber,			 
			 @SafeString @DefaultValue("")@QueryParam("masterAccountNumber") String masterAccountNumber,
			 @SafeString @DefaultValue("")@QueryParam("accountNumber") String accountNumber,
			 @SafeString @DefaultValue("")@QueryParam("nsccId") String nsccId
			 ){
		
		if("true".equalsIgnoreCase(defaultSummary)) {
			searchIndicator = "07";
		}
		NotificationDefaultSummaryResponse aNotificationDefaultSummaryResponse = getNotificationSummaryService().retrieveNotificationSummary(searchIndicator, searchBeginDate, searchEndDate , searchClientPoid ,searchFileId , searchLinkGroupId,fundNumber, masterAccountNumber, accountNumber, nsccId);
		Collection<NotificationSummaryResponse> notificationSummaryResponses = aNotificationDefaultSummaryResponse.getaNotificationSummaryResponse();
		ResponseBuilder response = null;
		response = Response.ok(getNotificationSummaryDownloadService().getAllCSVResponse(notificationSummaryResponses, filters, searchIndicator).toString());
		response.header(content, "attachment; filename=\"Notification_summary.csv\"");		
		return response.build();
	}
	
	@GET
	@Path("/notificationsIMSummaryCSV")
	@Produces("text/csv")
	@RolesAllowed({ "ACCESS_LTI", "CREATE_LT_QT_TRADE_NOTIFICATION", "EDIT_LT_QT_TRADE_NOTIFICATION", "APPROVE_LT_QT_TRADE_NOTIFICATION" ,"CREATE_LTQT_NOTIFICATION_FAS_CLIENT","CREATE_LTQT_NOTIFICATION_VISTA_CLIENT","CREATE_LTQT_NOTIFICATION_VAST_CLIENT","CREATE_LTQT_NOTIFICATION_ARK_CLIENT","CREATE_LTQT_NOTIFICATION_FUNDONLY_CLIENT","CREATE_LTQT_NOTIFICATION_IPA_CLIENT", "VIEW_LTQT_NOTIFICATION_IMG_EQUITY", "VIEW_LTQT_NOTIFICATION_IMG_FIXED_INCOME"})
	public Response getNotificationIMSummaryCSV() {
		
		NotificationIMSummaryResponse notificationIMSummaryResponse = getNotificationSummaryService().retrieveIMNotificationSummary();
		ResponseBuilder response = null;
		response = Response.ok(getNotificationSummaryDownloadService().getIMNotificationSummayCSVResponse(notificationIMSummaryResponse).toString());
		response.header(content, "attachment; filename=\"Notification_IM_Summary.csv\"");
		
		return response.build();
	}
	
	@GET
	@Path("/notificationsIMCSV")
	@Produces("text/csv")
	@RolesAllowed({ "ACCESS_LTI", "CREATE_LT_QT_TRADE_NOTIFICATION", "EDIT_LT_QT_TRADE_NOTIFICATION", "APPROVE_LT_QT_TRADE_NOTIFICATION" ,"CREATE_LTQT_NOTIFICATION_FAS_CLIENT","CREATE_LTQT_NOTIFICATION_VISTA_CLIENT","CREATE_LTQT_NOTIFICATION_VAST_CLIENT","CREATE_LTQT_NOTIFICATION_ARK_CLIENT","CREATE_LTQT_NOTIFICATION_FUNDONLY_CLIENT","CREATE_LTQT_NOTIFICATION_IPA_CLIENT", "VIEW_LTQT_NOTIFICATION_IMG_EQUITY", "VIEW_LTQT_NOTIFICATION_IMG_FIXED_INCOME"})
	public Response getNotificationIMSummaryCSV(@Text @QueryParam("dateIndicator") String dateIndicator, @Text @QueryParam("fundGroupCodeFilter") String fundGroupCodeFilter ,
			 @Text @QueryParam("fundManagerFilter") String fundManagerFilter ,@Text @QueryParam("statusFilter") String statusFilter ,
			 @Text @QueryParam("businessOwnerFilter") String businessOwnerFilter , @Text @QueryParam("fundSubGroupFilter") String fundSubGroupFilter, @Text @QueryParam("processingMethodFilter") String processingMethodFilter) {
		
		NotificationIMComplexSummaryResponse notificationIMComplexSummaryResponse = getNotificationSummaryService().retrieveIMComplexNotificationSummary(dateIndicator);
		ResponseBuilder response = null;
		response = Response.ok(getNotificationSummaryDownloadService().getIMComplexNotificationSummary(notificationIMComplexSummaryResponse, fundGroupCodeFilter, fundManagerFilter, statusFilter, businessOwnerFilter, fundSubGroupFilter, processingMethodFilter).toString());
		if(dateIndicator.equalsIgnoreCase(DateIndicator.CURRENT.toString())) {
			response.header(content, "attachment; filename=\"LTQT_Notifications_Current.csv\"");
		}
		else if(dateIndicator.equalsIgnoreCase(DateIndicator.PAST.toString())) {
			response.header(content, "attachment; filename=\"LTQT_Notifications_Previous.csv\"");
		}
		else {
			response.header(content, "attachment; filename=\"LTQT_Notifications_Future.csv\"");
		}
				
		return response.build();
	}
	

	
	@GET
	@Path("/notificationsCSV")
	@Produces("text/csv")
	@RolesAllowed({ "ACCESS_LTI", "CREATE_LT_QT_TRADE_NOTIFICATION", "EDIT_LT_QT_TRADE_NOTIFICATION", "APPROVE_LT_QT_TRADE_NOTIFICATION" ,"CREATE_LTQT_NOTIFICATION_FAS_CLIENT","CREATE_LTQT_NOTIFICATION_VISTA_CLIENT","CREATE_LTQT_NOTIFICATION_VAST_CLIENT","CREATE_LTQT_NOTIFICATION_ARK_CLIENT","CREATE_LTQT_NOTIFICATION_FUNDONLY_CLIENT","CREATE_LTQT_NOTIFICATION_IPA_CLIENT"})
	public Response getNotificaitonSummaryCSV(@Valid @SafeString @DefaultValue("01") @QueryParam("searchIndicator") String searchIndicator, 
			 @Text @QueryParam("filterAll") String filterAll,
			 @Text @QueryParam("searchBeginDate") String searchBeginDate, 
			 @Text @DefaultValue("0") @QueryParam("searchClientPoid") String searchClientPoid,
			 @Text @DefaultValue("0") @QueryParam("searchFileId") String searchFileId,
			 @Text @DefaultValue("0") @QueryParam("searchLinkedGroupId") String searchLinkGroupId,
			 @Text @QueryParam("searchEndDate") String searchEndDate,
			 @SafeString @DefaultValue("")@QueryParam("fundNumber") String fundNumber,			 
			 @SafeString @DefaultValue("")@QueryParam("masterAccountNumber") String masterAccountNumber,
			 @SafeString @DefaultValue("")@QueryParam("accountNumber") String accountNumber,
			 @SafeString @DefaultValue("")@QueryParam("nsccId") String nsccId
			 ) {
		
		NotificationDefaultSummaryResponse aNotificationDefaultSummaryResponse = getNotificationSummaryService().retrieveNotificationSummary(searchIndicator, searchBeginDate, searchEndDate , searchClientPoid , searchFileId , searchLinkGroupId,fundNumber, masterAccountNumber, accountNumber, nsccId);
		Collection<NotificationSummaryResponse> notificationSummaryResponses = aNotificationDefaultSummaryResponse.getaNotificationSummaryResponse();
		ResponseBuilder response = null;
		response = Response.ok(getNotificationSummaryDownloadService().getCSVResponse(notificationSummaryResponses, searchIndicator ,filterAll).toString());
		if(searchIndicator.equalsIgnoreCase(NotificationSummaryIndicator.EXTERNAL_COMUNICATION.toString())) {
			response.header(content, "attachment; filename=\"ExternalCommunication_Summary.csv\"");
		}
		else if(searchIndicator.equalsIgnoreCase(NotificationSummaryIndicator.SDQA_VIEW.toString())) {
			response.header(content, "attachment; filename=\"SDQA_Summary.csv\"");
		}
		else {
			response.header(content, "attachment; filename=\"Notification_Summary.csv\"");
		}
				
		return response.build();
	}
	
	private NotificationSummaryDownloadService getNotificationSummaryDownloadService() {
		return SpringBeanServiceLocator.getSpringManagedBean("notificationSummaryDownloadService", NotificationSummaryDownloadService.class);
		
	}

}
