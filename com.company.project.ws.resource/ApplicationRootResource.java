import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.container.ResourceContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import com.company.retail.rest.ltqtnotification.admin.AdminThresholdResource;
import com.company.retail.rest.ltqtnotification.admin.FundAdministrationResource;
import com.company.retail.rest.ltqtnotification.admin.LTQTGlobalAdminDataResource;
import com.company.retail.rest.ltqtnotification.clientfilemgmt.LTQTClientsFileMangementDataResource;
import com.company.retail.rest.ltqtnotification.clientprofile.ClientProfileResource;
import com.company.retail.rest.ltqtnotification.fima2.Fima2Resource;
import com.company.retail.rest.ltqtnotification.multipleupdate.MultipleUpdateResource;
import com.company.retail.rest.ltqtnotification.notification.avt.NotificationAVTResource;
import com.company.retail.rest.ltqtnotification.notification.bulkUpload.BulkUploadInternalResource;
import com.company.retail.rest.ltqtnotification.notification.bulkUpload.LTQTInternalBulkUploadReceiptResource;
import com.company.retail.rest.ltqtnotification.notification.bulkUpload.NotificationBulkUploadResource;
import com.company.retail.rest.ltqtnotification.notification.detail.NotificationDetailResource;
import com.company.retail.rest.ltqtnotification.notification.fund.NotificationFundResource;
import com.company.retail.rest.ltqtnotification.notification.linking.NotificationsLinkingGroupResource;
import com.company.retail.rest.ltqtnotification.notification.searchclient.NotificationSearchClientResource;
import com.company.retail.rest.ltqtnotification.notification.shared.NotificationSharedResource;
import com.company.retail.rest.ltqtnotification.notification.summary.NotificationSummaryResource;

　
@Path("/lti")
@Produces({ MediaType.APPLICATION_XML,  MediaType.APPLICATION_JSON , MediaType.TEXT_PLAIN})
@Consumes({ MediaType.APPLICATION_XML,  MediaType.APPLICATION_JSON })
public class NotificationRootResource {
	
	@Context
	ResourceContext resourceContext;
	
	
	@Path("/summary")
	public NotificationSummaryResource getNotificationSummary() {
		return resourceContext.getResource(NotificationSummaryResource.class);
	}
	
	@Path("/captureMaintainShared")
	public NotificationDetailResource getNotificationDetail() {
		return resourceContext.getResource(NotificationDetailResource.class);
	}
	
	@Path("/searchClient")
	public NotificationSearchClientResource getNotificationSearchClientResource() {
		return resourceContext.getResource(NotificationSearchClientResource.class);
	}
	
	@Path("/avt")
	public NotificationAVTResource getNotificationAVTResource() {
		return resourceContext.getResource(NotificationAVTResource.class);
	}
	
	@Path("/fund")
	public NotificationFundResource getNotificationFundResource() {
		return resourceContext.getResource(NotificationFundResource.class);
	}
	
	@Path("/notificationShared")
	public NotificationSharedResource getNotificationSharedResource(){
		return resourceContext.getResource(NotificationSharedResource.class);
	}
	
	@Path("/fima2")
	public Fima2Resource getFima2Resource(){
		return resourceContext.getResource(Fima2Resource.class);
	}

	@Path("/fundAdministration")
	public FundAdministrationResource getAdminSharedResource(){
		return resourceContext.getResource(FundAdministrationResource.class);
	}

	
	@Path("/threshold")
	public AdminThresholdResource getAdminThresholdResource(){
		return resourceContext.getResource(AdminThresholdResource.class);
	}
	
	@Path("/globalData")
	public LTQTGlobalAdminDataResource getLTQTGlobalAdminDataResource(){
		return resourceContext.getResource(LTQTGlobalAdminDataResource.class);
	}
	
	@Path("/clientsFileMgmt")
	public LTQTClientsFileMangementDataResource getLTQTClientsFileMangementDataResource(){
		return resourceContext.getResource(LTQTClientsFileMangementDataResource.class);
	}
	
	@Path("/clientProfile")
	public ClientProfileResource getClientProfileResource(){
		return resourceContext.getResource(ClientProfileResource.class);
	}
	
	@Path("/bulkUpload")
	public NotificationBulkUploadResource submitBulkUpload(){
		return resourceContext.getResource(NotificationBulkUploadResource.class);
	}
	
	@Path("/upload")
	public BulkUploadInternalResource submitInternalBulkUpload(){
		return resourceContext.getResource(BulkUploadInternalResource.class);
	}
	
	@Path("/internal")
	public LTQTInternalBulkUploadReceiptResource getFileUploadSummary(){
		return resourceContext.getResource(LTQTInternalBulkUploadReceiptResource.class);
	}	
	
	@Path("/multipleUpdate")
	public MultipleUpdateResource getMultipleUpdateResource(){
		return resourceContext.getResource(MultipleUpdateResource.class);
	}
	
	@Path("/linking")
	public NotificationsLinkingGroupResource getNotificationsLinkingGroupResource(){
		return resourceContext.getResource(NotificationsLinkingGroupResource.class);
	}
}
