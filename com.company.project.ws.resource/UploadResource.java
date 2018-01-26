import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.springframework.beans.factory.annotation.Autowired;

import com.company.retail.rest.ltqtnotification.notification.searchclient.NotificationExternalSearchClientResource;
import com.company.retail.rest.ltqtnotification.notification.shared.util.NotificationUtility;
import com.company.service.application.ltqtnotification.clientprofile.pojo.ClientProfileHistoryLog;
import com.company.service.application.ltqtnotification.external.batch.entity.response.BatchAcknowledgmentFileResponse;
import com.company.service.application.ltqtnotification.external.batch.pojo.AcknowledgementFileSummary;
import com.company.service.application.ltqtnotification.external.batch.service.impl.LTQTBatchFileServiceImpl;
import com.company.service.application.ltqtnotification.external.batch.util.FtpBulkUpload;
import com.company.service.application.ltqtnotification.external.pojo.AlternateContactInfo;
import com.company.service.application.ltqtnotification.external.pojo.WebUploadInfo;
import com.company.service.application.ltqtnotification.external.shared.enums.LTQTApplicationCode;
import com.company.service.application.ltqtnotification.util.LTQTNotificationUtil;
import com.company.service.application.ltqtnotification.webupload.service.LTQTWebUploadNotificationService;
import com.company.service.businessprocess.rules.ltqtnotification.util.LTQTNotificationApplicationErrorCode;
import com.company.service.businessprocess.rules.ltqtnotification.util.LTQTNotificationApplicationErrorMessage;
import com.company.service.businessprocess.rules.ltqtnotification.util.LTQTNotificationConstants;
import com.company.service.technical.logging.LogFactory;
import com.company.spring.servicelocator.SpringBeanServiceLocator;
import com.company.twowaytwofactor.process.util.Misc;

public class NotificationBulkUploadResource {
		
	@Autowired
	NotificationExternalSearchClientResource aNotificationExternalSearchClientResource;
	
	@Autowired
	LTQTBulkUploadRecieptResource aLTQTBulkUploadRecieptResource;
	
	@Autowired
	NotificationUtility aNotificationUtility;
	
	
	@Autowired
	LTQTWebUploadNotificationService aLTQTWebUploadNotificationService;
	
	@Autowired
	FtpBulkUpload ftpBulkUpload;
	
	@Autowired 
	LTQTNotificationUtil aLTQTNotificationUtil;
	

	@POST
	@Path("/externalUpload")
	@RolesAllowed({"ACCESS_LTE", "EXTERNAL_USER","ACCESS_IIO_EXTERNAL_FUNCTIONS" })
	public Response externalUploadSubmit(@FormDataParam("file") InputStream fis, 
			@FormDataParam("alternateContactInfo") FormDataBodyPart jsonPart,
			@FormDataParam("fileName") FormDataBodyPart fileNameJSONPart) throws IOException {
		String methodName = "externalUploadSubmit";
		LogFactory.getLog().logReport(methodName, " Inside externalUploadSubmit");
		jsonPart.setMediaType(MediaType.APPLICATION_JSON_TYPE);
		AlternateContactInfo alternateContactInfo = jsonPart.getValueAs(AlternateContactInfo.class);
		String fileName = fileNameJSONPart.getValueAs(String.class);
		return fileReadAndProcess(fis, fileName, alternateContactInfo);
	}

	public Response fileReadAndProcess(InputStream fis,	String fileName, AlternateContactInfo alternateContactInfo) throws IOException {
		String methodName ="fileReadAndProcess ";
		Response res = null;
		ByteArrayOutputStream baos = null;
		ByteArrayInputStream bais =null;
		BatchAcknowledgmentFileResponse aknowlegmentResponse = null;
		
		
			try {
				LogFactory.getLog().logReport(methodName, " Inside try");
				
				if(Misc.isNotNullAndEmpty(fileName) && "csv".equalsIgnoreCase(fileName.substring(fileName.lastIndexOf(".")+1, fileName.length()))){
					// To keep Input stream alive for multiple calls - Converting Byte Array and Pass
					LogFactory.getLog().logReport(methodName, " after file Name Check");
					baos = new ByteArrayOutputStream();
					org.apache.commons.io.IOUtils.copy(fis, baos);
					byte[] bytes = baos.toByteArray();
					bais = new ByteArrayInputStream(bytes);
					InputStream inputStream = new ByteArrayInputStream(bytes);//converting byte array to inputstream-FTP
					
					aknowlegmentResponse = processFileAndContactInfo(alternateContactInfo, bais, fileName,inputStream);
				}
										
		    } catch(Exception e){
		    	
		    	LogFactory.getLog().logError(methodName,LTQTNotificationApplicationErrorMessage.LTQT_WEBUPLOAD_SUBMIT_ERROR.getValue(),e,LTQTNotificationApplicationErrorCode.LTQT_WEBUPLOAD_SUBMIT_ERROR.getCode());
		    	aknowlegmentResponse = new BatchAcknowledgmentFileResponse();
				aknowlegmentResponse.setaResponseErrorCode(LTQTNotificationApplicationErrorCode.LTQT_WEBUPLOAD_SUBMIT_ERROR.toString());
				
		    } finally {
		    	if(fis != null){
		    		fis.close();
		    	}
		    	if(baos != null){
		    		baos.close();
		    	}
		       if(bais != null){
		    	   bais.close();
		   		}
		    }
			res = Response.ok(aknowlegmentResponse).build();
			return res;
	}

