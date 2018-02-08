import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.inject.Inject;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.commons.lang.StringUtils;
import org.springframework.mail.javamail.JavaMailSender;

//configure SMTP server in spring config file.
<bean id="mailSender" class="org.springframework.mail.javamail.JavaMailSenderImpl">
        <property name="host" value="$application-project-{SMTP_SERVER}"/>
    </bean>
public class EmailServiceUtil {
	
	static final String FROM = "test@test.com";
	
	@Inject
	JavaMailSender mailSender;
	
	public void sendEmail(String commaDelimitedEmailAddresses, String subject, String body, File attachment) throws MessagingException, IOException {
		MimeMessage msgMime = mailSender.createMimeMessage();
		
		TimeZone.setDefault(TimeZone.getTimeZone("EST"));
		
		List<InternetAddress> emailAddresses = new ArrayList<InternetAddress>();		
		for(String emailAddress : StringUtils.split(commaDelimitedEmailAddresses, ",")) {
			emailAddresses.add(new InternetAddress(emailAddress));
		}
		msgMime.setRecipients(Message.RecipientType.TO, emailAddresses.toArray(new InternetAddress[emailAddresses.size()]));
		msgMime.setFrom(new InternetAddress(FROM));			
		msgMime.setSubject(subject);
		msgMime.setSentDate(new Date());
		
		if(attachment == null) {
			msgMime.setText(body);
		}
		else {
			Multipart multipart = new MimeMultipart();

			MimeBodyPart textPart = new MimeBodyPart();
			textPart.setText(body);
			
			MimeBodyPart attachmentPart = new MimeBodyPart();
			attachmentPart.attachFile(attachment);
			
			multipart.addBodyPart(textPart);
			multipart.addBodyPart(attachmentPart);
			
			msgMime.setContent(multipart);
		}
		
		mailSender.send(msgMime);
	}

}
