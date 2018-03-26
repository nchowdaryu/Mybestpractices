import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "icap:transaction", propOrder={ "notificationAmt", "inkindCashAmt",
		"sharesEntered", "currency", "transType", "transTypeIndicator", "processingMethod", "nsccSetleId"
})
public class ICAPSNotificationTransactionDetails {
	
	@XmlElement(name = "dollar-amount", required=true)
	private BigDecimal notificationAmt;
	
	@XmlElement(name = "cash-amount")
	private BigDecimal inkindCashAmt;
	
	@XmlElement(name = "shares")
	private BigDecimal sharesEntered;
	
	@XmlElement(name = "iso-currency", required=true)
	private String currency = "USD";	 
	
	@XmlElement(name = "transaction-type", required=true)
	@XmlSchemaType(name = "icap:transactionType")
	private String transType;
	
	@XmlElement(name = "transaction-type-indicator")
	@XmlSchemaType(name = "icap:transactionTypeIndicator")
	private String transTypeIndicator;
	
	@XmlElement(name = "processing-method")
	private String processingMethod;
	
	@XmlElement(name = "nscc-settlement-number")
	private String nsccSetleId;