	private BatchAcknowledgmentFileResponse processFileAndContactInfo(AlternateContactInfo alternateContactInfo,ByteArrayInputStream bais, String fileName, InputStream inputStream) throws IOException{
		String methodName = "bulkProcessFile ";
		LogFactory.getLog().logReport(methodName, " inside bulk process");
		// as per req get only 40 character from file name and remove every thing after that.
		if(fileName.length() > 40){
			fileName = fileName.substring(0,40);
		} 
		LogFactory.getLog().logReport(methodName, " inside bulk process");
		WebUploadInfo  webUploadInfo = createWebUploadInfoObject(alternateContactInfo, fileName, bais);
		webUploadInfo = aLTQTWebUploadNotificationService.processBulkFile(webUploadInfo);
		LogFactory.getLog().logReport(methodName, " after bulk process");
		BatchAcknowledgmentFileResponse aknowlegmentResponse = new BatchAcknowledgmentFileResponse();
		AcknowledgementFileSummary acknowledgementFileSummary = new AcknowledgementFileSummary();
		acknowledgementFileSummary.setFileId(new BigDecimal(webUploadInfo.getFileId()));
		aknowlegmentResponse.setAcknowledgementFileSummary(acknowledgementFileSummary);
		
				
		//FTP
		fTPBulkUpload(fileName, webUploadInfo,inputStream); 
		
		
		if(Misc.isNotNullAndEmpty(webUploadInfo.getErrorCode())){
			aknowlegmentResponse.setaResponseErrorCode(webUploadInfo.getErrorCode());
			LogFactory.getLog().logError(methodName,LTQTNotificationApplicationErrorMessage.LTQT_WEBUPLOAD_PROCESS_ERROR.getValue(), Integer.valueOf(webUploadInfo.getErrorCode()));
		}
		else{
			LogFactory.getLog().logReport("methodName", " sendACKEmail process");
			getLTQTBatchFileServiceImpl().sendACKEmail(webUploadInfo);
			}
		return aknowlegmentResponse;
	}

	private void fTPBulkUpload(String fileName, WebUploadInfo webUploadInfo, InputStream inputStream)
			throws IOException {
		String currentDate = aLTQTNotificationUtil.getCurrentDate(LTQTNotificationConstants.DATE_FORMAT_TS);
		String FileName = fileName.replace(LTQTNotificationConstants.FILE_EXTN, currentDate);
		String extn = "_"+webUploadInfo.getFileId()+LTQTNotificationConstants.FILE_EXTN;
		webUploadInfo.setFileName(FileName.concat(extn));
		webUploadInfo.setFileInputStream(inputStream);
		ftpBulkUpload.ftpBulkUploadToSharedrive(webUploadInfo);
	}

	private WebUploadInfo createWebUploadInfoObject(AlternateContactInfo alternateContactInfo, String fileName,ByteArrayInputStream bais) {
		LogFactory.getLog().logReport("createWebUploadInfoObject", " inside createWebUploadInfoObject process");
		WebUploadInfo webUploadInfo = new WebUploadInfo();
		webUploadInfo.setFileName(fileName);
		webUploadInfo.setFileInputStream(bais);
		webUploadInfo.setAlternateContactInfo(alternateContactInfo);
		webUploadInfo.setRegisterdContactInfo(aNotificationExternalSearchClientResource.getRregisteredContactInfo());
		webUploadInfo.setUserPoId(aNotificationUtility.getUserPOID());
		webUploadInfo.setUploadSource(LTQTApplicationCode.EXTERNAL_UPLOAD.toString());
		LogFactory.getLog().logReport("createWebUploadInfoObject", " end of createWebUploadInfoObject process");
		return webUploadInfo;
	}
	
	@GET
	@Path("/uploadHistory")
	public List<ClientProfileHistoryLog> isUserFirmEnabled() {
		String poid = getNotificationUtility().getUserPOID();		
		return aLTQTWebUploadNotificationService.retrieveExternalUploadHistory(poid);

	}
	
	private LTQTBatchFileServiceImpl getLTQTBatchFileServiceImpl() {
		return SpringBeanServiceLocator.getSpringManagedBean("LTQTBatchFileService", LTQTBatchFileServiceImpl.class);
	}
	
	private NotificationUtility getNotificationUtility() {
		return SpringBeanServiceLocator.getSpringManagedBean("notificationUtility", NotificationUtility.class);
	}
}
