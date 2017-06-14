package com.unisystems.alpha.poc.apimanagment.integration.processor.validator;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.w3c.dom.Document;

import com.unisystems.alpha.poc.apimanagment.integration.exception.InvalidRequestException;

public class TransferRequestValidateProcessor implements Processor {

	@Override
	public void process(Exchange exchange) throws Exception {
		
		String xml = exchange.getIn().getBody(String.class);
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		DocumentBuilder builder;
		Document doc = null;

		builder = factory.newDocumentBuilder();
		
		doc = builder.parse(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));
		
		XPathFactory xpathFactory = XPathFactory.newInstance();
        XPath xpath = xpathFactory.newXPath();

        /*[name()='List']/*[name()='Fields']/*[name()='Field']*/
        String execDate =  xpath.evaluate("/*[name()='CustomerCreditTransferInitiationV03']/*[name()='PmtInf']/*[name()='ReqdExctnDt']/text()", doc, XPathConstants.STRING).toString();
        String amount =  xpath.evaluate("/*[name()='CustomerCreditTransferInitiationV03']/*[name()='PmtInf']/*[name()='CdtTrfTxInf']/*[name()='Amt']/*[name()='InstdAmt']/text()", doc, XPathConstants.STRING).toString();
        String toAccountId =  xpath.evaluate("/*[name()='CustomerCreditTransferInitiationV03']/*[name()='PmtInf']/*[name()='CdtTrfTxInf']/*[name()='CdtrAcct']/*[name()='Id']/*[name()='IBAN']/text()", doc, XPathConstants.STRING).toString();
        
        if(execDate==null || execDate.trim().length()==0 || execDate.contains("${") ||
           amount==null || amount.trim().length()==0 || amount.contains("${") ||
           toAccountId==null || toAccountId.trim().length()==0 || toAccountId.contains("${")) {
        	
        	exchange.getIn().setBody("OBP-10001: json value, value.amount, to, to.id, request_excecution_date are required input fields");			
			throw new InvalidRequestException("OBP-10001: json value, value.amount, to, to.id, request_excecution_date are required input fields");
        }
        	

        SimpleDateFormat dateSdf = new SimpleDateFormat("yyyy-MM-dd");
        
        try {
        	
            
        	if(dateSdf.parse(execDate).before(dateSdf.parse(dateSdf.format(new Date()))))
        			throw new Exception("");
        	
        } catch (ParseException e) {
        	exchange.getIn().setBody("OBP-10001:request_excecution_date field format should be yyyy-MM-dd or yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
			throw new InvalidRequestException("OBP-10001:request_excecution_date field format should be yyyy-MM-dd or yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        } catch (Exception e) {
        	exchange.getIn().setBody("OBP-10001:not valid value for field request_excecution_date ");
			throw new InvalidRequestException("OBP-10001:not valid value for field request_excecution_date");
        }
        
        try {
        	new BigDecimal(amount);
        } catch (Exception e) {
        	exchange.getIn().setBody("OBP-10001: not valid value for field value.amount");
			throw new InvalidRequestException("OBP-10001: not valid value for field value.amount");
        }
	}

}
